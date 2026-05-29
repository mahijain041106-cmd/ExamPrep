package palakfrontend;

public class QuizQuestion {

    private final int questionId;
    private final String questionText;
    private final String option1;
    private final String option2;
    private final String option3;
    private final String option4;
    private final int correctOption;

    public QuizQuestion(int questionId, String questionText,
                        String option1, String option2, String option3, String option4,
                        int correctOption) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correctOption = correctOption;
    }

    public int getQuestionId() {
        return questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getOption3() {
        return option3;
    }

    public String getOption4() {
        return option4;
    }

    public int getCorrectOption() {
        return correctOption;
    }

    public String getCorrectAnswerText() {
        switch (correctOption) {
            case 1: return option1;
            case 2: return option2;
            case 3: return option3;
            case 4: return option4;
            default: return "";
        }
    }
}
