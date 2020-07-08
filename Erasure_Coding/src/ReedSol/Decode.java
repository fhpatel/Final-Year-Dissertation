package ReedSol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class Decode {
	private final int SHARDS;
	private final int PARITY_SHARDS;
	private final String fileName;
	private static final int SIZE_OF_INT_BYTES = 4;
	
	public Decode(int shards, int parity, String fileName) {
		SHARDS = shards;
		PARITY_SHARDS = parity;
		this.fileName = fileName;
	}
	
	public void run() throws IOException {
		System.out.println("DECRYPTING");
		byte [] [] shardsArray = new byte[SHARDS + PARITY_SHARDS][];
		boolean [] shardsPresent = new boolean[SHARDS + PARITY_SHARDS];
		int totalShards = SHARDS + PARITY_SHARDS;
		int count = 0;
		int shardSize = 0;
		for(int i = 0; i < (SHARDS + PARITY_SHARDS); i++){
			File piece = new File("../Erasure_Coding/encoded/" + fileName + "." + i);
			if(piece.exists()) {
				count++;
				shardSize = (int) piece.length();
				shardsPresent[i] = true;
				shardsArray[i] = new byte [shardSize];
				InputStream input = new FileInputStream(piece);
				input.read(shardsArray[i],0,shardSize);
				input.close();
			}
		}
		
		ReedSol reedSol = new ReedSol(SHARDS, PARITY_SHARDS, shardSize);
		//Check to make sure that at least n data shards have been recovered
		
		if(count < SHARDS) {
			System.out.println("Thre arent enough shards to recover the file");
			return; //Not enough shard's the number of shards 
		}
		
		if(count == SHARDS + PARITY_SHARDS) {
			byte [] combine = new byte[shardSize * SHARDS];
			for(int i = 0; i < SHARDS; i++) {
				System.arraycopy(shardsArray[i],0,combine,i * shardSize, shardSize);
			}
			int size = ByteBuffer.wrap(combine).getInt();
			File output = new File("../Erasure_Coding/decoded/DECODED_" + fileName);
			OutputStream out = new FileOutputStream(output);
			out.write(combine,SIZE_OF_INT_BYTES,size);
			out.close();
			System.out.println("wrote " + output);
			return; 
		}
		
		byte [] [] recovered = new byte[totalShards - count][];
		
		for(int i = 0; i < (SHARDS + PARITY_SHARDS); i++) {
			if(shardsPresent[i] == false) {
				shardsArray[i] = new byte[shardSize];
			}
		}
		
		
		reedSol.checkData(shardsArray,0,shardSize);
		recovered = reedSol.decode(shardsArray, shardsPresent, 0, shardSize);
		
		int rec = 0;
		for(int x = 0; x < shardsPresent.length; x++) {
			if(shardsPresent[x] == false) {
				shardsArray[x] = recovered[rec];
				rec++;
			}
		}

		//Reconstruct the file
		byte [] allBytes = new byte[shardSize * SHARDS];
		for(int i = 0; i < SHARDS; i++) {
			System.arraycopy(shardsArray[i],0,allBytes,i * shardSize, shardSize);
		}
		int fileSize = ByteBuffer.wrap(allBytes).getInt();
		File output = new File("../Erasure_Coding/decoded/DECODED_" + fileName);
		OutputStream out = new FileOutputStream(output);
		out.write(allBytes,SIZE_OF_INT_BYTES,fileSize);
		out.close();
		System.out.println("wrote " + output);
		return;
	}
	
	
	
	
}
