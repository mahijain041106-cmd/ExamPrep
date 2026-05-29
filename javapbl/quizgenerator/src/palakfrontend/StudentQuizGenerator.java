package palakfrontend;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

/**
 * Select PDF → wait 10 seconds → open Start Quiz ({@link QuizPage}) automatically.
 * No PDF extraction.
 */
public class StudentQuizGenerator {

    private static final String FONT = "Segoe UI";
    private static final int WAIT_SECONDS = 10;

    public static void open(Stage parentStage) {
        if (!SessionManager.isLoggedIn()) {
            new Alert(Alert.AlertType.WARNING, "Please login as a student first.").showAndWait();
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select PDF");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = chooser.showOpenDialog(parentStage);
        if (file == null) {
            return;
        }

        Stage waitStage = new Stage();
        waitStage.initOwner(parentStage);

        Label title = new Label("Starting quiz");
        title.setFont(Font.font(FONT, FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#1e293b"));

        Label countdown = new Label(String.valueOf(WAIT_SECONDS));
        countdown.setFont(Font.font(FONT, FontWeight.BOLD, 48));
        countdown.setTextFill(Color.web("#4f46e5"));

        Label hint = new Label("Opening Start Quiz in " + WAIT_SECONDS + " seconds…");
        hint.setFont(Font.font(FONT, 13));
        hint.setTextFill(Color.web("#64748b"));

        VBox card = new VBox(16, title, countdown, hint);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(36));
        card.setMaxWidth(400);
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 18; "
                        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 16, 0, 0, 4);"
        );

        VBox root = new VBox(card);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #4f46e5, #7c3aed);");

        waitStage.setScene(new Scene(root, 440, 300));
        waitStage.setTitle("Please wait");
        waitStage.show();

        final int[] secondsLeft = {WAIT_SECONDS};
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsLeft[0]--;
            countdown.setText(String.valueOf(Math.max(0, secondsLeft[0])));
            hint.setText("Opening Start Quiz in " + Math.max(0, secondsLeft[0]) + " seconds…");
            if (secondsLeft[0] <= 0) {
                //timeline.stop();
                waitStage.close();
                Platform.runLater(() -> new QuizPage().start(new Stage()));
            }
        }));
        timeline.setCycleCount(WAIT_SECONDS);
        timeline.play();
    }
}
