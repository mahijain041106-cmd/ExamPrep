package palakfrontend;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddQuestionPage {

    public void start(Stage stage) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField questionField = new TextField();
        TextField o1 = new TextField();
        TextField o2 = new TextField();
        TextField o3 = new TextField();
        TextField o4 = new TextField();
        TextField subjectField = new TextField();
        TextField topicField = new TextField();
        ComboBox<String> correctBox = new ComboBox<>();
        correctBox.getItems().addAll("1", "2", "3", "4");
        correctBox.setValue("1");
        ComboBox<String> difficultyBox = new ComboBox<>();
        difficultyBox.getItems().addAll("easy", "medium", "hard");
        difficultyBox.setValue("easy");

        grid.add(new Label("Question:"), 0, 0);
        grid.add(questionField, 1, 0);
        grid.add(new Label("Option 1:"), 0, 1);
        grid.add(o1, 1, 1);
        grid.add(new Label("Option 2:"), 0, 2);
        grid.add(o2, 1, 2);
        grid.add(new Label("Option 3:"), 0, 3);
        grid.add(o3, 1, 3);
        grid.add(new Label("Option 4:"), 0, 4);
        grid.add(o4, 1, 4);
        grid.add(new Label("Correct (1-4):"), 0, 5);
        grid.add(correctBox, 1, 5);
        grid.add(new Label("Subject:"), 0, 6);
        grid.add(subjectField, 1, 6);
        grid.add(new Label("Topic:"), 0, 7);
        grid.add(topicField, 1, 7);
        grid.add(new Label("Difficulty:"), 0, 8);
        grid.add(difficultyBox, 1, 8);

        Label msg = new Label();

        Button saveBtn = new Button("Add Question");
        saveBtn.setOnAction(e -> {
            try {
                QuizService.addQuestion(
                        questionField.getText().trim(),
                        o1.getText().trim(),
                        o2.getText().trim(),
                        o3.getText().trim(),
                        o4.getText().trim(),
                        Integer.parseInt(correctBox.getValue()),
                        subjectField.getText().trim(),
                        topicField.getText().trim(),
                        difficultyBox.getValue()
                );
                msg.setText("Question added successfully.");
                msg.setStyle("-fx-text-fill: green;");
            } catch (Exception ex) {
                msg.setText("Error: " + ex.getMessage());
                msg.setStyle("-fx-text-fill: red;");
            }
        });

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> stage.close());

        VBox root = new VBox(15, new Label("Add Question Manually"), grid, saveBtn, msg, backBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 550, 520));
        stage.setTitle("Add Question");
        stage.show();
    }
}
