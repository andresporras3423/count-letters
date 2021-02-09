package global;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
	public static void main(String[] args) throws IOException  {
		int follow=0;
			do {
				play_exam();
				System.out.println("Do you wan't to play again?: 1) yes 2) no");	
		        follow = Integer.parseInt(reader.readLine());
			}while(follow==1);
	}
	
	public static void play_exam() throws NumberFormatException, IOException {
		System.out.println("Type how many questions:");	
        int total = Integer.parseInt(reader.readLine());
        int sols=0;
        LocalDateTime initial = LocalDateTime.now();  
        for(int i=0; i<total; i++) {
        	if(question()) sols++;
        }
        LocalDateTime end = LocalDateTime.now(); 
        long diff = ChronoUnit.SECONDS.between(initial, end);
        System.out.println("Final score: "+sols+"/"+total);
        System.out.println("Time: "+diff+" seconds");
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
        for(int j=0; j<50;j++) {
	        int index = rand.nextInt(subList.size());
	        if(index==0) sol++;
	        word+=subList.get(index);
	        if((j+1)%10==0) word+="\n";
        }
        System.out.println("Find how many "+subList.get(0)+"'s?:");
        System.out.println(word);	
        String lett = reader.readLine();
        if(Integer.parseInt(lett)==sol) System.out.println("correct");
        else System.out.println("incorrect, the solution was "+sol);
        return Integer.parseInt(lett)==sol;
	}
}
