package palakfrontend;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class LeaderboardPage extends Application {

    @SuppressWarnings("unchecked")
    @Override
    public void start(Stage stage) {

        Label title = new Label("Leaderboard");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        title.setTextFill(Color.WHITE);

        TableView<Student> table = new TableView<>();
        table.setPrefHeight(400);

        TableColumn<Student, Integer> rankCol = new TableColumn<>("Rank");
        rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
        rankCol.setPrefWidth(80);

        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(180);

        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(220);

        TableColumn<Student, Integer> scoreCol = new TableColumn<>("Total Score");
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        scoreCol.setPrefWidth(120);

        table.getColumns().addAll(rankCol, nameCol, emailCol, scoreCol);

        try {
            table.getItems().addAll(QuizService.getLeaderboard());
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Could not load leaderboard: " + e.getMessage()).showAndWait();
        }

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> stage.close());

        VBox card = new VBox(15, table, backBtn);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(25));
        card.setMaxWidth(750);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

        VBox inner = new VBox(20, title, card);
        inner.setAlignment(Pos.CENTER);
        inner.setPadding(new Insets(30));
        inner.setStyle("-fx-background-color: linear-gradient(to bottom right, #4f46e5, #7c3aed);");

        stage.setScene(new Scene(inner, 800, 550));
        stage.setTitle("Leaderboard");
        stage.show();
    }

    public static class Student {
        private final int rank;
        private final String name;
        private final String email;
        private final int score;

        public Student(int rank, String name, String email, int score) {
            this.rank = rank;
            this.name = name;
            this.email = email;
            this.score = score;
        }

        public int getRank() { return rank; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public int getScore() { return score; }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
