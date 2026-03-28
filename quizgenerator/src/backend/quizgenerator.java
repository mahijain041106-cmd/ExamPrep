package backend;
import java.util.*;
import java.sql.*;
public class quizgenerator {
	public static void main(String[] args) {
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter email: ");
		String name=sc.nextLine();
		System.out.println("Enter difficulty(easy/medium/hard):");
		String diff=sc.nextLine();
		int score=0;
		try
		{
			Connection con = jdbcconnection.getConnection();
			String query = "SELECT * FROM questions WHERE difficulty=? ORDER BY RAND() LIMIT 10";
			PreparedStatement pst=con.prepareStatement(query);
			pst.setString(1, diff);
			ResultSet rs=pst.executeQuery();
			while(rs.next())
			{
				System.out.println("\n"+rs.getString("question_text"));
				System.out.println("1."+rs.getString("option1"));
				System.out.println("2."+rs.getString("option2"));
				System.out.println("3."+rs.getString("option3"));
				System.out.println("4."+rs.getString("option4"));
				
				System.out.print("Enter your answer (1-4): ");
                int ans = sc.nextInt();
                
                if (ans == rs.getInt("correct_option")) {
                    score++;
                }
                else {
                	System.out.println("---------WRONG ANSWER----------\nCorrect answer: "+rs.getInt("correct_option"));
                }             
			}
			System.out.println("Your score: "+score);
			query = "UPDATE users SET score = score + ? WHERE email = ?";
			pst = con.prepareStatement(query);

			pst.setInt(1, score);   
			pst.setString(2, name); 

			pst.executeUpdate();
		}
		
		catch (Exception e) {
            e.printStackTrace();
        }
		sc.close();
	}
}
