package service;

import palakfrontend.QuizQuestion;

import java.util.ArrayList;
import java.util.List;

/**
 * PDF extraction and MCQ generation only — no UI, no database, no score updates.
 */
public class PdfQuizGenerationService {

    private static final int MAX_TEXT_LENGTH = 3000;
    private static final int MAX_QUESTIONS = 10;

    public static String extractTextFromPdf(String filePath) {
        String pdfText = PDFReader.readPDF(filePath);
        if (pdfText == null || pdfText.isEmpty()) {
            return "";
        }
        if (pdfText.length() > MAX_TEXT_LENGTH) {
            pdfText = pdfText.substring(0, MAX_TEXT_LENGTH);
        }
        return pdfText;
    }

    public static List<QuizQuestion> generateQuestionsFromText(String pdfText, String difficulty) {
        if (pdfText == null || pdfText.isBlank()) {
            return List.of();
        }

        String response = ChatbotService.generateMcqFromPdfText(pdfText, difficulty);
        if (response == null || response.isBlank()
                || response.startsWith("Error") || response.startsWith("API")) {
            return List.of();
        }

        List<QuizQuestion> questions = parseMcqResponseToList(response);
        if (questions.isEmpty()) {
            questions = parseChatbotFormatToList(response);
        }
        if (questions.isEmpty()) {
            return List.of();
        }
        return questions.subList(0, Math.min(MAX_QUESTIONS, questions.size()));
    }

    public static List<QuizQuestion> generateFromPdf(String filePath, String difficulty) {
        return generateQuestionsFromText(extractTextFromPdf(filePath), difficulty);
    }

    private static List<QuizQuestion> parseMcqResponseToList(String text) {
        List<QuizQuestion> list = new ArrayList<>();
        int num = 0;
        String[] blocks = text.split("(?i)Q\\s*:");
        for (String block : blocks) {
            if (block.trim().isEmpty()) {
                continue;
            }
            try {
                String[] lines = block.split("\\r?\\n");
                List<String> clean = new ArrayList<>();
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        clean.add(line.trim());
                    }
                }
                if (clean.size() < 6) {
                    continue;
                }

                String question = stripOptionPrefix(clean.get(0));
                String o1 = stripOptionPrefix(clean.get(1));
                String o2 = stripOptionPrefix(clean.get(2));
                String o3 = stripOptionPrefix(clean.get(3));
                String o4 = stripOptionPrefix(clean.get(4));
                String ans = clean.get(5).replaceAll("(?i)Answer\\s*:\\s*", "").trim();
                int correct = answerToInt(ans);

                list.add(new QuizQuestion(++num, question, o1, o2, o3, o4, correct));
            } catch (Exception ignored) {
            }
        }
        return list;
    }

    private static List<QuizQuestion> parseChatbotFormatToList(String text) {
        List<QuizQuestion> list = new ArrayList<>();
        int num = 0;
        String[] blocks = text.split("(?i)Question\\s*:");
        for (String block : blocks) {
            if (block.trim().isEmpty()) {
                continue;
            }
            try {
                String question = "";
                String o1 = "";
                String o2 = "";
                String o3 = "";
                String o4 = "";
                int correct = 1;

                for (String line : block.split("\n")) {
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    if (question.isEmpty() && !line.startsWith("Option")) {
                        question = line;
                    } else if (line.startsWith("Option1:")) {
                        o1 = line.replace("Option1:", "").trim();
                    } else if (line.startsWith("Option2:")) {
                        o2 = line.replace("Option2:", "").trim();
                    } else if (line.startsWith("Option3:")) {
                        o3 = line.replace("Option3:", "").trim();
                    } else if (line.startsWith("Option4:")) {
                        o4 = line.replace("Option4:", "").trim();
                    } else if (line.startsWith("CorrectOption:")) {
                        correct = answerToInt(line.replace("CorrectOption:", "").trim());
                    }
                }

                if (!question.isEmpty() && !o1.isEmpty()) {
                    list.add(new QuizQuestion(++num, question, o1, o2, o3, o4, correct));
                }
            } catch (Exception ignored) {
            }
        }
        return list;
    }

    private static String stripOptionPrefix(String line) {
        return line.replaceFirst("^[A-Da-d1-4][\\.\\)]\\s*", "").trim();
    }

    private static int answerToInt(String ans) {
        if (ans == null) {
            return 1;
        }
        ans = ans.trim();
        if (ans.equalsIgnoreCase("A") || ans.equals("1") || ans.equalsIgnoreCase("Option1")) {
            return 1;
        }
        if (ans.equalsIgnoreCase("B") || ans.equals("2") || ans.equalsIgnoreCase("Option2")) {
            return 2;
        }
        if (ans.equalsIgnoreCase("C") || ans.equals("3") || ans.equalsIgnoreCase("Option3")) {
            return 3;
        }
        if (ans.equalsIgnoreCase("D") || ans.equals("4") || ans.equalsIgnoreCase("Option4")) {
            return 4;
        }
        try {
            int n = Integer.parseInt(ans);
            if (n >= 1 && n <= 4) {
                return n;
            }
        } catch (NumberFormatException ignored) {
        }
        return 1;
    }
}
