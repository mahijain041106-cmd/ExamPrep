package service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Gemini study assistant (standalone logic). Used by {@link palakfrontend.FloatingChatbot}
 * on the student dashboard only.
 */
public class ChatbotStandalone {

    private static final String API_KEY = System.getenv("GEMINI_API_KEY");
    private static final StringBuilder chatHistory = new StringBuilder();

    public static String getResponse(String userInput) {
        if (API_KEY == null || API_KEY.isBlank()) {
            return "API error: Set GEMINI_API_KEY environment variable (Gemini API key).";
        }

        try {
            String endpoint = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key="
                    + API_KEY;

            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String prompt = "You are a helpful teacher.\n"
                    + "Default: short answers (2-3 lines).\n"
                    + "If user says 'explain', 'detail', or 'why', then give detailed answer.\n\n"
                    + chatHistory
                    + "\nStudent: " + userInput;

            JSONObject request = new JSONObject()
                    .put("contents", new JSONArray()
                            .put(new JSONObject()
                                    .put("parts", new JSONArray()
                                            .put(new JSONObject()
                                                    .put("text", prompt)))));

            try (OutputStream os = conn.getOutputStream()) {
                os.write(request.toString().getBytes());
                os.flush();
            }

            BufferedReader br;
            if (conn.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String line;
            StringBuilder response = new StringBuilder();
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            String botReply = extractText(response.toString());

            chatHistory.append("\nStudent: ").append(userInput);
            chatHistory.append("\nTeacher: ").append(botReply);

            return botReply;

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private static String extractText(String jsonResponse) {
        try {
            JSONObject obj = new JSONObject(jsonResponse);

            if (obj.has("error")) {
                return "API Error: " + obj.getJSONObject("error").getString("message");
            }

            return obj
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

        } catch (Exception e) {
            return "Parsing error: " + e.getMessage();
        }
    }

    /** Console entry point for testing outside JavaFX. */
    public static void main(String[] args) {
        java.util.Scanner sc = new java.util.Scanner(System.in);
        System.out.println("AI Teacher Chatbot (type 'exit' to stop)\n");
        while (true) {
            System.out.print("You: ");
            String userInput = sc.nextLine();
            if (userInput.equalsIgnoreCase("exit")) {
                break;
            }
            System.out.println("Bot: " + getResponse(userInput) + "\n");
        }
        sc.close();
    }
}
