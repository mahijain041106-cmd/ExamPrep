import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    static final String URL = "jdbc:mysql://localhost:3306/exam_prep";
    static final String USER = "root";
    static final String PASS = "rawatpalak089";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            System.out.println("DB CONNECTION ERROR:");
            e.printStackTrace();
            return null;  // ✅ 
        }
    }
}