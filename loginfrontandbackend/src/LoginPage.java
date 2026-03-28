import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;


public class LoginPage extends Application {

    // ─── State ────────────────────────────────────────────────────────────────
    private boolean isLoginMode = true;   // true = Login mode, false = Signup mode
    private final AuthService authService = new AuthService();

    // ─── UI Nodes (declared as fields so event handlers can access them) ──────
    private Label  titleLabel;
    private TextField    emailField;
    private PasswordField passwordField;
    private Button mainButton;    // "Login" or "Sign Up"
    private Button toggleButton;  // "Switch to Sign Up" or "Switch to Login"
    private Label  messageLabel;

    // ─── Entry point ──────────────────────────────────────────────────────────
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        // ── Title ─────────────────────────────────────────────────────────────
        titleLabel = new Label("Welcome Back");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        titleLabel.setTextFill(Color.web("#1a1a2e"));

        Label subtitleLabel = new Label("Login to your account");
        subtitleLabel.setFont(Font.font("Segoe UI", 14));
        subtitleLabel.setTextFill(Color.web("#6b7280"));

        // ── Email field ───────────────────────────────────────────────────────
        Label emailLabel = new Label("Email Address");
        emailLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        emailLabel.setTextFill(Color.web("#374151"));

        emailField = new TextField();
        emailField.setPromptText("you@gmail.com");
        emailField.setPrefHeight(42);
        emailField.setStyle(fieldStyle());

        // ── Password field ────────────────────────────────────────────────────
        Label passwordLabel = new Label("Password");
        passwordLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        passwordLabel.setTextFill(Color.web("#374151"));

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefHeight(42);
        passwordField.setStyle(fieldStyle());

        // ── Main action button (Login / Sign Up) ──────────────────────────────
        mainButton = new Button("Login");
        mainButton.setPrefWidth(340);
        mainButton.setPrefHeight(44);
        mainButton.setStyle(primaryButtonStyle());
        mainButton.setOnAction(e -> handleMainAction());

        // Hover effects for main button
        mainButton.setOnMouseEntered(e ->
            mainButton.setStyle(primaryButtonHoverStyle()));
        mainButton.setOnMouseExited(e ->
            mainButton.setStyle(primaryButtonStyle()));

        // ── Divider ───────────────────────────────────────────────────────────
        Separator sep = new Separator();
        sep.setPrefWidth(340);

        // ── Toggle button (switch between Login / Signup) ─────────────────────
        toggleButton = new Button("Don't have an account? Sign Up");
        toggleButton.setStyle(toggleButtonStyle());
        toggleButton.setOnAction(e -> toggleMode(subtitleLabel));

        // ── Message / feedback label ──────────────────────────────────────────
        messageLabel = new Label("");
        messageLabel.setFont(Font.font("Segoe UI", 13));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(340);
        messageLabel.setAlignment(Pos.CENTER);

