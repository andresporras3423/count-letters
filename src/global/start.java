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
	
	public static void options() throws NumberFormatException, IOException {
		System.out.println("please choose one option:");	
		System.out.println("1) play");	
		System.out.println("2) settings");	
		System.out.println("3) exit");	
        int opt = Integer.parseInt(reader.readLine());
        if(opt==1) play_exam();
        else if(opt==2) settings();
        else System.out.println("Bye bye!");
	}
	
	public static void settings() throws NumberFormatException, IOException {
		System.out.println("Choose a setting to update:");	
		System.out.println("1) N° of questions");	
		System.out.println("2) N° of letters");	
		System.out.println("3) Main menu");	
		int opt = Integer.parseInt(reader.readLine());
		if(opt==1) {
			System.out.println("Type how many questions:");	
	       questions = Integer.parseInt(reader.readLine());
	       settings();
		}
		else if(opt==2) {
			System.out.println("Type how many letters:");	
		    tLetters = Integer.parseInt(reader.readLine());
		    letters_x = (int) Math.round(Math.pow(2*tLetters, 0.5));
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
        LocalDateTime end = LocalDateTime.now(); 
        long diff = ChronoUnit.SECONDS.between(initial, end);
        System.out.println("Final score: "+sols+"/"+questions);
        System.out.println("Time: "+diff+" seconds");
        options();
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
