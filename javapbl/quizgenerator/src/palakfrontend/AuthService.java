package palakfrontend;

import backend.jdbcconnection;

import java.sql.*;

public class AuthService {

    public enum AuthResult {
        LOGIN_SUCCESS,
        LOGIN_FAILED,
        SIGNUP_SUCCESS,
        USER_ALREADY_EXISTS,
        WRONG_ROLE,
        DB_ERROR
    }

    public AuthResult loginStudent(String email, String password) {
        return loginWithRole(email, password, "student");
    }

    public AuthResult loginAdmin(String email, String password) {
        return loginWithRole(email, password, "admin");
    }

    private AuthResult loginWithRole(String email, String password, String expectedRole) {
        String sql = "SELECT name, email, role FROM users WHERE email=? AND password=?";

        try (Connection conn = jdbcconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                return AuthResult.LOGIN_FAILED;
            }

            String role = rs.getString("role");
            if (role == null || !role.equalsIgnoreCase(expectedRole)) {
                return AuthResult.WRONG_ROLE;
            }

            SessionManager.setUser(rs.getString("email"), rs.getString("name"), role);
            return AuthResult.LOGIN_SUCCESS;

        } catch (Exception e) {
            e.printStackTrace();
            return AuthResult.DB_ERROR;
        }
    }

    public AuthResult signup(String name, String email, String password) {
        String checkSql = "SELECT email FROM users WHERE email=?";

        try (Connection conn = jdbcconnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, email);
            if (checkStmt.executeQuery().next()) {
                return AuthResult.USER_ALREADY_EXISTS;
            }

            String insertSql =
                    "INSERT INTO users(name, email, password, role) VALUES(?, ?, ?, 'student')";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, name);
            insertStmt.setString(2, email);
            insertStmt.setString(3, password);
            insertStmt.executeUpdate();

            return AuthResult.SIGNUP_SUCCESS;

        } catch (Exception e) {
            e.printStackTrace();
            return AuthResult.DB_ERROR;
        }
    }
}
