package palakfrontend;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.List;
/**
 * Start Quiz module: displays quiz UI and updates student scores in the database.
 * PDF extraction and MCQ generation are handled separately by {@link StudentQuizGenerator}.
 */
public class QuizPage extends Application {
	private static final int QUIZ_TIME_SECONDS = 120;
	private static final String FONT = "Segoe UI";
	private List<QuizQuestion> questions;
	private int currentIndex = 0;
	private int score = 0;
	private Timeline timeline;
	private int timeLeft = QUIZ_TIME_SECONDS;
	private Label timerLabel;
	private Label questionLabel;
	private Label feedbackLabel;
	private ToggleGroup group;
	private RadioButton op1, op2, op3, op4;
	private Button nextBtn;
	private Button submitBtn;
	private Stage stage;
	private boolean finished = false;
	private boolean awaitingContinue = false;
	private boolean currentQuestionScored = false;
	@Override
	public void start(Stage stage) {
		this.stage = stage;
		if (!SessionManager.isLoggedIn()) {
			new Alert(Alert.AlertType.WARNING, "Please login first.").showAndWait();
			stage.close();
			return;
		}
		ComboBox<String> difficultyBox = new ComboBox<>();
		difficultyBox.getItems().addAll("easy", "medium", "hard");
		difficultyBox.setValue("easy");
		styleCombo(difficultyBox);
		Label startTitle = new Label("Start Your Quiz");
		startTitle.setFont(Font.font(FONT, FontWeight.BOLD, 26));
		startTitle.setTextFill(Color.WHITE);
		Label info = new Label("10 questions  |  " + QUIZ_TIME_SECONDS + " seconds  |  Auto-submit when time ends");
		info.setFont(Font.font(FONT, 14));
		info.setTextFill(Color.web("#e0e7ff"));
		Label chatHint = new Label("Ask AI tutor available during the quiz (bottom-right)");
		chatHint.setFont(Font.font(FONT, 13));
		chatHint.setTextFill(Color.web("#c7d2fe"));
		Button startBtn = createPrimaryButton("Start Quiz");

		startBtn.setOnAction(e -> {
			try {
				questions = QuizService.fetchQuizQuestions(difficultyBox.getValue());
				if (questions.isEmpty()) {
					new Alert(Alert.AlertType.WARNING, "No questions found for difficulty: " + difficultyBox.getValue())
							.showAndWait();
					return;
				}
				timeLeft = QUIZ_TIME_SECONDS;
				currentIndex = 0;
				score = 0;
				finished = false;
				awaitingContinue = false;
				currentQuestionScored = false;
				showQuizUi(difficultyBox.getValue());
			} catch (Exception ex) {
				new Alert(Alert.AlertType.ERROR, "Could not load quiz: " + ex.getMessage()).showAndWait();
			}
		});
		VBox startCard = new VBox(14, new Label("Select Difficulty"), difficultyBox, info, startBtn);
		startCard.getChildren().add(0, startTitle);
		startCard.setAlignment(Pos.CENTER);
		startCard.setPadding(new Insets(32));
		startCard.setMaxWidth(420);
		startCard.setStyle("-fx-background-color: white; -fx-background-radius: 18;");
		for (javafx.scene.Node node : startCard.getChildren()) {
			if (node instanceof Label && node != startTitle && !node.equals(info)) {
				((Label) node).setFont(Font.font(FONT, FontWeight.BOLD, 15));
				((Label) node).setTextFill(Color.web("#334155"));
			}
		}
		VBox startRoot = new VBox(18, startCard, chatHint);
		startRoot.setAlignment(Pos.CENTER);
		startRoot.setPadding(new Insets(36));
		startRoot.setStyle("-fx-background-color: linear-gradient(to bottom right, #4f46e5, #7c3aed);");
		stage.setScene(new Scene(startRoot, 560, 400));
		stage.setTitle("Quiz - PrepGenius");
		stage.show();
	}

	/** Start quiz with questions generated from a student PDF (score saved on submit). */
	public void startWithQuestions(Stage stage, List<QuizQuestion> loadedQuestions, String quizLabel) {
		this.stage = stage;
		if (!SessionManager.isLoggedIn()) {
			new Alert(Alert.AlertType.WARNING, "Please login first.").showAndWait();
			stage.close();
			return;
		}
		if (loadedQuestions == null || loadedQuestions.isEmpty()) {
			new Alert(Alert.AlertType.WARNING, "No questions were generated from the PDF.").showAndWait();
			stage.close();
			return;
		}

		questions = loadedQuestions;
		timeLeft = Math.max(QUIZ_TIME_SECONDS, questions.size() * 15);
		currentIndex = 0;
		score = 0;
		finished = false;
		awaitingContinue = false;
		currentQuestionScored = false;

		String label = quizLabel != null && !quizLabel.isBlank() ? quizLabel : "PDF Quiz";
		showQuizUi(label);
		stage.setTitle("Quiz - " + label);
		stage.show();
	}

