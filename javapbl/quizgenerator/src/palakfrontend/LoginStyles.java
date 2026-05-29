package palakfrontend;

final class LoginStyles {
    private LoginStyles() {}

    static String field() {
        return "-fx-font-size: 14px; -fx-background-color: #f9fafb; "
                + "-fx-border-color: #d1d5db; -fx-border-radius: 8; -fx-background-radius: 8;";
    }

    static String primaryBtn() {
        return "-fx-background-color: #4f46e5; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand;";
    }

    static String linkBtn() {
        return "-fx-background-color: transparent; -fx-text-fill: #4f46e5; "
                + "-fx-cursor: hand; -fx-underline: true;";
    }

    static String card() {
        return "-fx-background-color: white; -fx-background-radius: 16; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 24, 0, 0, 6);";
    }
}
