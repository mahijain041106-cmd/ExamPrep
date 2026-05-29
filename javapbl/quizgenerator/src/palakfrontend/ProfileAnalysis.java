package palakfrontend;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class ProfileAnalysis extends Application {

    private static final String FONT = "Segoe UI";

    @Override
    public void start(Stage stage) {

        Label title = new Label("Profile Analysis");
        title.setFont(Font.font(FONT, FontWeight.BOLD, 30));
        title.setTextFill(Color.WHITE);

        PieChart pieChart = new PieChart();
        pieChart.setStyle("-fx-font-family: '" + FONT + "'; -fx-font-size: 13px;");
        Label totalQuiz = new Label();
        Label totalQuestions = new Label();
        Label totalScore = new Label();
        Label averageScore = new Label();
        Label rankLabel = new Label();
        Label nameLabel = new Label();

        try {
            QuizService.ProfileStats stats = QuizService.getProfile(SessionManager.getEmail());
            if (stats == null) {
                new Alert(Alert.AlertType.WARNING, "Profile not found.").showAndWait();
                stage.close();
                return;
            }

            nameLabel.setText("Name: " + stats.name + "  |  Email: " + stats.email);
            totalQuiz.setText("Total Quizzes Attempted: " + stats.totalQuizAttempted);
            totalQuestions.setText("Total Questions Attempted: " + stats.totalQuestionsAttempted);
            totalScore.setText("Total Score: " + stats.totalScore);
            averageScore.setText(String.format("Average Score per Quiz: %.1f", stats.average));
            rankLabel.setText(stats.rank > 0 ? "Leaderboard Rank: #" + stats.rank : "Leaderboard Rank: —");

            pieChart.getData().add(new PieChart.Data("Correct", Math.max(1, stats.correctAnswers)));
            pieChart.getData().add(new PieChart.Data("Wrong", Math.max(0, stats.wrongAnswers)));
            pieChart.setTitle("Correct vs Wrong Answers");
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Could not load profile: " + e.getMessage()).showAndWait();
        }

        nameLabel.setFont(Font.font(FONT, 15));
        nameLabel.setTextFill(Color.web("#475569"));

        for (Label lbl : new Label[]{totalQuiz, totalQuestions, totalScore, averageScore, rankLabel}) {
            lbl.setFont(Font.font(FONT, FontWeight.BOLD, 17));
            lbl.setTextFill(Color.web("#1e1b4b"));
        }

        Button backBtn = new Button("Back");
        backBtn.setPrefWidth(140);
        backBtn.setPrefHeight(40);
        backBtn.setStyle(
                "-fx-background-color: #4f46e5; -fx-text-fill: white; "
                        + "-fx-font-family: '" + FONT + "'; -fx-font-size: 15px; -fx-font-weight: bold; "
                        + "-fx-background-radius: 10; -fx-cursor: hand;"
        );
        backBtn.setOnAction(e -> stage.close());

        VBox card = new VBox(18, nameLabel, pieChart, totalQuiz, totalQuestions, totalScore, averageScore, rankLabel, backBtn);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(25));
        card.setMaxWidth(650);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

        VBox inner = new VBox(15, title, card);
        inner.setAlignment(Pos.CENTER);
        inner.setPadding(new Insets(25));
        inner.setStyle("-fx-background-color: linear-gradient(to bottom right, #4f46e5, #7c3aed);");

        stage.setScene(new Scene(inner, 750, 650));
        stage.setTitle("Profile Analysis");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