	private void showQuizUi(String difficulty) {
		Label title = new Label("Quiz — " + difficulty.toUpperCase());
		title.setFont(Font.font(FONT, FontWeight.BOLD, 26));
		title.setTextFill(Color.WHITE);
		timerLabel = new Label("Time Left: " + timeLeft + " sec");
		timerLabel.setFont(Font.font(FONT, FontWeight.BOLD, 17));
		timerLabel.setTextFill(Color.WHITE);
		questionLabel = new Label();
		questionLabel.setWrapText(true);
		questionLabel.setFont(Font.font(FONT, FontWeight.BOLD, 19));
		questionLabel.setTextFill(Color.web("#1e293b"));
		questionLabel.setMaxWidth(580);
		feedbackLabel = new Label();
		feedbackLabel.setWrapText(true);
		feedbackLabel.setMaxWidth(580);
		feedbackLabel.setFont(Font.font(FONT, FontWeight.BOLD, 16));
		feedbackLabel.setVisible(false);
		feedbackLabel.setManaged(false);
		group = new ToggleGroup();
		op1 = new RadioButton();
		op2 = new RadioButton();
		op3 = new RadioButton();
		op4 = new RadioButton();
		for (RadioButton rb : new RadioButton[] { op1, op2, op3, op4 }) {
			rb.setToggleGroup(group);
			rb.setWrapText(true);
			rb.setMaxWidth(560);
			resetOptionStyle(rb);
		}
		nextBtn = createPrimaryButton("Check & Next");
		submitBtn = createPrimaryButton("Check & Submit");
		nextBtn.setOnAction(e -> onAnswerAction());
		submitBtn.setOnAction(e -> onAnswerAction());
		VBox quizCard = new VBox(14, questionLabel, op1, op2, op3, op4, feedbackLabel, nextBtn, submitBtn);
		quizCard.setAlignment(Pos.CENTER_LEFT);
		quizCard.setPadding(new Insets(28));
		quizCard.setMaxWidth(640);
		quizCard.setStyle("-fx-background-color: white; -fx-background-radius: 18; "
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 16, 0, 0, 4);");
		Label chatHint = new Label("Need help? Tap Ask AI at the bottom-right corner.");
		chatHint.setFont(Font.font(FONT, 13));
		chatHint.setTextFill(Color.web("#c7d2fe"));
		VBox root = new VBox(14, title, timerLabel, quizCard, chatHint);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(24));
		root.setStyle("-fx-background-color: linear-gradient(to bottom right, #4f46e5, #7c3aed);");
		stage.setScene(new Scene(root, 780, 680));
		loadQuestion();
		startTimer();
	}
	private void startTimer() {
		timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
			timeLeft--;
			timerLabel.setText("Time Left: " + timeLeft + " sec");
			if (timeLeft <= 10) {
				timerLabel.setTextFill(Color.web("#fecaca"));
			}
			if (timeLeft <= 0) {
				finishQuiz(true);
			}
		}));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
	}
	private void loadQuestion() {
		QuizQuestion q = questions.get(currentIndex);
		questionLabel.setText("Q" + (currentIndex + 1) + " of " + questions.size() + ".  " + q.getQuestionText());
		op1.setText("1.  " + q.getOption1());
		op2.setText("2.  " + q.getOption2());
		op3.setText("3.  " + q.getOption3());
		op4.setText("4.  " + q.getOption4());
		group.selectToggle(null);
		awaitingContinue = false;
		currentQuestionScored = false;
		hideFeedback();
		setOptionsDisabled(false);
		for (RadioButton rb : new RadioButton[] { op1, op2, op3, op4 }) {
			resetOptionStyle(rb);
		}
		nextBtn.setText("Check & Next");
		submitBtn.setText("Check & Submit");
		nextBtn.setVisible(currentIndex < questions.size() - 1);
		submitBtn.setVisible(currentIndex == questions.size() - 1);
	}
	private void onAnswerAction() {
		if (awaitingContinue) {
			if (currentIndex >= questions.size() - 1) {
				finishQuiz(false);
			} else {
				currentIndex++;
				loadQuestion();
			}
			return;
		}
		if (group.getSelectedToggle() == null) {
			feedbackLabel.setText("Please select an answer first.");
			feedbackLabel.setTextFill(Color.web("#b45309"));
			feedbackLabel.setStyle("-fx-background-color: #fffbeb; -fx-padding: 12 16; -fx-background-radius: 10;");
			feedbackLabel.setVisible(true);
			feedbackLabel.setManaged(true);
			return;
		}
		boolean correct = scoreCurrentQuestion() > 0;
		showFeedback(correct);
		awaitingContinue = true;
		setOptionsDisabled(true);
		if (currentIndex < questions.size() - 1) {
			nextBtn.setText("Continue");
		} else {
			submitBtn.setText("Confirm Submit");
		}
	}
	private void showFeedback(boolean correct) {
		QuizQuestion q = questions.get(currentIndex);
		int chosen = getChosenOption();
		if (correct) {
			feedbackLabel.setText("Correct! Well done.");
			feedbackLabel.setTextFill(Color.web("#166534"));
			feedbackLabel.setStyle("-fx-background-color: #dcfce7; -fx-padding: 12 16; -fx-background-radius: 10;");
			highlightOption(chosen, true);
		} else {
			int correctNum = q.getCorrectOption();
			feedbackLabel
					.setText("Wrong.  The correct answer is option " + correctNum + ":  " + q.getCorrectAnswerText());
			feedbackLabel.setTextFill(Color.web("#b91c1c"));
			feedbackLabel.setStyle("-fx-background-color: #fee2e2; -fx-padding: 12 16; -fx-background-radius: 10;");
			if (chosen > 0) {
				highlightOption(chosen, false);
			}
			highlightOption(correctNum, true);
		}
		feedbackLabel.setVisible(true);
		feedbackLabel.setManaged(true);
	}
	private void hideFeedback() {
		feedbackLabel.setVisible(false);
		feedbackLabel.setManaged(false);
		feedbackLabel.setText("");
	}
	private void highlightOption(int optionNum, boolean correct) {
		RadioButton rb = radioForOption(optionNum);
		if (rb == null) {
			return;
		}
		if (correct) {
			rb.setStyle("-fx-font-family: '" + FONT + "'; -fx-font-size: 16px; -fx-font-weight: bold; "
					+ "-fx-text-fill: #166534; -fx-background-color: #dcfce7; "
					+ "-fx-padding: 6 10; -fx-background-radius: 8;");
		} else {
			rb.setStyle("-fx-font-family: '" + FONT + "'; -fx-font-size: 16px; -fx-font-weight: bold; "
					+ "-fx-text-fill: #b91c1c; -fx-background-color: #fee2e2; "
					+ "-fx-padding: 6 10; -fx-background-radius: 8;");
		}
	}
	private RadioButton radioForOption(int optionNum) {
		switch (optionNum) {
		case 1:
			return op1;
		case 2:
			return op2;
		case 3:
			return op3;
		case 4:
			return op4;
		default:
			return null;
		}
	}
	private int scoreCurrentQuestion() {
		if (!currentQuestionScored) {
			int points = checkAnswer();
			score += points;
			currentQuestionScored = true;
			return points;
		}
		return checkAnswer();
	}
	private int getChosenOption() {
		RadioButton selected = (RadioButton) group.getSelectedToggle();
		if (selected == null) {
			return 0;
		}
		if (selected == op1)
			return 1;
		if (selected == op2)
			return 2;
		if (selected == op3)
			return 3;
		if (selected == op4)
			return 4;
		return 0;
	}
	private int checkAnswer() {
		int chosen = getChosenOption();
		if (chosen == 0) {
			return 0;
		}
		return chosen == questions.get(currentIndex).getCorrectOption() ? 1 : 0;
	}
	private void finishQuiz(boolean timeUp) {
		if (finished) {
			return;
		}
		finished = true;
		if (timeline != null) {
			timeline.stop();
		}
		if (!currentQuestionScored) {
			score += checkAnswer();
		}
		try {
			QuizService.submitQuizResult(SessionManager.getEmail(), score, questions.size());
		} catch (Exception ex) {
			new Alert(Alert.AlertType.ERROR, "Could not save score: " + ex.getMessage()).showAndWait();
		}
		String msg = timeUp ? "Time is over! Quiz auto-submitted.\nYour score: " + score + "/" + questions.size()
				: "Quiz submitted!\nYour score: " + score + "/" + questions.size();
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
		stage.close();
	}
	private void setOptionsDisabled(boolean disabled) {
		for (RadioButton rb : new RadioButton[] { op1, op2, op3, op4 }) {
			rb.setDisable(disabled);
		}
	}
	private void resetOptionStyle(RadioButton rb) {
		rb.setStyle("-fx-font-family: '" + FONT + "'; -fx-font-size: 16px; -fx-text-fill: #334155;");
	}
	private Button createPrimaryButton(String text) {
		Button btn = new Button(text);
		btn.setPrefWidth(200);
		btn.setPrefHeight(42);
		btn.setStyle("-fx-background-color: #4f46e5; -fx-text-fill: white; " + "-fx-font-family: '" + FONT
				+ "'; -fx-font-size: 15px; -fx-font-weight: bold; " + "-fx-background-radius: 10; -fx-cursor: hand;");
		return btn;
	}
	private void styleCombo(ComboBox<String> box) {
		box.setStyle("-fx-font-family: '" + FONT + "'; -fx-font-size: 14px; " + "-fx-background-radius: 8;");
	}
	public static void main(String[] args) {
		launch(args);
	}
}
