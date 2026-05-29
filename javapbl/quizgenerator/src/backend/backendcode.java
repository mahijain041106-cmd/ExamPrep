package backend;
import java.util.*;
import java.sql.*;
public class backendcode {
	public static void main(String[] args) {
	}
	public static void generatequiz() {
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter email: ");
		String name=sc.nextLine();
		System.out.println("Enter difficulty(easy/medium/hard):");
		String diff=sc.nextLine();
		int score=0,tq=0;
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
                tq+=1;
			}
			System.out.println("\n**Your score**: "+score);
			
			query = "UPDATE users SET total_score = total_score + ? WHERE email = ?";
			pst = con.prepareStatement(query);
			pst.setInt(1, score);   
			pst.setString(2, name); 
			pst.executeUpdate();
			
			query = "UPDATE users SET correct_answers = correct_answers + ? WHERE email = ?";
			pst = con.prepareStatement(query);
			pst.setInt(1, score);   
			pst.setString(2, name);
			pst.executeUpdate();
			
			query="UPDATE users SET total_quiz_attempted = total_quiz_attempted + ? WHERE email = ?";
			pst = con.prepareStatement(query);
			pst.setInt(1, 1);   
			pst.setString(2, name); 
			pst.executeUpdate();
			
			query="UPDATE users SET total_questions_attempted = total_questions_attempted + ? WHERE email = ?";
			pst = con.prepareStatement(query);
			pst.setInt(1, tq);   
			pst.setString(2, name); 
			pst.executeUpdate();
		}
		
		catch (Exception e) {
            e.printStackTrace();
        }
		sc.close();
	}
	public static void viewleaderboard() {
		try {
			Connection  con=jdbcconnection.getConnection();
			PreparedStatement pst=con.prepareStatement("SELECT name,email,total_score from users ORDER BY total_score desc");
			ResultSet rs=pst.executeQuery();
			int i=1;
			while(rs.next()) {
				System.out.print("\n"+i+" "+rs.getString("name")+" ");
				System.out.print(rs.getString("email")+" ");
				System.out.print(rs.getString("total_score"));
				i++;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	public static void viewprofile() {
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter email: ");
		String name=sc.nextLine();
		try {
			Connection  con=jdbcconnection.getConnection();
			PreparedStatement pst=con.prepareStatement("Select * from users where email=?");
			pst.setString(1,name);
			ResultSet rs=pst.executeQuery();
			while(rs.next()) {
				System.out.println("Name "+rs.getString("name"));
				System.out.println("Email " +rs.getString("email"));
				System.out.println("Total quiz attempted "+rs.getString("total_quiz_attempted"));
				System.out.println("Total questions "+rs.getString("total_questions_attempted"));
				System.out.println("Total correct answers "+rs.getString("correct_answers"));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		sc.close();
	}
	public static void deletestudent() {
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter email: ");
		String name=sc.nextLine();
		try {
			Connection  con=jdbcconnection.getConnection();
			PreparedStatement pst=con.prepareStatement("delete from users where email=?");
			pst.setString(1,name);
			pst.executeUpdate();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		sc.close();
	
	}
	public static void viewstudentrecords() {
		try {
			Connection  con=jdbcconnection.getConnection();
			PreparedStatement pst=con.prepareStatement("SELECT * from users");
			ResultSet rs=pst.executeQuery();
			int i=1;
			while(rs.next()) {
				System.out.print("\n"+i+" "+rs.getString("name")+" ");
				System.out.print(rs.getString("email")+" ");
				System.out.print(rs.getString("total_score")+" ");
				System.out.print(rs.getString("total_quiz_attempted")+" ");
				System.out.print(rs.getString("total_questions_attempted")+" ");
				System.out.print(rs.getString("correct_answers"));
				i++;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	
	}
}