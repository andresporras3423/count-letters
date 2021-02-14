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
		System.out.println("3) game feedback");
		System.out.println("4) exit");	
        int opt = Integer.parseInt(reader.readLine());
        if(opt==1) play_exam();
        else if(opt==2) settings();
        else if(opt==3)  feedback();
        else System.out.println("Bye bye!");
	}
	
	public static void feedback() throws NumberFormatException, IOException {
		System.out.println("FEEDBACK MENU");
		System.out.println("1) Top scores for current settings");
		System.out.println("2) Recent scores for current settings");
		System.out.println("3) Most errors");
		System.out.println("4) Most corrects");
		System.out.println("5) Recent errors");
		System.out.println("6) Recent corrects");
		System.out.println("7) back main menu");
		int opt = Integer.parseInt(reader.readLine());
        if(opt==1) show_top_similars();
        else if(opt==2) show_top_recents();
        else if(opt==3) most_errors();
        else if(opt==4) most_corrects();
        else if(opt==5) recent_errors();
        else if(opt==6) recent_corrects();
        else {
        	options();
        	return;
        }
        feedback();
	}
	
	public static void most_errors() {
		try {
			Statement st = s.connect();
			String query = """
					   with q1 as (select question,
                       count(*) as total,
                       sum(case
                       when correct=0
                       then 1
                       else 0
                       end) as mistakes
                       from questions
                       group by question)
                       select top 10 question, cast(mistakes*100.0/total as decimal(18,2)) as percentaje, total, mistakes
                       from q1
                       order by percentaje desc, total desc;
						""";
			ResultSet rs = st.executeQuery(query);
			int i=1;
			while(rs.next()) {
				System.out.println(""+i+") question: "+rs.getString("question")+", percentage: "+rs.getFloat("percentaje")+", total: "+rs.getInt("total")+", mistakes: "+rs.getInt("mistakes"));
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
    public static void most_corrects() {
    	try {
			Statement st = s.connect();
			String query = """
					   with q1 as (select question,
                       count(*) as total,
                       sum(case
                       when correct=1
                       then 1
                       else 0
                       end) as corrects
                       from questions
                       group by question)
                       select top 10 question, cast(corrects*100.0/total as decimal(18,2)) as percentaje, total, corrects
                       from q1
                       order by percentaje desc, total desc;
						""";
			ResultSet rs = st.executeQuery(query);
			int i=1;
			while(rs.next()) {
				System.out.println(""+i+") question: "+rs.getString("question")+", percentage: "+rs.getFloat("percentaje")+", total: "+rs.getInt("total")+", corrects: "+rs.getInt("corrects"));
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

    public static void recent_errors() {
    	try {
			Statement st = s.connect();
			ResultSet rs = st.executeQuery("select top 10 question, daytime from questions where correct=0 order by daytime desc");
			int i=1;
			while(rs.next()) {
				System.out.println(""+i+") question: "+rs.getString("question")+", datetime: "+rs.getTimestamp("daytime"));
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    public static void recent_corrects() {
    	try {
			Statement st = s.connect();
			ResultSet rs = st.executeQuery("select top 10 question, daytime from questions where correct=1 order by daytime desc");
			int i=1;
			while(rs.next()) {
				System.out.println(""+i+") question: "+rs.getString("question")+", datetime: "+rs.getTimestamp("daytime"));
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
        boolean correct = Integer.parseInt(lett)==sol;
        save_question(subList.get(0), correct);
        if(correct) System.out.println("correct");
        else System.out.println("incorrect, the solution was "+sol);
        return correct;
	}
	
    public static void save_question(String question, boolean correct){
    	try {
			Statement st = s.connect();
			int result = st.executeUpdate("insert into questions (question, correct, daytime) values('"+question+"', "+(correct? 1 : 0)+", CURRENT_TIMESTAMP)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
