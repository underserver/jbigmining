package org.sg.recognition.validations;

import java.util.Arrays;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 27/11/13 10:07 PM
 */
public class ConfusionMatrix {
	private int[][] matrix;

	public ConfusionMatrix( int size ) {
		matrix = new int[size][size];
		init();
	}

	public void set(int i, int j, int val) {
		matrix[i][j] = val;
	}

	public int get(int i, int j){
		return matrix[i][j];
	}

	public void add(int i, int j, int val) {
		matrix[i][j] += val;
	}

	public void init() {
		for( int i = 0; i < matrix.length; i++ ) {
			for( int j = 0; j < matrix.length; j++ ) {
				matrix[i][j] = 0;
			}
		}
	}

	public int[][] getMatrix() {
		return matrix;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for( int i = 0; i < matrix.length; i++ ) {
			for( int j = 0; j < matrix.length; j++ ) {
				sb.append( matrix[i][j] );
				sb.append( " " );
			}
			sb.append( "\n" );
		}
		return sb.toString();
	}
}
