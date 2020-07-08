package ReedSol;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Random;
import org.apache.commons.io.*;

public class main {
	private static String fileName;
	private static int shards;
	private static int pB;
	public ExpLog expLog = new ExpLog();
	
	
	public static void main(String[] arg) throws IOException {
		setUp();
		long eStartTime = System.nanoTime();
		Encode encode = new Encode(shards,pB,fileName);
		encode.run();
		long eEndTime = System.nanoTime();
		deleteFiles();
		long dStartTime = System.nanoTime(); 
		Decode decode = new Decode(shards,pB,fileName);
		decode.run();
		long dEndTime = System.nanoTime();
		checkEquals();
		double eTime = (double) (eEndTime - eStartTime) / 1000000.0;
		double dTime = (double) (dEndTime - dStartTime) / 1000000.0;
		System.out.println("Encoding Time: " + eTime);
		System.out.println("Decoding Time: " + dTime);
	}
	
	/*
	 * checkEquals compares the content of the original file and the decoded file 
	*/
	public static void checkEquals() throws IOException {
		File original = new File("../Erasure_Coding/files/" + fileName);
		File decoded = new File("../Erasure_coding/decoded/DECODED_" + fileName);
		boolean same = FileUtils.contentEquals(original, decoded);
		if(same) {
			System.out.println("File decoded and regenerated correctly");
		}else { 
			System.out.println("File decoded is not the same as the original file");
		}
	}
	
	/*
	 * setUp() allows the users to declare the file they would like to test and dicate the number
	 * of shard and parity data blocks they would like to create
	 */
	public static void setUp() {
		Scanner info = new Scanner(System.in);
		System.out.print("Please enter the file you would like to test: ");
		fileName = info.nextLine();
		System.out.print("\nPlease enter the number of shards you would like to create: ");
		shards = info.nextInt();
		System.out.print("\nPlease enter the number of parity data blocks you would like to create: ");
		pB = info.nextInt();
	}
	/*
	 * deleteFile randomly parses through the data sets and deletes a number a files equivalent to the number of data blocks
	 * created
	 */
	public static void deleteFiles() {
		Random rand = new Random();
		int i = 0;
		while (i < pB) {
			int n = rand.nextInt(shards + pB);
			File file = new File("../Erasure_Coding/encoded/" + fileName + "." + n);
			if(file.delete()) {
				i++;
			}
		}
		
	}
	/*
	 * This returns a reference to the Experimental Logarithm table class that can be accessed
	 * by the Galois Class
	 */
	public ExpLog rExpObject() {
		return expLog;
	}

	
}
