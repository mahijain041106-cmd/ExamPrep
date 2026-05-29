package palakfrontend;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class AdminLoginPage extends Application {

    @Override
    public void start(Stage stage) {
        final AuthService authService = new AuthService();

        Label brand = new Label(WelcomePage.APP_TITLE);
        brand.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        brand.setTextFill(Color.web("#7c3aed"));
        brand.setWrapText(true);
        brand.setTextAlignment(TextAlignment.CENTER);
        brand.setMaxWidth(380);

        Label title = new Label("Admin Login");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#1a1a2e"));

        Label subtitle = new Label("Admin accounts are pre-configured only");
        subtitle.setFont(Font.font("Segoe UI", 13));
        subtitle.setTextFill(Color.web("#6b7280"));
        subtitle.setWrapText(true);

        TextField emailField = new TextField();
        emailField.setPromptText("admin@gmail.com");
        emailField.setPrefHeight(42);
        emailField.setStyle(LoginStyles.field());

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefHeight(42);
        passwordField.setStyle(LoginStyles.field());

        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(340);

        Button loginBtn = new Button("Login");
        loginBtn.setPrefWidth(340);
        loginBtn.setPrefHeight(44);
        loginBtn.setStyle(LoginStyles.primaryBtn());
        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            if (email.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Please fill in all fields.");
                messageLabel.setTextFill(Color.web("#b45309"));
                return;
            }
            AuthService.AuthResult result = authService.loginAdmin(email, password);
            switch (result) {
                case LOGIN_SUCCESS -> {
                    stage.close();
                    new AdminDashboard().start(new Stage());
                }
                case LOGIN_FAILED -> {
                    messageLabel.setText("Invalid admin email or password.");
                    messageLabel.setTextFill(Color.web("#991b1b"));
                }
                case WRONG_ROLE -> {
                    messageLabel.setText("Not an admin account. Use Student Login.");
                    messageLabel.setTextFill(Color.web("#b45309"));
                }
                case DB_ERROR -> {
                    messageLabel.setText("Database error.");
                    messageLabel.setTextFill(Color.web("#7c3aed"));
                }
                default -> {}
            }
        });

        Button backBtn = new Button("← Back");
        backBtn.setStyle(LoginStyles.linkBtn());
        backBtn.setOnAction(e -> {
            stage.close();
            new WelcomePage().start(new Stage());
        });

        VBox card = new VBox(12,
                brand, title, subtitle,
                new Label("Email"), emailField,
                new Label("Password"), passwordField,
                loginBtn, messageLabel, backBtn
        );
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(40));
        card.setMaxWidth(420);
        card.setStyle(LoginStyles.card());

        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #ede9fe, #f0fdf4);");
        root.setPadding(new Insets(30));

        stage.setTitle("Admin - " + WelcomePage.APP_TITLE);
        stage.setScene(new Scene(root, 520, 520));
        stage.setResizable(false);
        stage.show();
    }
}
