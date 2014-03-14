package org.sg.recognition.utils;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * Author: Antonio, Elias, Cero
 * Date: 28/05/13
 * Time: 08:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class Matrix {

	public static Double[][] mult(Double a[][], Double b[][]) {
		int m1rows = a.length;
		int m1cols = a[0].length;
		int m2rows = b.length;
		int m2cols = b[0].length;

		if (m1cols != m2rows)
			throw new IllegalArgumentException("matrices don't match: " + m1cols + " != " + m2rows);

		//T[][] result = new T[m1rows][m2cols];
		Double[][] result = Matrix.fill(m1rows, m2cols, 0d);

		for (int i = 0; i < m1rows; i++) {
			for (int j = 0; j < m2cols; j++) {
				for (int k = 0; k < m1cols; k++) {
					result[i][j] += a[i][k] * b[k][j];
				}
			}
		}

		return result;
	}

	public static Double[][] mult(Double column[], Double row[]) {
		int rows = column.length;
		int cols = row.length;

		Double[][] result = Matrix.fill(rows, cols, 0d);

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				result[i][j] = column[i] * row[j];
			}
		}

		return result;
	}

	public static Integer[][] mult(Integer a[][], Integer b[][]) {
		int m1rows = a.length;
		int m1cols = a[0].length;
		int m2rows = b.length;
		int m2cols = b[0].length;

		if (m1cols != m2rows)
			throw new IllegalArgumentException("matrices don't match: " + m1cols + " != " + m2rows);

		//T[][] result = new T[m1rows][m2cols];
		Integer[][] result = Matrix.fill(m1rows, m2cols, 0);

		for (int i = 0; i < m1rows; i++) {
			for (int j = 0; j < m2cols; j++) {
				for (int k = 0; k < m1cols; k++) {
					result[i][j] += a[i][k] * b[k][j];
				}
			}
		}

		return result;
	}

    public static Double[][] trans(Double a[][]) {
        Double[][] result = new Double[a[0].length][a.length];
        for(int i = 0; i < a.length; i++){
            for (int j = 0; j < a[0].length; j++){
                result[j][i] = a[i][j];
            }
        }

        return result;
    }

	public static Integer[][] trans(Integer a[][]) {
		Integer[][] result = new Integer[a[0].length][a.length];
		for(int i = 0; i < a.length; i++){
			for (int j = 0; j < a[0].length; j++){
				result[j][i] = a[i][j];
			}
		}

		return result;
	}


	public static Double[][] sum(Double a[][], Double b[][]) {
        Double[][] result = new Double[a.length][a[0].length];
        for(int i = 0; i < a.length; i++){
            for (int j = 0; j < a[0].length; j++){
                result[i][j] = a[i][j] + b[i][j];
            }
        }
        return result;
    }

	public static double[] sum(double a[], double b[]) {
		double[] result = new double[a.length];
		for(int i = 0; i < a.length; i++){
			result[i] = a[i] + b[i];
		}
		return result;
	}

	public static double[] wise(Double a[], double b) {
		double[] result = new double[a.length];
		for(int i = 0; i < a.length; i++){
			result[i] = a[i] * b;
		}
		return result;
	}

    public static boolean compare(Double a[], Double b[]) {
        for(int i = 0; i < a.length; i++){
            if(a[i].doubleValue() != b[i].doubleValue()){
                return false;
            }
        }
        return true;
    }

    public static Double[][] fill(int rows, int cols, Double value) {
        Double[][] result = new Double[rows][cols];
        for(int i = 0; i < rows; i++){
            result[i] = new Double[cols];
            for (int j = 0; j < cols; j++){
                result[i][j] = value;
            }
        }
        return result;
    }

    public static Integer[][] fill(int rows, int cols, Integer value) {
        Integer[][] result = new Integer[rows][cols];
        for(int i = 0; i < rows; i++){
            result[i] = new Integer[cols];
            for (int j = 0; j < cols; j++){
                result[i][j] = value;
            }
        }
        return result;
    }

	public static Long[][] fill(int rows, int cols, Long value) {
		Long[][] result = new Long[rows][cols];
		for(int i = 0; i < rows; i++){
			result[i] = new Long[cols];
			for (int j = 0; j < cols; j++){
				result[i][j] = value;
			}
		}
		return result;
	}

	public static BigDecimal[][] fill(int rows, int cols, BigDecimal value) {
		BigDecimal[][] result = new BigDecimal[rows][cols];
		for(int i = 0; i < rows; i++){
			result[i] = new BigDecimal[cols];
			for (int j = 0; j < cols; j++){
				result[i][j] = value;
			}
		}
		return result;
	}

    public static Double[][] max(Double[][]... matrices) {
        int r = matrices[0].length;
        int c = matrices[0][0].length;
        Double[][] maxMatrix = new Double[r][c];
        for(int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                Double _max = -Double.MAX_VALUE;
                for ( Double[][] matrix : matrices ){
                    _max = matrix[i][j] > _max ? matrix[i][j] : _max;
                }
                maxMatrix[i][j] = _max;
            }
        }
        return maxMatrix;
    }

    public static Integer[][] max(Integer[][]... matrices) {
        int r = matrices[0].length;
        int c = matrices[0][0].length;
        Integer[][] maxMatrix = new Integer[r][c];
        for(int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                Integer _max = -Integer.MAX_VALUE;
                for ( Integer[][] matrix : matrices ){
                    _max = matrix[i][j] > _max ? matrix[i][j] : _max;
                }
                maxMatrix[i][j] = _max;
            }
        }
        return maxMatrix;
    }

    public static Double[][] min(Double[][]... matrices) {
        int r = matrices[0].length;
        int c = matrices[0][0].length;
        Double[][] maxMatrix = new Double[r][c];
        for(int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                Double _min = Double.MAX_VALUE;
                for ( Double[][] matrix : matrices ){
                    _min = matrix[i][j] < _min ? matrix[i][j] : _min;
                }
                maxMatrix[i][j] = _min;
            }
        }
        return maxMatrix;
    }

	public static Integer[][] min(Integer[][]... matrices) {
		int r = matrices[0].length;
		int c = matrices[0][0].length;
		Integer[][] maxMatrix = new Integer[r][c];
		for(int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {
				Integer _min = Integer.MAX_VALUE;
				for ( Integer[][] matrix : matrices ){
					try{
						_min = matrix[i][j] < _min ? matrix[i][j] : _min;
					} catch(ArrayIndexOutOfBoundsException e) {
						e.printStackTrace();
					}
				}
				maxMatrix[i][j] = _min;
			}
		}
		return maxMatrix;
	}

	public static Integer[] oneHotI(int dimension, int k){
		Integer[] one = new Integer[dimension];
		for(int i = 0; i < dimension; i++) {
			if( i == k ) {
				one[i] = 1;
			} else {
				one[i] = 0;
			}
		}
		return one;
	}

	public static Double[] oneHotD(int dimension, int k){
		Double[] one = new Double[dimension];
		for(int i = 0; i < dimension; i++) {
			if( i == k ) {
				one[i] = 1d;
			} else {
				one[i] = 0d;
			}
		}
		return one;
	}

	public static Double[] kHotD(int dimension, int pos, double k){
		Double[] one = new Double[dimension];
		for(int i = 0; i < dimension; i++) {
			if( i == pos ) {
				one[i] = k;
			} else {
				one[i] = 0d;
			}
		}
		return one;
	}

    public static Double euclideanDistance( Double[] a, Double[] b ) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum = sum + Math.pow( ( a[i] - b[i] ), 2.0 );
        }
        return Math.sqrt(sum);
    }

	public static Double cityblockDistance( Double[] a, Double[] b ) {
		double sum = 0.0;
		for (int i = 0; i < a.length; i++) {
			sum = sum + Math.abs( ( a[i] - b[i] ) );
		}
		return sum;
	}

    public static Double euclideanDistance(Integer[] a, Integer[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum = sum + Math.pow( ( a[i] - b[i] ), 2.0 );
        }
        return Math.sqrt(sum);
    }

}
