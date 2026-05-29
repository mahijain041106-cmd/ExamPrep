import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.*;

public class ChatbotStandalone {

    private static final String API_KEY = "AIzaSyBeYJBqNhDQsNXpPglU5_yvurGXC0xNuS8";

    static StringBuilder chatHistory = new StringBuilder();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("AI Teacher Chatbot (type 'exit' to stop)\n");

        while (true) {
            System.out.print("You: ");
            String userInput = sc.nextLine();

            if (userInput.equalsIgnoreCase("exit")) break;

            String response = getResponse(userInput);
            System.out.println("Bot: " + response + "\n");
        }

        sc.close();
    }

    public static String getResponse(String userInput) {
        try {
            String endpoint = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=" + API_KEY;

            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String prompt = "You are a helpful teacher.\n"
            + "Default: short answers (2-3 lines).\n"
            + "If user says 'explain', 'detail', or 'why', then give detailed answer.\n\n"
            + chatHistory.toString()
            + "\nStudent: " + userInput;

            JSONObject request = new JSONObject()
                    .put("contents", new JSONArray()
                            .put(new JSONObject()
                                    .put("parts", new JSONArray()
                                            .put(new JSONObject()
                                                    .put("text", prompt)))));

            OutputStream os = conn.getOutputStream();
            os.write(request.toString().getBytes());
            os.flush();

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
            return "Parsing error: " + jsonResponse;
        }
    }
}