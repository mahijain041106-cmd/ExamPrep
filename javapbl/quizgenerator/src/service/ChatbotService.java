package service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ChatbotService {

    // Safely reads the key from your computer environment variables
    private static final String API_KEY = System.getenv("OPENAI_API_KEY"); 
    
    // OpenAI Chat Completions Gateway
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public static String generateQuiz(String topic, String difficulty, int numberOfQuestions) {
        String prompt =
                "Generate " + numberOfQuestions + " MCQ quiz questions on " + topic
                        + ". Difficulty: " + difficulty + ".\n\n"
                        + "Format STRICTLY:\n\n"
                        + "Question: ...\n"
                        + "Option1: ...\n"
                        + "Option2: ...\n"
                        + "Option3: ...\n"
                        + "Option4: ...\n"
                        + "CorrectOption: ...\n\n";
        return callOpenAI(prompt);
    }

    /** Convert PDF text into MCQs (Q:/A./B./C./D./Answer format). */
    public static String generateMcqFromPdfText(String pdfText) {
        return generateMcqFromPdfText(pdfText, "medium");
    }

    public static String generateMcqFromPdfText(String pdfText, String difficulty) {
        String level = difficulty == null || difficulty.isBlank() ? "medium" : difficulty;
        String prompt =
                "Convert the following study material into MCQ quiz questions.\n"
                        + "Difficulty: " + level + ".\n"
                        + "STRICTLY follow this format:\n"
                        + "Q: question\nA. option\nB. option\nC. option\nD. option\nAnswer: X\n\n"
                        + pdfText;
        return callOpenAI(prompt);
    }

    /** Student study help – general Q&A (not MCQ generation). */
    public static String askStudyAssistant(String userMessage) {
        String prompt =
                "You are PrepGenius, a friendly AI tutor for exam preparation. "
                        + "Answer clearly in 2–5 short sentences. Help with concepts, definitions, and study tips. "
                        + "If unsure, say so.\n\n"
                        + "Student question: " + userMessage;
        return callOpenAI(prompt);
    }

    private static String callOpenAI(String prompt) {
        if (API_KEY == null || API_KEY.isBlank()) {
            return "API error: OpenAI API Key missing! Set the OPENAI_API_KEY environment variable.";
        }

        try {
            HttpClient client = HttpClient.newHttpClient();

            // Structure message object for OpenAI payload specification
            JsonObject messageObject = new JsonObject();
            messageObject.addProperty("role", "user");
            messageObject.addProperty("content", prompt);

            JsonArray messagesArray = new JsonArray();
            messagesArray.add(messageObject);

            // Construct payload configuration targeting gpt-4o-mini
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", "gpt-4o-mini");
            requestBody.add("messages", messagesArray);
            requestBody.addProperty("temperature", 0.7);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(requestBody)))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject jsonResponse = new Gson().fromJson(response.body(), JsonObject.class);

            // Handle API level errors thrown back by OpenAI gateway
            if (jsonResponse.has("error")) {
                return "API error: "
                        + jsonResponse.getAsJsonObject("error").get("message").getAsString();
            }

            // Extract content path: choices[0].message.content
            String rawContent = jsonResponse.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();

            // Strip out markdown block wraps (```text, ```json, ```) that disrupt the string split parsers
            if (rawContent != null) {
                rawContent = rawContent.replaceAll("```[a-zA-Z]*\\r?\\n?", "").replace("```", "").trim();
            }

            return rawContent;

        } catch (Exception e) {
            e.printStackTrace();
            return "Could not reach OpenAI: " + e.getMessage();
        }
    }
}