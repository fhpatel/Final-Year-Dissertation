package ReedSol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class Encode {
	public final int SHARDS;
	public final int PARITY_SHARDS;
	public final String fileName;
	public static final int SIZE_OF_INT_BYTES = 4;
	public static int count = 0;
	public Encode(int shards, int parity, String fileName){
		SHARDS = shards;
		PARITY_SHARDS = parity;
		this.fileName = fileName;
	}
	
	public void run() throws IOException{
		int totalShards = SHARDS + PARITY_SHARDS;
		final File file = new File("../Erasure_Coding/files/" + fileName);
		if(file.exists() == false) {
			System.out.println("File does not exist so could not open");
			System.out.println(file);
			return;
		}
		//Files that are bigger that integer.maxValue will fail here
		int fileSize = (int) file.length();
		
		int spaceSize = fileSize + SIZE_OF_INT_BYTES; //Space taken up in bytes
		int shardSize = (spaceSize + SHARDS - 1) / SHARDS; //Size of shards
		
		//Create Buffer Size to check that the file has contents
		int bufferSize = shardSize * SHARDS;
		byte[] dataBuffer = new byte[bufferSize];
		ByteBuffer.wrap(dataBuffer).putInt(fileSize);
		InputStream input = new FileInputStream(file);
		int bytesRead = input.read(dataBuffer, SIZE_OF_INT_BYTES, fileSize);
		if(bytesRead != fileSize) {
			throw new IOException("Not enough bytes read");
		}
		input.close();
		
		byte [] [] shardsArray = new byte[totalShards][shardSize];
		for(int i = 0; i < SHARDS; i++) {
			System.arraycopy(dataBuffer, i * shardSize, shardsArray[i], 0, shardSize); 
		}
		
		//System.out.println("Number of Rows: " + data.getRows());
		//System.out.println("Number of Columns: " + data.getColumns());
		System.out.println("File Size: " + fileSize + " bytes");
		System.out.println("Shard Size: " + shardSize + " bytes");
		
		for(int i = 0; i < shardsArray.length; i++) {
			for(int x = 0; x < shardsArray[i].length; x++) {
				if(shardsArray[i][x] == 0) {
					count++;
				}
			}
		}
		ReedSol reedSol = new ReedSol(SHARDS,PARITY_SHARDS,shardSize);
		
		reedSol.checkData(shardsArray, 0, shardSize);
		//checkData(shardsArray,0,SIZE_OF_INT_BYTES);
		Matrix matrix = reedSol.buildMatrix(reedSol.getDataShardsNum(), reedSol.getTotalShardsNum());
		
        byte [] [] output = reedSol.encode(shardsArray, shardSize,0,reedSol.getParityRows());
		
        //Write out the resulting files.
        for (int i = 0; i < totalShards; i++) {
            File outputFile = new File(
                    "..\\Erasure_Coding\\encoded\\" + 
                    file.getName() + "." + i);
            OutputStream out = new FileOutputStream(outputFile);
            out.write(shardsArray[i]);
            out.close();	
            System.out.println("wrote " + outputFile);
        }
        System.out.println(Galois.isZero);
        return; 
		
	}
	
}
