package global;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;



public class start {
	
	static BufferedReader reader = new BufferedReader( new InputStreamReader(System.in)); 
	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	static int questions =5;
	static int tLetters =50;
	static int letters_x =10;
	public static sql_connection s = new sql_connection();
	public static void main(String[] args) throws IOException  {
		get_total_questions();
		get_total_letters();
		System.out.println("WELCOME");
		options();
	}
	
	public static void get_total_questions() {
		try {
			Statement st = s.connect();
			ResultSet rs = st.executeQuery("select top 1 val from config_game where property='questions'");
			while(rs.next())  questions = rs.getInt("val");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void get_total_letters() {
		try {
			Statement st = s.connect();
			ResultSet rs = st.executeQuery("select top 1 val from config_game where property='total_letters'");
			while(rs.next())  tLetters = rs.getInt("val");
			letters_x = (int) Math.round(Math.pow(2*tLetters, 0.5));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void update_total_letters(int nVal) {
		try {
			Statement st = s.connect();
			int result = st.executeUpdate("update config_game set val="+nVal+" where property='total_letters'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void update_total_questions(int nVal) {
		try {
			Statement st = s.connect();
			int result = st.executeUpdate("update config_game set val="+nVal+" where property='questions'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void options() throws NumberFormatException, IOException {
		System.out.println("please choose one option:");	
		System.out.println("1) play");	
		System.out.println("2) settings");
		System.out.println("3) Top scores for current settings");
		System.out.println("4) Recent scores for current settings");
		System.out.println("5) exit");	
        int opt = Integer.parseInt(reader.readLine());
        if(opt==1) play_exam();
        else if(opt==2) settings();
        else if(opt==3) {
        	show_top_similars();
        	options();
        }
        else if(opt==4) {
        	show_top_recents();
        	options();
        }
        else System.out.println("Bye bye!");
	}
	
	public static void settings() throws NumberFormatException, IOException {
		System.out.println("Choose a setting to update:");	
		System.out.println("1) change N° of questions");	
		System.out.println("2) change N° of letters");
		System.out.println("3) current settings");
		System.out.println("4) Main menu");	
		int opt = Integer.parseInt(reader.readLine());
		if(opt==1) {
			System.out.println("Type how many questions:");	
	       questions = Integer.parseInt(reader.readLine());
	       update_total_questions(questions);
	       settings();
		}
		else if(opt==2) {
			System.out.println("Type how many letters:");	
		    tLetters = Integer.parseInt(reader.readLine());
		    letters_x = (int) Math.round(Math.pow(2*tLetters, 0.5));
		    update_total_letters(tLetters);
		    settings();
		}
		else if(opt==3) {
			System.out.println("Current number of questions: "+questions);
			System.out.println("Current number of letters: "+tLetters);
			settings();
		}
		else options();
	}
	
	public static void play_exam() throws NumberFormatException, IOException {
        int sols=0;
        LocalDateTime initial = LocalDateTime.now();  
        for(int i=0; i<questions; i++) {
        	System.out.println("Questions N°"+(i+1)+")");
        	if(question()) sols++;
        }
        end_game(sols, initial);
	}
	
	public static void end_game(int sols, LocalDateTime initial) throws NumberFormatException, IOException {
		LocalDateTime end = LocalDateTime.now(); 
        long diff = ChronoUnit.SECONDS.between(initial, end);
        save_final_score(sols, diff);
        show_top_similars();
        System.out.println("Correct questions: "+sols+"/"+questions);
        System.out.println("Time: "+diff+" seconds");
        System.out.println("Score: "+(diff*(questions+1-sols)));
        options();
	}
	
	public static void save_final_score(int sols, long diff) {
		try {
			Statement st = s.connect();
			int result = st.executeUpdate("insert into scores (questions, letters, seconds, correct, daytime) values("+questions+", "+tLetters+", "+diff+", "+sols+", CURRENT_TIMESTAMP)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void show_top_similars() {
		try {
			Statement st = s.connect();
			ResultSet rs = st.executeQuery("select top 10 (seconds*(questions+1-correct)) as score, questions, correct, seconds, daytime from scores where questions="+questions+" and letters="+tLetters+" order by (seconds*(questions+1-correct)), correct");
			int i=1;
			while(rs.next()) {
				System.out.println(""+i+") score: "+rs.getInt("score")+", questions: "+rs.getInt("questions")+", correct: "+rs.getInt("correct")+", seconds: "+rs.getInt("seconds")+", date: "+rs.getTimestamp("daytime")+"");
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void show_top_recents() {
		try {
			Statement st = s.connect();
			ResultSet rs = st.executeQuery("select top 10 (seconds*(questions+1-correct)) as score, questions, correct, seconds, daytime from scores where questions="+questions+" and letters="+tLetters+" order by daytime desc");
			int i=1;
			while(rs.next()) {
				System.out.println(""+i+") score: "+rs.getInt("score")+", questions: "+rs.getInt("questions")+", correct: "+rs.getInt("correct")+", seconds: "+rs.getInt("seconds")+", date: "+rs.getTimestamp("daytime")+"");
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean question() throws IOException{
        ArrayList<String> letters = new ArrayList<String>( 
        List.of("A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S"
        		,"T","U","V","W","X","Y","Z"));
        ArrayList<String> subList = new ArrayList<String>();
        Random rand = new Random();
        String word = "";
        int sol=0;
        for(int i =0; i<5; i++) {
	        int current = rand.nextInt(letters.size());
	        String n = letters.get(current);
	        subList.add(n);
	        letters.remove(current);
        }
        for(int j=0; j<tLetters;j++) {
	        int index = rand.nextInt(subList.size());
	        if(index==0) sol++;
	        word+=subList.get(index);
	        if((j+1)%letters_x==0) word+="\n";
        }
        System.out.println("Find how many "+subList.get(0)+"'s?:");
        System.out.println(word);	
        String lett = reader.readLine();
        if(Integer.parseInt(lett)==sol) System.out.println("correct");
        else System.out.println("incorrect, the solution was "+sol);
        return Integer.parseInt(lett)==sol;
	}
}
