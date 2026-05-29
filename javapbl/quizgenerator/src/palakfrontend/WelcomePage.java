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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class WelcomePage extends Application {

    public static final String APP_TITLE =
            "PrepGenius – AI-Powered Quiz & Analytics Platform";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        SessionManager.clear();

        Label brand = new Label(APP_TITLE);
        brand.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        brand.setTextFill(Color.web("#1a1a2e"));
        brand.setWrapText(true);
        brand.setTextAlignment(TextAlignment.CENTER);
        brand.setMaxWidth(400);

        Label tagline = new Label("Choose how you want to sign in");
        tagline.setFont(Font.font("Segoe UI", 14));
        tagline.setTextFill(Color.web("#6b7280"));

        Button studentBtn = new Button("Student Login / Sign Up");
        studentBtn.setPrefWidth(320);
        studentBtn.setPrefHeight(48);
        studentBtn.setStyle(btnStyle("#4f46e5"));
        studentBtn.setOnAction(e -> {
            stage.close();
            new StudentLoginPage().start(new Stage());
        });

        Button adminBtn = new Button("Admin Login");
        adminBtn.setPrefWidth(320);
        adminBtn.setPrefHeight(48);
        adminBtn.setStyle(btnStyle("#7c3aed"));
        adminBtn.setOnAction(e -> {
            stage.close();
            new AdminLoginPage().start(new Stage());
        });

        VBox card = new VBox(18, brand, tagline, studentBtn, adminBtn);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(45));
        card.setMaxWidth(460);
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 16;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 24, 0, 0, 6);"
        );

        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #e0e7ff, #f0fdf4);");
        root.setPadding(new Insets(40));

        stage.setTitle(APP_TITLE);
        stage.setScene(new Scene(root, 520, 420));
        stage.setResizable(false);
        stage.show();
    }

    static String btnStyle(String color) {
        return "-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;";
    }
}
