package palakfrontend;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class StudentLoginPage extends Application {

    private boolean isLoginMode = true;
    private final AuthService authService = new AuthService();

    private Label titleLabel;
    private Label subtitleLabel;
    private TextField nameField;
    private TextField emailField;
    private PasswordField passwordField;
    private Label nameLabel;
    private Button mainButton;
    private Button toggleButton;
    private Button backButton;
    private Label messageLabel;

    @Override
    public void start(Stage stage) {
        Label brand = new Label(WelcomePage.APP_TITLE);
        brand.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        brand.setTextFill(Color.web("#4f46e5"));
        brand.setWrapText(true);
        brand.setTextAlignment(TextAlignment.CENTER);
        brand.setMaxWidth(380);

        titleLabel = new Label("Student Login");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#1a1a2e"));

        subtitleLabel = new Label("Login to your student account");
        subtitleLabel.setFont(Font.font("Segoe UI", 14));
        subtitleLabel.setTextFill(Color.web("#6b7280"));

        nameLabel = new Label("Full Name");
        nameLabel.setVisible(false);
        nameLabel.setManaged(false);
        nameField = new TextField();
        nameField.setPromptText("Your name");
        nameField.setPrefHeight(42);
        nameField.setStyle(LoginStyles.field());
        nameField.setVisible(false);
        nameField.setManaged(false);

        emailField = new TextField();
        emailField.setPromptText("you@gmail.com");
        emailField.setPrefHeight(42);
        emailField.setStyle(LoginStyles.field());

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefHeight(42);
        passwordField.setStyle(LoginStyles.field());

        mainButton = new Button("Login");
        mainButton.setPrefWidth(340);
        mainButton.setPrefHeight(44);
        mainButton.setStyle(LoginStyles.primaryBtn());
        mainButton.setOnAction(e -> handleAction(stage));

        toggleButton = new Button("Don't have an account? Sign Up");
        toggleButton.setStyle(LoginStyles.linkBtn());
        toggleButton.setOnAction(e -> toggleMode());

        backButton = new Button("← Back");
        backButton.setStyle(LoginStyles.linkBtn());
        backButton.setOnAction(e -> {
            stage.close();
            new WelcomePage().start(new Stage());
        });

        messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(340);
        messageLabel.setAlignment(Pos.CENTER);

        VBox card = new VBox(10,
                brand, spacer(4), titleLabel, subtitleLabel, spacer(4),
                nameLabel, nameField,
                new Label("Email"), emailField,
                new Label("Password"), passwordField,
                mainButton, messageLabel,
                new Separator(), toggleButton, backButton
        );
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(35));
        card.setMaxWidth(420);
        card.setStyle(LoginStyles.card());

        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #e0e7ff, #f0fdf4);");
        root.setPadding(new Insets(30));

        stage.setTitle("Student - " + WelcomePage.APP_TITLE);
        stage.setScene(new Scene(root, 520, 620));
        stage.setResizable(false);
        stage.show();
    }

    private void handleAction(Stage stage) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showMsg("Please fill in all fields.", "#b45309");
            return;
        }

        if (isLoginMode) {
            AuthService.AuthResult result = authService.loginStudent(email, password);
            switch (result) {
                case LOGIN_SUCCESS -> {
                    stage.close();
                    new StudentDashboard().start(new Stage());
                }
                case LOGIN_FAILED -> showMsg("Invalid email or password.", "#991b1b");
                case WRONG_ROLE -> showMsg("This account is not a student. Use Admin Login.", "#b45309");
                case DB_ERROR -> showMsg("Database error. Check MySQL connection.", "#7c3aed");
                default -> {}
            }
        } else {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                showMsg("Please enter your name.", "#b45309");
                return;
            }
            AuthService.AuthResult result = authService.signup(name, email, password);
            switch (result) {
                case SIGNUP_SUCCESS -> {
                    clearFields();
                    switchToLoginAfterSignup();
                    showMsg("Signup successful! Please login.", "#065f46");
                }
                case USER_ALREADY_EXISTS -> showMsg("Email already registered.", "#b45309");
                case DB_ERROR -> showMsg("Database error.", "#7c3aed");
                default -> {}
            }
        }
    }

    private void switchToLoginAfterSignup() {
        isLoginMode = true;
        titleLabel.setText("Student Login");
        subtitleLabel.setText("Login to your student account");
        mainButton.setText("Login");
        toggleButton.setText("Don't have an account? Sign Up");
        nameLabel.setVisible(false);
        nameField.setVisible(false);
        nameField.setManaged(false);
        nameLabel.setManaged(false);
    }

    private void toggleMode() {
        isLoginMode = !isLoginMode;
        clearFields();
        messageLabel.setText("");
        if (isLoginMode) {
            switchToLoginAfterSignup();
        } else {
            titleLabel.setText("Student Sign Up");
            subtitleLabel.setText("Create your student account");
            mainButton.setText("Sign Up");
            toggleButton.setText("Already have an account? Login");
            nameLabel.setVisible(true);
            nameField.setVisible(true);
            nameField.setManaged(true);
            nameLabel.setManaged(true);
        }
    }

    private void showMsg(String text, String color) {
        messageLabel.setText(text);
        messageLabel.setTextFill(Color.web(color));
    }

    private void clearFields() {
        nameField.clear();
        emailField.clear();
        passwordField.clear();
    }

    private Region spacer(double h) {
        Region r = new Region();
        r.setPrefHeight(h);
        return r;
    }
}
