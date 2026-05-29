package palakfrontend;

public final class SessionManager {

    private static String email;
    private static String name;
    private static String role;

    private SessionManager() {}

    public static void setUser(String userEmail, String userName, String userRole) {
        email = userEmail;
        name = userName;
        role = userRole;
    }

    public static void clear() {
        email = null;
        name = null;
        role = null;
    }

    public static String getEmail() {
        return email;
    }

    public static String getName() {
        return name;
    }

    public static String getRole() {
        return role;
    }

    public static boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }

    public static boolean isLoggedIn() {
        return email != null && !email.isEmpty();
    }
}
