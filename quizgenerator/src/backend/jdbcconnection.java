package backend;

import java.sql.Connection;
import java.sql.DriverManager;

public class jdbcconnection {
    public static Connection getConnection() {
    	String url = "jdbc:mysql://localhost:3306/quizdb";
        String user = "root";
        String password = "Mahijain@041106";
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
}