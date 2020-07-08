package ReedSol;

public class Matrix {
	private final int rows;
	private final int columns;
	private final byte [] [] data;
	private final Galois Galois = new Galois(29);
	/*
	 * This constructor creates a matrix based upon the dimensions passed in
	 */
	public Matrix(int initRows, int initColumns) {
		rows = initRows;
		columns = initColumns;
		data = new byte[rows][];
		for(int i = 0; i < rows; i++) {
			data[i] = new byte[columns];
		}
	}
	/*
	 * This constructor creates a matrix based upon a data passed in
	 */
	public Matrix(byte [] [] initData) {
		rows = initData.length;
		columns = initData[0].length;
		data = new byte[rows][];
		for(int i = 0; i < rows; i++) {
			if(initData[i].length != columns) {
				throw new IllegalArgumentException("Not a vandermonde matrix");
			}
			data[i] = new byte[columns];
			for(int c = 0; c < columns; c++) {
				data[i][c] = initData[i][c];
			}
		}
	}
	
	/*
	 * set() allows for a specific field's values to be set 
	 */
	public void set(int r, int c, byte value){
		data[r][c] = value;
	}
	
	/*
	 *toString() prints the matrix into a readable string format
	 */
	@Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append('[');
        for (int r = 0; r < rows; r++) {
            if (r != 0) {
                result.append(", ");
            }
            result.append('[');
            for (int c = 0; c < columns; c++) {
                if (c != 0) {
                    result.append(", ");
                }
                result.append(data[r][c] & 0xFF);
            }
            result.append(']');
        }
        result.append(']');
        return result.toString();
    }
	/*
	 * times() multiplies two matrices together
	 */
	public Matrix times(Matrix second) {
		if(getColumns() != second.getRows()) {
			throw new IllegalArgumentException("Matrix 1 coulumns != Matrix 2 rows");
		}else {
			System.out.println("Passed");
		}
		Matrix result = new Matrix(getRows(), second.getColumns());
		
		for(int r = 0; r < getRows(); r++) {
			for(int c = 0; c < second.getColumns(); c++) {
				byte temp = 0;
				for(int i = 0; i < getColumns(); i++) {
					temp ^= Galois.mult(getValue(r,i),second.getValue(i, c));
				}
				result.set(r, c, temp);
			}
		}
		return result;
	}
	
	/*
	 * getRows() returns the number of rows in a specific matrix
	 */
	public int getRows() {
		return rows;
	}
	
	/*
	 * getRowEntire() returns the entire contents of specific row r
	 */
	public byte[] getRowEntire(int r) {
		byte[]output = new byte[getColumns()];
		for(int c = 0; c < getColumns(); c++) {
			output[c] = getValue(r,c);
		}
		return output;
	}
	
	/*
	 * getColumns() return the number of columns in a specific matrix
	 */
	public int getColumns() {
		return columns;
	}
	
	/*
	 * getValue() returns a specific value at a specific point in the matrix
	 */
	public byte getValue(int row, int column) {
		return data[row][column];
	}
	
	/*
	 * subMatrix() returns a sub matrix specified by the parameters 
	 */
	public Matrix subMatrix(int rBegin, int rEnd, int cBegin, int cEnd) {
		Matrix output = new Matrix(rEnd-rBegin, cEnd-cBegin);
		for(int x = rBegin; x < rEnd; x++) {
			for(int y = cBegin; y < cEnd; y++) {
				byte temp = data[x][y];
				output.set(x-rBegin, y-cBegin, temp);
			}
		}
		return output;
	}
	
	/*
	 * identit() creates an identity matrix of size "size"
	 */
	public static Matrix identity(int size) {
		Matrix output = new Matrix(size,size);
		for(int x = 0; x < size; x++) {
			for(int y = 0; y < size; y++) {
				if(x == y) {
					output.set(x, y, (byte) 1);
				}else {
					output.set(x, y, (byte) 0);
				}
			}
		}
		return output;
	}
	
	/*
	 * augment() joins two matrices together privided the numebr of rows are the same
	 */
	public Matrix augment(Matrix second) {
		if(getRows() != second.getRows()) {
			throw new IllegalArgumentException("Number of rows arent the same");
		}
		Matrix output = new Matrix(getRows(), (getColumns() + second.getColumns()));
		for(int x = 0; x < getRows(); x++) {
			for(int y = 0; y < getColumns(); y++) {
				output.set(x,y,data[x][y]);
			}
			for(int y = 0; y < second.getColumns(); y++) {
				output.set(x, getColumns() + y, second.getValue(x,y));
			}
		}
		
		return output;
	}
	
	/*
	 * swapRows() swaps row a and b in a matrix
	 */
	public void swapRows(int a, int b) {
		byte [] temp = data[a];
		data[a] = data[b];
		data[b] = temp;
	}
	
	/*
	 * invert() produces the inverse of a given matrix
	 */
	public Matrix invert() {
		if(rows != columns) {
			throw new IllegalArgumentException("Only square matrices can be inverted");
		}
		
		Matrix identity = identity(rows);
		
		
		Matrix output = augment(identity(rows));
		
		output.gaussianElimination();
		
		return output.subMatrix(0, columns, rows, (columns*2));
	}
	
	
	
	/*
	 * It should be noted that this gauusianElimination() method is not mine. I attempted several times to write my own but kept failing
	 * and so due to the time constraints this is the implemented Gaussian Eliminaiton method from Backblaze's implementation of Reed-Solomon that
	 * can be found here: https://github.com/Backblaze/JavaReedSolomon/blob/master/src/main/java/com/backblaze/erasure/Galois.java
	 */
	private void gaussianElimination() {
        // Clear out the part below the main diagonal and scale the main
        // diagonal to be 1.
        for (int r = 0; r < rows; r++) {
            // If the element on the diagonal is 0, find a row below
            // that has a non-zero and swap them.
            if (data[r][r] == (byte) 0) {
                for (int rowBelow = r + 1; rowBelow < rows; rowBelow++) {
                    if (data[rowBelow][r] != 0) {
                        swapRows(r, rowBelow);
                        break;
                    }
                }
            }
            // If we couldn't find one, the matrix is singular.
            if (data[r][r] == (byte) 0) {
                throw new IllegalArgumentException("Matrix is singular");
            }
            // Scale to 1.
            if (data[r][r] != (byte) 1) {
                byte scale = Galois.divide((byte) 1, data[r][r]);
                for (int c = 0; c < columns; c++) {
                    data[r][c] = Galois.mult(data[r][c], scale);
                }
            }
            // Make everything below the 1 be a 0 by subtracting
            // a multiple of it.  (Subtraction and addition are
            // both exclusive or in the Galois field.)
            for (int rowBelow = r + 1; rowBelow < rows; rowBelow++) {
                if (data[rowBelow][r] != (byte) 0) {
                    byte scale = data[rowBelow][r];
                    for (int c = 0; c < columns; c++) {
                        data[rowBelow][c] ^= Galois.mult(scale, data[r][c]);
                    }
                }
            }
        }

        // Now clear the part above the main diagonal.
        for (int d = 0; d < rows; d++) {
            for (int rowAbove = 0; rowAbove < d; rowAbove++) {
                if (data[rowAbove][d] != (byte) 0) {
                    byte scale = data[rowAbove][d];
                    for (int c = 0; c < columns; c++) {
                        data[rowAbove][c] ^= Galois.mult(scale, data[d][c]);
                    }

                }
            }
        }
    }
	
}
