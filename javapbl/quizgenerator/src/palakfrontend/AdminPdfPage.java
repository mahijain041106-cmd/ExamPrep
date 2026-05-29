package palakfrontend;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class AdminPdfPage {

    public void start(Stage stage) {
        Label status = new Label("Select a PDF file. Questions will be generated via chatbot and saved.");
        ComboBox<String> difficultyBox = new ComboBox<>();
        difficultyBox.getItems().addAll("easy", "medium", "hard");
        difficultyBox.setValue("medium");

        Button uploadBtn = new Button("Choose PDF & Upload");
        uploadBtn.setOnAction(e -> {
            if (!SessionManager.isAdmin()) {
                status.setText("Only admin can upload PDF questions.");
                return;
            }
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = chooser.showOpenDialog(stage);
            if (file != null) {
                status.setText("Processing PDF... please wait.");
                String result = QuizService.processPdfForAdmin(file.getAbsolutePath(), difficultyBox.getValue());
                status.setText(result);
            }
        });

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> stage.close());

        VBox root = new VBox(15, new Label("Upload PDF"), difficultyBox, uploadBtn, status, backBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        stage.setScene(new Scene(root, 500, 300));
        stage.setTitle("PDF Upload");
        stage.show();
    }
}
