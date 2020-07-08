package ReedSol;
import java.util.List;
import java.util.ArrayList;

public class Galois {
	public final int FIELD_SIZE = 256;
	
	public static int isZero = 0;
	public short[] logTable = new short[] {};
	public byte[] expTable = new byte[] {};
	public main main = new main();
	public ExpLog eLog;

	public Galois(int polynomial) {
		logTable = generatefLog(polynomial);
		expTable = generatefExp(logTable);
		eLog = main.rExpObject();
	}
	/*
	 * generatefLog generates the logTable to be used in Galois Field arithmetic
	 */
	public short[] generatefLog(int polynomial) {
		short[] output = new short[FIELD_SIZE];
		for(int i = 0; i < FIELD_SIZE; i++) {
			output[i] = -1;
		}
		int b = 1;
		for(int log = 0; log < FIELD_SIZE - 1; log++ ) {
			if(output[b] != -1) {
				throw new RuntimeException();
			}
			output[b] = (short)log;
			b = (b << 1);
			if(FIELD_SIZE <= b) {
				b = ((b - FIELD_SIZE) ^ polynomial);
			}
		}
		return output;
	}
	
	/*
	 * generatefExp generates the expontential table to be used in Galois Field arithemtic
	 */
	public byte[] generatefExp(short[] logTable){
		final byte [] result = new byte[FIELD_SIZE * 2 - 2];
		for(int i = 1; i < FIELD_SIZE; i++) {
			int log = logTable[i];
			result[log] = (byte) i;
			result[log + FIELD_SIZE - 1] = (byte) i;
		}
		return result;
	}
	
	/*
	 * The original mult()
	 */
	/*public byte mult(byte a, byte b) {
		int sum_log = 0;
		if(a == 0 || b == 0) {
			isZero++;
			return 0;
		}
		sum_log = logTable[a & 0xFF] + logTable[b & 0xFF];
		return expTable[sum_log];
	}*/
	
	/*
	 * This is the new mult() that uses a two-dimensional array look-up
	 */
	public byte mult(byte a, byte b) {
		int sum_log = 0;
		if(a == 0 || b == 0) {
			return 0;
		}
		sum_log = eLog.expLookUp(a & 0xFF,b & 0xFF);
		return expTable[sum_log];
	}
	
	/*
	 * divide() divides a and b across a Galois field 
	 */
	public byte divide(byte a, byte b) {
		int diff_log;
		if(a == 0) {
			return 0;
		}else if(b == 0) {
			throw new IllegalArgumentException();
		}
		diff_log = logTable[a & 0xFF] - logTable[b & 0xFF];
		if(diff_log < 0) {
			diff_log = diff_log + 255;
		}
		return expTable[diff_log];
	}
	
	/*
	 * exp() calculates a^b over a Galois Field 
	 */
	public byte exp(byte a, int b) {
		if(b == 0) {
			return 1;
		}else if(a == 0) {
			return 0;
		}else {
			int log = logTable[a & 0xFF];
			int logResult = log * b;
			while(255 <= logResult) {	
				logResult -= 255;
			}
			return expTable[logResult];
		}
	}
	
	/*
	 * add() executes a + b 
	 */
	public byte add(byte a, byte b) {
		return (byte) (a ^ b);
	}
	
	/*
	 * sub() executes a - b
	 */
	public byte sub(byte a, byte b) {
		return (byte)(a ^ b);
	}
	public short[] getLogTable() {
		return logTable;
	}
	
	public byte[] getExpTable() {
		return expTable;
	}
	
	//Generates polynomial list 29,43,45,77,95,99,101,105,113,135,141,169,195,207,231,245 f ones to be used 
	public Integer[] generatePolynomials() {
		List<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < FIELD_SIZE; i++) {
			try {
				generatefLog(i);
				list.add(i);
			}catch(RuntimeException e){
				
			}
		}
		
		return list.toArray(new Integer[list.size()]);
	}
	
}
