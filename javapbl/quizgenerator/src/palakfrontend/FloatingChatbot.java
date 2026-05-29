package palakfrontend;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import service.ChatbotStandalone;

/**
 * Floating chat on the student dashboard only — powered by {@link ChatbotStandalone} (Gemini).
 */
public final class FloatingChatbot {

    private static Stage chatStage;

    private FloatingChatbot() {}

    /** Adds the Ask AI button; use only on {@link StudentDashboard}. */
    public static StackPane wrapForStudentDashboard(javafx.scene.Parent content) {
        StackPane root = new StackPane(content);
        if (SessionManager.isLoggedIn() && !SessionManager.isAdmin()) {
            Button fab = new Button("Ask AI");
            fab.setStyle(
                    "-fx-background-color: #4f46e5; -fx-text-fill: white; "
                            + "-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-font-weight: bold; "
                            + "-fx-background-radius: 28; -fx-padding: 14 22; -fx-cursor: hand; "
                            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 12, 0, 0, 3);"
            );
            fab.setOnAction(e -> openChatWindow(fab.getScene() != null ? (Stage) fab.getScene().getWindow() : null));
            StackPane.setAlignment(fab, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(fab, new Insets(0, 28, 28, 0));
            fab.toFront();
            root.getChildren().add(fab);
        }
        return root;
    }

    public static void openChatWindow(Stage owner) {
        if (chatStage != null && chatStage.isShowing()) {
            chatStage.toFront();
            return;
        }

        Label header = new Label("PrepGenius Study Assistant (Gemini)");
        header.setStyle(
                "-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 15px;"
        );

        VBox messages = new VBox(8);
        messages.setPadding(new Insets(10));
        appendBot(messages, "Hi! Ask me anything about your subjects or exams.");

        ScrollPane scroll = new ScrollPane(messages);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(320);
        scroll.setStyle("-fx-background: white;");

        TextField input = new TextField();
        input.setPromptText("Type your question...");
        input.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
        HBox.setHgrow(input, Priority.ALWAYS);

        Button send = new Button("Send");
        send.setStyle(
                "-fx-background-color: #4f46e5; -fx-text-fill: white; -fx-cursor: hand; "
                        + "-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-font-weight: bold;"
        );

        Runnable doSend = () -> {
            String text = input.getText().trim();
            if (text.isEmpty()) {
                return;
            }
            input.clear();
            appendUser(messages, text);
            Label loading = new Label("PrepGenius is typing...");
            loading.setStyle("-fx-text-fill: #6b7280; -fx-font-style: italic;");
            messages.getChildren().add(loading);
            scroll.setVvalue(1.0);

            send.setDisable(true);
            input.setDisable(true);

            Task<String> task = new Task<>() {
                @Override
                protected String call() {
                    return ChatbotStandalone.getResponse(text);
                }
            };
            task.setOnSucceeded(ev -> {
                messages.getChildren().remove(loading);
                String reply = task.getValue();
                if (reply == null || reply.isBlank()) {
                    reply = "No response from Gemini. Check GEMINI_API_KEY and internet.";
                }
                appendBot(messages, reply);
                send.setDisable(false);
                input.setDisable(false);
                Platform.runLater(() -> scroll.setVvalue(1.0));
            });
            task.setOnFailed(ev -> {
                messages.getChildren().remove(loading);
                appendBot(messages, "Error: " + task.getException().getMessage());
                send.setDisable(false);
                input.setDisable(false);
            });
            new Thread(task, "gemini-chat").start();
        };

        send.setOnAction(e -> doSend.run());
        input.setOnAction(e -> doSend.run());

        HBox inputRow = new HBox(8, input, send);
        inputRow.setAlignment(Pos.CENTER_LEFT);

        BorderPane pane = new BorderPane();
        pane.setTop(header);
        pane.setCenter(scroll);
        pane.setBottom(inputRow);
        BorderPane.setMargin(header, new Insets(0, 0, 8, 0));
        BorderPane.setMargin(inputRow, new Insets(8, 0, 0, 0));
        pane.setPadding(new Insets(12));

        chatStage = new Stage();
        if (owner != null) {
            chatStage.initOwner(owner);
        }
        chatStage.initModality(Modality.NONE);
        chatStage.initStyle(StageStyle.UTILITY);
        chatStage.setTitle("PrepGenius Chat");
        chatStage.setScene(new javafx.scene.Scene(pane, 420, 450));
        chatStage.setOnHidden(e -> chatStage = null);
        chatStage.show();
    }

    private static void appendUser(VBox box, String text) {
        Label lbl = new Label("You: " + text);
        lbl.setWrapText(true);
        lbl.setMaxWidth(380);
        lbl.setStyle(
                "-fx-font-family: 'Segoe UI'; -fx-font-size: 13px; "
                        + "-fx-background-color: #e0e7ff; -fx-padding: 8; -fx-background-radius: 8;"
        );
        box.getChildren().add(lbl);
    }

    private static void appendBot(VBox box, String text) {
        Label lbl = new Label("PrepGenius: " + text);
        lbl.setWrapText(true);
        lbl.setMaxWidth(380);
        lbl.setStyle(
                "-fx-font-family: 'Segoe UI'; -fx-font-size: 13px; "
                        + "-fx-background-color: #f3f4f6; -fx-padding: 8; -fx-background-radius: 8;"
        );
        box.getChildren().add(lbl);
    }
}
