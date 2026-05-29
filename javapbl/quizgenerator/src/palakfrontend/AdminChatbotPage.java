package palakfrontend;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminChatbotPage {

    public void start(Stage stage) {
        TextField topicField = new TextField();
        topicField.setPromptText("Topic e.g. Java, DBMS");
        ComboBox<String> difficultyBox = new ComboBox<>();
        difficultyBox.getItems().addAll("easy", "medium", "hard");
        difficultyBox.setValue("medium");
        TextField countField = new TextField("5");
        Label status = new Label();

        Button generateBtn = new Button("Generate & Save to Database");
        generateBtn.setOnAction(e -> {
            try {
                int count = Integer.parseInt(countField.getText().trim());
                status.setText("Generating... please wait.");
                String result = QuizService.generateAndStoreFromChatbot(
                        topicField.getText().trim(),
                        difficultyBox.getValue(),
                        count
                );
                status.setText(result);
            } catch (NumberFormatException ex) {
                status.setText("Enter a valid number of questions.");
            }
        });

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> stage.close());

        VBox root = new VBox(12,
                new Label("Generate Questions via Chatbot"),
                new Label("Topic:"), topicField,
                new Label("Difficulty:"), difficultyBox,
                new Label("Number of questions:"), countField,
                generateBtn, status, backBtn
        );
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(25));

        stage.setScene(new Scene(root, 480, 350));
        stage.setTitle("Admin Chatbot");
        stage.show();
    }
}
