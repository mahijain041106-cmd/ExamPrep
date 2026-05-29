package palakfrontend;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StudentRecordsPage {

    public static class Row {
        private final SimpleStringProperty name = new SimpleStringProperty();
        private final SimpleStringProperty email = new SimpleStringProperty();
        private final SimpleIntegerProperty totalScore = new SimpleIntegerProperty();

        public Row(String name, String email, int score) {
            this.name.set(name);
            this.email.set(email);
            this.totalScore.set(score);
        }

        public String getName() { return name.get(); }
        public String getEmail() { return email.get(); }
        public int getTotalScore() { return totalScore.get(); }
    }

    @SuppressWarnings("unchecked")
    public void start(Stage stage) {
        TableView<Row> table = new TableView<>();

        TableColumn<Row, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(180);

        TableColumn<Row, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(220);

        TableColumn<Row, Number> scoreCol = new TableColumn<>("Total Score");
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("totalScore"));
        scoreCol.setPrefWidth(120);

        table.getColumns().addAll(nameCol, emailCol, scoreCol);

        try {
            for (QuizService.StudentRecord r : QuizService.getStudentRecords()) {
                table.getItems().add(new Row(r.name, r.email, r.totalScore));
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Could not load records: " + e.getMessage()).showAndWait();
        }

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> stage.close());

        VBox root = new VBox(15, new Label("Student Records"), table, backBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        table.setPrefHeight(400);

        stage.setScene(new Scene(root, 600, 500));
        stage.setTitle("Student Records");
        stage.show();
    }
}
