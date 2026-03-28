import java.sql.*;

public class AuthService {

    public enum AuthResult {
        LOGIN_SUCCESS,
        LOGIN_FAILED,
        SIGNUP_SUCCESS,
        USER_ALREADY_EXISTS,
        DB_ERROR
    }

    // LOGIN
    public AuthResult login(String email, String password) {

        String sql = "SELECT * FROM users WHERE email=? AND password=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return AuthResult.LOGIN_SUCCESS;
            } else {
                return AuthResult.LOGIN_FAILED;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return AuthResult.DB_ERROR;
        }
    }

    // SIGNUP
    public AuthResult signup(String email, String password) {

        // check if user exists
        String checkSql = "SELECT * FROM users WHERE email=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                return AuthResult.USER_ALREADY_EXISTS;
            }

            String insertSql = "INSERT INTO users(email, password) VALUES(?, ?)";

            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, email);
            insertStmt.setString(2, password);

            insertStmt.executeUpdate();

            return AuthResult.SIGNUP_SUCCESS;

        } catch (Exception e) {
            e.printStackTrace();
            return AuthResult.DB_ERROR;
        }
    }
}