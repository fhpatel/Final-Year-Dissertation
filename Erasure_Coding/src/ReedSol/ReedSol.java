package ReedSol;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ReedSol {
	
	private final int dataShards;
	private final int parityShards;
	private final int totalShards;
	private final int byteCount;
	private final byte [][] parityRows;
	private final Matrix matrix;
	
	static Galois Galois = new Galois(29);
	
	/*
	 * Initiates the values upon the class being called
	 * it also creates matrix V that shall be used to encode and decode data
	 */
	public ReedSol(int dataShards, int parityShards, int byteCount) {
		this.dataShards = dataShards;
		this.parityShards = parityShards;
		this.totalShards = dataShards + parityShards;
		this.byteCount = byteCount;
		matrix = buildMatrix(dataShards, totalShards);
		parityRows = new byte[parityShards][];
		
		for(int i = 0; i < parityShards; i++) {
			parityRows[i] = matrix.getRowEntire(dataShards + i);
		}

		
	}
	
	/*
	 * buildMatrix() creates a Vandermonde Matrix of datashards x totalShards 
	 */
	public Matrix buildMatrix(int dataShards, int totalShards) {
		Matrix result = vandermonde(totalShards,dataShards);
		Matrix top = result.subMatrix(0,dataShards,0,dataShards);
		top = top.invert();
		return result.times(top);
	}
	
	/*
	 * vandermonde() creates a matrix of r x c and populates it using the exp() 
	 * function from the Galois Class
	 */
	public Matrix vandermonde(int r, int c) {
		Matrix output = new Matrix(r, c);
		for(int x = 0; x < r; x++) {
			for(int y = 0; y < c; y++) {
				byte ans = Galois.exp((byte)x,y);
				output.set(x, y, ans);
			}
		}
		return output;
	}
	
	/*
	 * checkData() verifies that the there are enough enough shards to encode the file correct
	 * and that they are all the same size
	 */
	public void checkData(byte [] [] shards,int offset, int byteCount) {
		if(shards.length != totalShards) {
			throw new IllegalArgumentException("Not enough shards");
		}
		int shardLength = shards[0].length;
		for(int i = 1 ; i < dataShards; i++) {
			if(shards[i].length != shardLength) {
				throw new IllegalArgumentException("Shard size not consistent");
			}
		}
		
		if(byteCount < 0) {
			throw new IllegalArgumentException("Byte count is negative");
		}
		
		if(offset < 0) {
			throw new IllegalArgumentException("Offeset is negative");
		}
	}
	
	/*
	 * encode() creates the parity blocks and outputs them
	 */
	public byte[] [] encode(byte [][] shards, int byteCount, int offset,byte[][]parityRows) {
		checkData(shards,offset,byteCount);
		 byte [] [] outputs = new byte [parityShards] [];
	     System.arraycopy(shards, dataShards, outputs, 0, parityShards);
	     outputs = codeShards(parityRows,shards, outputs, dataShards, parityShards,0,byteCount);
	     return outputs;
	}	
	
	
	/*
	 * codeShards is the process of multiplying a given matrix with a data set
	 * whether that be V x S = D or V^-1 x D' = S
	 */
	public byte[][] codeShards(byte[][]matrixRows, byte[][]inputs, byte[][]outputs, int inputNum, int outputNum, int offset, int byteCount){
		for(int iOutput = 0; iOutput < outputNum; iOutput++) {
			final byte[] outputShard = outputs[iOutput];
			final byte[] matrixRow = matrixRows[iOutput];
			for(int iByte = offset; iByte < offset + byteCount; iByte++) {
				int value = 0;
				for(int iInput = 0; iInput < inputNum; iInput++) {
					final byte[] inputShard = inputs[iInput];
					value ^= Galois.mult(matrixRow[iInput], inputShard[iByte]);
				}
				outputShard[iByte] = (byte) value;
			}
			outputs[iOutput] = outputShard;
		}
		return outputs;
	}
	
	
	/*
	 * decode() is the function to regenerate missing data shards 
	 */
	public byte [] [] decode(byte [][] shards, boolean [] shardsPresent, int offset, int numberShards) {
		if(numberShards < dataShards) {
			throw new IllegalArgumentException();
		}
		
		Matrix subMatrix = new Matrix(dataShards, dataShards);
		byte [] [] subShards = new byte[dataShards][];
		{ //Constructor for this method
			int subMatrixRow = 0;
			for(int matrixRow = 0; matrixRow < totalShards && subMatrixRow < dataShards; matrixRow++) {
				if(shardsPresent[matrixRow] == true) {
					for(int columns = 0; columns < dataShards; columns++) {
						subMatrix.set(subMatrixRow, columns, matrix.getValue(matrixRow, columns));
					}
					subShards[subMatrixRow] = shards[matrixRow];
					subMatrixRow += 1;
					
				}
			}
		}
		
		Matrix decodeMatrix = subMatrix.invert();
		
		
		//Re-generate the data shards missing
		byte [] [] output = new byte[parityShards][];
		byte [] [] matrixRows = new byte[parityShards][];
		
		int outputCount = 0;
		for(int iShard = 0; iShard < dataShards; iShard++) {
			if(shardsPresent[iShard] == false) {
				output[outputCount] = shards[iShard];
				matrixRows[outputCount] = decodeMatrix.getRowEntire(iShard);
				outputCount++;
			}
		}
		
		output = codeShards(matrixRows, subShards, output, dataShards,outputCount,0,byteCount);
		
		return output;
		
	}
	
	/*
	 * getDataShardsNum returns the number of dataShards
	 */
	public int getDataShardsNum() {
		return dataShards;
	}
	
	/*
	 * getParityShardsNum returns the number of parityShards
	 */
	public int getPairtyShardsNum() {
		return parityShards;
	}
	
	/*
	 * getTotalShardsNum returns the total number of shards
	 */
	public int getTotalShardsNum() {
		return totalShards;
	}
	
	/*
	 * getByteCount returns the size of each shards
	 */
	public int getByteCount() {
		return byteCount;
	}
	
	/*
	 * getParityRows returns the parity data
	 */
	public byte[][] getParityRows(){
		return parityRows;
	}
	
	
	
}