        // ── Card VBox (all form elements stacked vertically) ──────────────────
        VBox card = new VBox(12,
            titleLabel,
            subtitleLabel,
            spacer(6),
            emailLabel, emailField,
            passwordLabel, passwordField,
            spacer(4),
            mainButton,
            messageLabel,
            sep,
            toggleButton
        );
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(40));
        card.setMaxWidth(420);
        card.setStyle(
            "-fx-background-color: #ffffff;" +
            "-fx-background-radius: 16;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 24, 0, 0, 6);"
        );

        // ── Root pane (centres the card on a gradient background) ─────────────
        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #e0e7ff, #f0fdf4);");
        root.setPadding(new Insets(40));

        // ── Scene & Stage ─────────────────────────────────────────────────────
        Scene scene = new Scene(root, 520, 560);
        primaryStage.setTitle("Login / Signup — exam_prep");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // ─── Handle Login or Signup action ────────────────────────────────────────
    private void handleMainAction() {
        String email    = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // Validation: empty fields
        if (email.isEmpty() || password.isEmpty()) {
            showMessage("⚠  Please fill in all fields.", "#b45309");
            return;
        }

        // Optional: basic Gmail format check
        if (!email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            showMessage("⚠  Please enter a valid email address.", "#b45309");
            return;
        }

        if (isLoginMode) {
            // ── Login ──────────────────────────────────────────────────────
            AuthService.AuthResult result = authService.login(email, password);
            switch (result) {
                case LOGIN_SUCCESS      -> showMessage("  Login successful! Welcome back.", "#065f46");
                case LOGIN_FAILED       -> showMessage("  Invalid email or password.", "#991b1b");
                case DB_ERROR           -> showMessage("⚠  Database error. Check your connection.", "#7c3aed");
                default                 -> {}
            }
        } else {
            // ── Signup ─────────────────────────────────────────────────────
            AuthService.AuthResult result = authService.signup(email, password);
            switch (result) {
                case SIGNUP_SUCCESS     -> {
                    showMessage("✅  Signup successful! You can now log in.", "#065f46");
                    clearFields();
                }
                case USER_ALREADY_EXISTS -> showMessage("⚠  This email is already registered.", "#b45309");
                case DB_ERROR            -> showMessage("⚠  Database error. Check your connection.", "#7c3aed");
                default                  -> {}
            }
        }
    }

    // ─── Toggle between Login and Signup mode ─────────────────────────────────
    private void toggleMode(Label subtitleLabel) {
        isLoginMode = !isLoginMode;
        clearFields();
        clearMessage();

        if (isLoginMode) {
            titleLabel.setText("Welcome Back");
            subtitleLabel.setText("Login to your account");
            mainButton.setText("Login");
            toggleButton.setText("Don't have an account? Sign Up");
        } else {
            titleLabel.setText("Create Account");
            subtitleLabel.setText("Sign up for a new account");
            mainButton.setText("Sign Up");
            toggleButton.setText("Already have an account? Login");
        }
    }

   
    /** Shows a feedback message in the given hex colour. */
    private void showMessage(String text, String hexColor) {
        messageLabel.setText(text);
        messageLabel.setTextFill(Color.web(hexColor));
    }

    /** Clears the message label. */
    private void clearMessage() {
        messageLabel.setText("");
    }

    /** Clears email and password fields. */
    private void clearFields() {
        emailField.clear();
        passwordField.clear();
    }

    /** Returns a transparent Region used as vertical spacing. */
    private Region spacer(double height) {
        Region r = new Region();
        r.setPrefHeight(height);
        return r;
    }

    // ─── Inline CSS style strings ──────────────────────────────────────────────

    private String fieldStyle() {
        return "-fx-font-size: 14px;" +
               "-fx-font-family: 'Segoe UI';" +
               "-fx-background-color: #f9fafb;" +
               "-fx-border-color: #d1d5db;" +
               "-fx-border-radius: 8;" +
               "-fx-background-radius: 8;" +
               "-fx-padding: 0 12 0 12;";
    }

    private String primaryButtonStyle() {
        return "-fx-background-color: #4f46e5;" +
               "-fx-text-fill: white;" +
               "-fx-font-size: 15px;" +
               "-fx-font-family: 'Segoe UI';" +
               "-fx-font-weight: bold;" +
               "-fx-background-radius: 10;" +
               "-fx-cursor: hand;";
    }

    private String primaryButtonHoverStyle() {
        return "-fx-background-color: #4338ca;" +
               "-fx-text-fill: white;" +
               "-fx-font-size: 15px;" +
               "-fx-font-family: 'Segoe UI';" +
               "-fx-font-weight: bold;" +
               "-fx-background-radius: 10;" +
               "-fx-cursor: hand;";
    }

    private String toggleButtonStyle() {
        return "-fx-background-color: transparent;" +
               "-fx-text-fill: #4f46e5;" +
               "-fx-font-size: 13px;" +
               "-fx-font-family: 'Segoe UI';" +
               "-fx-cursor: hand;" +
               "-fx-underline: true;";
    }
}
    

