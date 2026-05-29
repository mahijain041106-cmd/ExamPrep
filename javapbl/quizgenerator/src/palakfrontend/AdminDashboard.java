package palakfrontend;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class AdminDashboard extends Application {

    @Override
    public void start(Stage stage) {

        Label title = new Label("Admin Dashboard");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Hello, " + SessionManager.getName());
        subtitle.setFont(Font.font("Segoe UI", 15));
        subtitle.setTextFill(Color.WHITE);

        Button addQuestionBtn = createButton("Add Questions (Manual)");
        Button uploadPdfBtn = createButton("Add Questions (PDF)");
        Button chatbotBtn = createButton("Generate Through Chatbot");
        Button studentRecordBtn = createButton("View Student Records");
        Button deleteStudentBtn = createButton("Delete Student");
        Button logoutBtn = createButton("Logout");

        addQuestionBtn.setOnAction(e -> new AddQuestionPage().start(new Stage()));
        uploadPdfBtn.setOnAction(e -> new AdminPdfPage().start(new Stage()));
        chatbotBtn.setOnAction(e -> new AdminChatbotPage().start(new Stage()));
        studentRecordBtn.setOnAction(e -> new StudentRecordsPage().start(new Stage()));

        deleteStudentBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Delete Student");
            dialog.setHeaderText(null);
            dialog.setContentText("Enter student email:");
            dialog.showAndWait().ifPresent(email -> {
                try {
                    boolean deleted = QuizService.deleteStudentByEmail(email.trim());
                    Alert alert = new Alert(deleted ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING);
                    alert.setHeaderText(null);
                    alert.setContentText(deleted ? "Student deleted." : "Student not found or not a student account.");
                    alert.showAndWait();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait();
                }
            });
        });

        logoutBtn.setOnAction(e -> {
            SessionManager.clear();
            stage.close();
            try {
                new WelcomePage().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 20;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15,0,0,5);"
        );
        card.getChildren().addAll(
                title, subtitle, addQuestionBtn, uploadPdfBtn, chatbotBtn,
                studentRecordBtn, deleteStudentBtn, logoutBtn
        );

        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #4f46e5, #7c3aed);");

        Scene scene = new Scene(root, 700, 650);
        stage.setTitle("Admin Dashboard - ExamPrep");
        stage.setScene(scene);
        stage.show();
    }

    private Button createButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(280);
        btn.setPrefHeight(45);
        btn.setStyle(
                "-fx-background-color: #4f46e5;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 12;" +
                "-fx-cursor: hand;"
        );
        return btn;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
