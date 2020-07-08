package ReedSol;

import static org.junit.Assert.*;

import org.junit.Test;

public class MatrixTest {

	
	@Test
	public void createMatrix() {
		Matrix matrix = new Matrix(
				new byte [][] {
					new byte[] {1,2,3},
					new byte[] {4,5,6},
					new byte[] {7,8,9}
				});
		assertEquals("[[1, 2, 3], [4, 5, 6], [7, 8, 9]]", matrix.toString());
	}
	
	@Test
	public void setValues() {
		Matrix matrix = new Matrix(
				new byte [][] {
					new byte[] {1,2,3},
					new byte[] {4,5,6},
					new byte[] {7,8,9}
				});
		matrix.set(0, 0, (byte) 200);
		assertEquals("[[200, 2, 3], [4, 5, 6], [7, 8, 9]]", matrix.toString());
	}
	
	@Test
	public void testInverse() {
		Matrix matrix = new Matrix(
                new byte [] [] {
                    new byte [] { 56, 23, 98 },
                    new byte [] { 3, 100, (byte)200 },
                    new byte [] { 45, (byte)201, 123 }
                });
		assertEquals("[[175, 133, 33], [130, 13, 245], [112, 35, 126]]",matrix.invert().toString());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInverseError() {
		Matrix matrix = new Matrix(
				new byte [][] {
					new byte[] {1,2,3,4},
					new byte[] {4,5,6,4},
					new byte[] {7,8,9,4}
				});
		matrix.invert();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void timesError() {
		Matrix m = new Matrix(3,3);
		Matrix m2 = new Matrix(4,4);
		m.times(m2);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void augmentError() {
		Matrix m = new Matrix(3,3);
		Matrix m2 = new Matrix(4,4);
		m.augment(m2);
	}
	
	
	
		

}
