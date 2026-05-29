package palakfrontend;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class StudentDashboard extends Application {

    @Override
    public void start(Stage stage) {
        Label title = new Label("Student Dashboard");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        title.setTextFill(Color.web("#1a1a2e"));

        Label subtitle = new Label("Welcome, " + SessionManager.getName());
        subtitle.setFont(Font.font("Segoe UI", 15));
        subtitle.setTextFill(Color.web("#6b7280"));

        Button quizBtn = createButton("Start Quiz");
        Button quizGeneratorBtn = createGreenButton("Upload PDF Quiz");
        Button leaderboardBtn = createButton("Leaderboard");
        Button analysisBtn = createButton("Profile Analysis");
        Button logoutBtn = createButton("Logout");

        quizBtn.setOnAction(e -> new QuizPage().start(new Stage()));
        quizGeneratorBtn.setOnAction(e -> StudentQuizGenerator.open(stage));
        leaderboardBtn.setOnAction(e -> new LeaderboardPage().start(new Stage()));
        analysisBtn.setOnAction(e -> new ProfileAnalysis().start(new Stage()));
        logoutBtn.setOnAction(e -> {
            SessionManager.clear();
            stage.close();
            new WelcomePage().start(new Stage());
        });

        VBox card = new VBox(20,
                title, subtitle,
                quizBtn, quizGeneratorBtn,
                leaderboardBtn, analysisBtn, logoutBtn
        );
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 20; "
                        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0, 0, 5);"
        );

        StackPane inner = new StackPane(card);
        inner.setStyle("-fx-background-color: linear-gradient(to bottom right, #4f46e5, #7c3aed);");

        StackPane root = FloatingChatbot.wrapForStudentDashboard(inner);

        stage.setTitle("Student Dashboard - PrepGenius");
        stage.setScene(new Scene(root, 720, 620));
        stage.show();
    }

    private Button createButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(250);
        btn.setPrefHeight(45);
        btn.setStyle(
                "-fx-background-color: #4f46e5; -fx-text-fill: white; "
                        + "-fx-font-size: 15px; -fx-font-weight: bold; "
                        + "-fx-background-radius: 12; -fx-cursor: hand;"
        );
        return btn;
    }

    private Button createGreenButton(String text) {
        Button btn = createButton(text);
        btn.setStyle(
                "-fx-background-color: #059669; -fx-text-fill: white; "
                        + "-fx-font-size: 15px; -fx-font-weight: bold; "
                        + "-fx-background-radius: 12; -fx-cursor: hand;"
        );
        return btn;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
