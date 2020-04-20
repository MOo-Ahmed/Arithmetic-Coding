import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Demo {
	public static String message ;
	
	public static int NumberOfSymbols = 0 ;
	
	public static double commulativeFrequency[] = new double [256] ;
	
	public static Map<String,Double> probabilities  = new HashMap<String,Double>() ;
	//________________________________________________________________________
	
	public static int countSymbolFrequency(char c) {
		int count = 0 ;
		for(char s : message.toCharArray()) {
			if(s == c)	count++ ;
		}
		return count ;
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	public static void prepareProbabilities(String message) throws IOException {
		Scanner sc = new Scanner(System.in);
		if(message.equals("") == false) {
			BufferedReader brComp = new BufferedReader(new FileReader("D:\\FCAI-CU material\\Multimedia\\implementations\\ArithmeticCoding/probabilities.txt"));
			String s ;
			while ((s = brComp.readLine()) != null) { 
				double prob = Double.parseDouble(s.substring(1));
				String c = s.substring(0,1) ;
				probabilities.put( c , (double) prob) ;
			}
			brComp.close();
			
			// then prepare commulative frequencies

			for(char c : message.toCharArray()) {
				
				double temp = 0 ;
				for (Map.Entry<String, Double> entry : probabilities.entrySet()) {
				    if (entry.getKey().compareTo(c + "") < 0) temp += entry.getValue() ;
				}
				commulativeFrequency[c] = probabilities.get(c + "")+ temp ;
			
			}
		}
		else {	// in case of decoding, when there's no info 
			BufferedReader brComp = new BufferedReader(new FileReader("D:\\FCAI-CU material\\Multimedia\\implementations\\ArithmeticCoding/probabilities.txt"));
			String s ;
			while ((s = brComp.readLine()) != null) { 
				double prob = Double.parseDouble(s.substring(1));
				String c = s.substring(0,1) ;
				probabilities.put( c , (double) prob) ;
			}
			brComp.close();
		
			String virtualMessage = new String(); // to act like I have a message
			for (Map.Entry<String, Double> entry : probabilities.entrySet()) {
			     virtualMessage += entry.getKey() ;
			}
			// then prepare commulative frequencies

			for(char c : virtualMessage.toCharArray()) {
				
				double temp = 0 ;
				for (Map.Entry<String, Double> entry : probabilities.entrySet()) {
				    if (entry.getKey().compareTo(c + "") < 0) temp += entry.getValue() ;
				}
				commulativeFrequency[c] = probabilities.get(c + "")+ temp ;
			
			}
		
		}
				
	}
	
	public static void print() {
		for(int i = 0 ; i < message.length(); i++) {
			System.out.println(message.charAt(i) + ", " + commulativeFrequency[(message.charAt(i))] + ", "+
		probabilities.get(message.charAt(i)+""));
		}
	}
	
	public static double Encode() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader
				("D:\\FCAI-CU material\\Multimedia\\implementations\\ArithmeticCoding/Original Message.txt"));
		message = br.readLine();
		prepareProbabilities(message);
		/*
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter your message : ");
		*/
		double Lower = 0, Upper = 1 , before = 0 ,previousLower;
		boolean firstChar = true;
		for(char c : message.toCharArray()) {
			if(firstChar == true) {
				before = commulativeFrequency[c] - (double)probabilities.get(c+"") ;
				previousLower= Lower; 
				Lower =  previousLower + (Upper - previousLower)* before ;
				Upper = previousLower + (Upper - previousLower)*commulativeFrequency[c] ;
				firstChar = false ;
			}
			else {
				before = commulativeFrequency[c] - (double)probabilities.get(c+"") ;
				previousLower= Lower; 
				Lower =  previousLower + (Upper - previousLower)* before ;
				Upper = previousLower + (Upper - previousLower)*commulativeFrequency[c] ;
			}
			
			NumberOfSymbols++ ;
			//System.out.println("Symbol = " + c + " , comm frequency = " + commulativeFrequency[c] + ".. Lower = " + Lower + " , Upper = " + Upper);
		}
		double code = 0.5 * (Lower + Upper) ;
		code = round(code,4) ;
		//System.out.println(code);
		br.close();
		String strFilePath = "D:\\FCAI-CU material\\Multimedia\\implementations\\ArithmeticCoding/Encoding Output.txt";
		
	    FileOutputStream fos = new FileOutputStream(strFilePath);
	    DataOutputStream dos = new DataOutputStream(fos);
	    dos.writeDouble(code);
	        
	    dos.close();
		return code ;
	}
	
	public static  String BelongsTo (double R) {
		String symbolZone = new String();
		for (int i = 0 ; i < commulativeFrequency.length ; i++) {
		    if ( commulativeFrequency[i] >= R) 	{
		    	symbolZone =  (char)i + "" ;
		    	break ;
		    	}
		}
		return symbolZone ;
	}
	
	public  static double minimumProbability() {
		double min = -1;
		for (Map.Entry<String, Double> entry : probabilities.entrySet()) {
		    if (entry.getValue() < min) min = entry.getValue() ;
		}
		return min ;
	}
	
	public static int kValue () {
		double min = minimumProbability() ;
		int K = 0;
		double counter = 1 ;
		for(K = 0 ; counter <= (1/min) ; K++) {
			counter *= 2 ;
		}
		return K ;
	}
	
	public static StringBuffer Decoding() {
		int k = kValue() ;
		StringBuffer Binary = new StringBuffer("1") ;
		// Add (k-1) zeros
		for(int i = 1 ; i <= k ; i++)	{
			Binary.append('0') ;
		}
		
		
		return Binary ;
	}
	
	public static String Decode () throws IOException {
		
        //("D:\\FCAI-CU material\\Multimedia\\implementations\\ArithmeticCoding/Floating point code.txt");
		BufferedReader brComp = new BufferedReader(new FileReader("D:\\FCAI-CU material\\Multimedia\\implementations\\ArithmeticCoding/Floating point code.txt"));
		//String s= "";
		double code  = Double.parseDouble(brComp.readLine());
		//Scanner sc = new Scanner(System.in);
		//System.out.println("Enter your floating code : ");
		brComp.close();
		prepareProbabilities("");
		
		//System.out.println("in ...");
		
		String originalMessage = new String();
		double Lower = 0, Upper = 1 , before = 0 ,previousLower;
		boolean firstChar = true;
		double R = 0.000000000001 ;
		char c ;
		while(NumberOfSymbols > 0) {
			//System.out.println("in ...");
			if(firstChar == true) {
				R = (code - Lower)/(Upper - Lower);
				c = BelongsTo(R).charAt(0) ;
				originalMessage += c ;
				before = commulativeFrequency[c] - (double)probabilities.get(c+"") ;
				previousLower= Lower; 
				Lower =  previousLower + (Upper - previousLower)* before ;
				Upper = previousLower + (Upper - previousLower)*commulativeFrequency[BelongsTo(R).charAt(0)] ;
				firstChar = false ;
			}
			else {
				R = (code - Lower)/(Upper - Lower);
				originalMessage += BelongsTo(R) ;
				c = BelongsTo(R).charAt(0) ;
				before = commulativeFrequency[BelongsTo(R).charAt(0)] - (double)probabilities.get(BelongsTo(R)) ;
				//System.out.println(BelongsTo(R).charAt(0) + " " +(before));
				previousLower= Lower; 
				Lower =  previousLower + (Upper - previousLower)* before ;
				Upper = previousLower + (Upper - previousLower)*commulativeFrequency[BelongsTo(R).charAt(0)] ;
				//System.out.println("R : " + R);
			}
			//System.out.println(R + " , " + BelongsTo(R));
			//System.out.println("Symbol = " + BelongsTo(R).charAt(0) + "Lower = ");
			NumberOfSymbols-- ;
			System.out.println("Symbol = " + c + " , comm frequency = " + commulativeFrequency[c] + ".. Lower = " + Lower + " , Upper = " + Upper);
			
		}
		BufferedWriter bw = new BufferedWriter
				(new FileWriter
			("D:\\FCAI-CU material\\Multimedia\\implementations\\ArithmeticCoding/Decoding Output.txt"));
		bw.write(originalMessage);
		bw.close();
		return originalMessage ;
	}
	
	public static void main(String[] args) throws IOException {
		for(double d : commulativeFrequency) 	d = 0 ;		
		System.out.println(Encode());
		Decode();
		//System.out.println(probabilities);
		
		
	}

}
