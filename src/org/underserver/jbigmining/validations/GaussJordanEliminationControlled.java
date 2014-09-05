/*
 * Copyright (c) %today.year Sergio Ceron Figueroa
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL COPYRIGHT HOLDERS OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.underserver.jbigmining.validations;/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 26/06/14 06:27 PM
 */

import org.underserver.jbigmining.DataSet;
import org.underserver.jbigmining.Parser;
import org.underserver.jbigmining.Pattern;
import org.underserver.jbigmining.parsers.CSVParser;
import org.underserver.jbigmining.utils.Matrix;

import java.util.*;

/**
 * **********************************************************************
 * Compilation:  javac GaussJordanElimination.java
 * Execution:    java GaussJordanElimination N
 * <p/>
 * Finds a solutions to Ax = b using Gauss-Jordan elimination with partial
 * pivoting. If no solution exists, find a solution to yA = 0, yb != 0,
 * which serves as a certificate of infeasibility.
 * <p/>
 * % java GaussJordanElimination
 * -1.000000
 * 2.000000
 * 2.000000
 * <p/>
 * 3.000000
 * -1.000000
 * -2.000000
 * <p/>
 * System is infeasible
 * <p/>
 * -6.250000
 * -4.500000
 * 0.000000
 * 0.000000
 * 1.000000
 * <p/>
 * System is infeasible
 * <p/>
 * -1.375000
 * 1.625000
 * 0.000000
 * <p/>
 * <p/>
 * ***********************************************************************
 */

public class GaussJordanEliminationControlled {
	private static final double EPSILON = 1e-8;

	private final int N;      // N-by-N system
	private double[][] a;     // N-by-N+1 augmented matrix

	// Gauss-Jordan elimination with partial pivoting
	public GaussJordanEliminationControlled( Double[][] A, Double[] b ) {
		N = b.length;

		// build augmented matrix
		a = new double[N][N + N + 1];
		for( int i = 0; i < N; i++ )
			for( int j = 0; j < N; j++ )
				a[i][j] = A[i][j];

		// only needed if you want to find certificate of infeasibility (or compute inverse)
		for( int i = 0; i < N; i++ )
			a[i][N + i] = 1.0;

		for( int i = 0; i < N; i++ ) a[i][N + N] = b[i];

		solve();

		assert check( A, b );
	}

	private void solve() {

		// Gauss-Jordan elimination
		for( int p = 0; p < N; p++ ) {
			// show();

			// find pivot row using partial pivoting
			int max = p;
			for( int i = p + 1; i < N; i++ ) {
				if( Math.abs( a[i][p] ) > Math.abs( a[max][p] ) ) {
					max = i;
				}
			}

			// exchange row p with row max
			swap( p, max );

			// singular or nearly singular
			if( Math.abs( a[p][p] ) <= EPSILON ) {
				continue;
				// throw new ArithmeticException("Matrix is singular or nearly singular");
			}

			// pivot
			pivot( p, p );
		}
		// show();
	}

	// swap row1 and row2
	private void swap( int row1, int row2 ) {
		double[] temp = a[row1];
		a[row1] = a[row2];
		a[row2] = temp;
	}


	// pivot on entry (p, q) using Gauss-Jordan elimination
	private void pivot( int p, int q ) {

		// everything but row p and column q
		for( int i = 0; i < N; i++ ) {
			double alpha = a[i][q] / a[p][q];
			for( int j = 0; j <= N + N; j++ ) {
				if( i != p && j != q ) a[i][j] -= alpha * a[p][j];
			}
		}

		// zero out column q
		for( int i = 0; i < N; i++ )
			if( i != p ) a[i][q] = 0.0;

		// scale row p (ok to go from q+1 to N, but do this for consistency with simplex pivot)
		for( int j = 0; j <= N + N; j++ )
			if( j != q ) a[p][j] /= a[p][q];
		a[p][q] = 1.0;
	}

	// extract solution to Ax = b
	public double[] primal() {
		double[] x = new double[N];
		for( int i = 0; i < N; i++ ) {
			if( Math.abs( a[i][i] ) > EPSILON )
				x[i] = a[i][N + N] / a[i][i];
			else if( Math.abs( a[i][N + N] ) > EPSILON )
				return null;
		}
		return x;
	}

	// extract solution to yA = 0, yb != 0
	public double[] dual() {
		double[] y = new double[N];
		for( int i = 0; i < N; i++ ) {
			if( ( Math.abs( a[i][i] ) <= EPSILON ) && ( Math.abs( a[i][N + N] ) > EPSILON ) ) {
				for( int j = 0; j < N; j++ )
					y[j] = a[i][N + j];
				return y;
			}
		}
		return null;
	}

	// does the system have a solution?
	public boolean isFeasible() {
		return primal() != null;
	}

	// print the tableaux
	private void show() {
		for( int i = 0; i < N; i++ ) {
			for( int j = 0; j < N; j++ ) {
				System.out.printf( "%8.3f ", a[i][j] );
			}
			System.out.printf( "| " );
			for( int j = N; j < N + N; j++ ) {
				System.out.printf( "%8.3f ", a[i][j] );
			}
			System.out.printf( "| %8.3f\n", a[i][N + N] );
		}
		System.out.println();
	}


	// check that Ax = b or yA = 0, yb != 0
	private boolean check( Double[][] A, Double[] b ) {

		// check that Ax = b
		if( isFeasible() ) {
			double[] x = primal();
			for( int i = 0; i < N; i++ ) {
				double sum = 0.0;
				for( int j = 0; j < N; j++ ) {
					sum += A[i][j] * x[j];
				}
				if( Math.abs( sum - b[i] ) > EPSILON ) {
					System.out.println( "not feasible" );
					System.out.printf( "b[%d] = %8.3f, sum = %8.3f\n", i, b[i], sum );
					return false;
				}
			}
			return true;
		}

		// or that yA = 0, yb != 0
		else {
			double[] y = dual();
			for( int j = 0; j < N; j++ ) {
				double sum = 0.0;
				for( int i = 0; i < N; i++ ) {
					sum += A[i][j] * y[i];
				}
				if( Math.abs( sum ) > EPSILON ) {
					System.out.println( "invalid certificate of infeasibility" );
					System.out.printf( "sum = %8.3f\n", sum );
					return false;
				}
			}
			double sum = 0.0;
			for( int i = 0; i < N; i++ ) {
				sum += y[i] * b[i];
			}
			if( Math.abs( sum ) < EPSILON ) {
				System.out.println( "invalid certificate of infeasibility" );
				System.out.printf( "yb  = %8.3f\n", sum );
				return false;
			}
			return true;
		}
	}

	public static double varianza( double[] vals ) {
		double mean = 0.0;
		for( int i = 0; i < vals.length; i++ ) {
			mean += vals[i];
		}
		mean = mean / (double) vals.length;

		double var = 0.0;
		for( int i = 0; i < vals.length; i++ ) {
			var += vals[i] * vals[i];
		}
		var = var / (double) vals.length - mean * mean;

		return var;
	}

	public static double desviacion( double[] vals ) {
		double mean = 0.0;
		for( int i = 0; i < vals.length; i++ ) {
			mean += vals[i];
		}
		mean = mean / (double) vals.length;

		double desv = 0.0;
		for( int i = 0; i < vals.length; i++ ) {
			desv += Math.abs( vals[i] - mean );
		}
		desv = desv / (double) vals.length;

		return desv;
	}

	public static double mean( double[] vals ) {
		double mean = 0.0;
		for( int i = 0; i < vals.length; i++ ) {
			mean += vals[i];
		}
		mean = mean / (double) vals.length;

		return mean;
	}

	public static double test( Double[][] A, Double[] b ) {
		GaussJordanEliminationControlled gaussian = new GaussJordanEliminationControlled( A, b );
		if( gaussian.isFeasible() ) {
			//System.out.println( "Solution to Ax = b" );
			//imprime( gaussian.a );
			double suma = 0.0, suma2 = 0.0;
			double[] x = gaussian.primal();
			for( int i = 0; i < x.length; i++ ) {
				System.out.printf( "%9.2f\t", x[i] );
				suma += Math.abs( x[i] );
				if( x[i] >= 0 )
					suma2 += x[i];
			}
			System.out.printf( "\t(%.2f)\t(%.2f)\t(%6.2f)\t(%.4f)\t(%.4f)\n", suma, suma2, varianza( x ), desviacion( x ), mean( x ) );
			return suma;
		} else {
			System.out.println( "no hay solucion" );
			System.out.println();
		}
		return Double.MAX_VALUE;
	}

	// sample client
	public static void main( String[] args ) {

		Parser parser = new CSVParser( "./banks/controlled.csv" );
		DataSet dataSet = parser.parse();

		int REPRESENTANTES = 3;

		List<Pattern> list = new LinkedList<Pattern>( dataSet );
		Collections.shuffle( list );
		Set<Pattern> randomSet = new HashSet<Pattern>( list );

		int index;
		Double d[][];

		d = new Double[REPRESENTANTES][];
		index = 0;
		for( Pattern p : randomSet ) {
			if( p.getClassIndex() == 0 && index < REPRESENTANTES ) {
				d[index++] = p.toDoubleVector();
			}
		}
		Double A[][] = Matrix.trans( d );

		d = new Double[REPRESENTANTES][];
		index = 0;
		for( Pattern p : randomSet ) {
			if( p.getClassIndex() == 1 && index < REPRESENTANTES ) {
				d[index++] = p.toDoubleVector();
			}
		}
		Double B[][] = Matrix.trans( d );

		/*d = new Double[REPRESENTANTES][];
		index = 0;
		for( Pattern p : randomSet ){
			if( p.getClassIndex() == 2 && index < REPRESENTANTES ){
				d[index++] = p.toDoubleVector();
			}
		}
		Double C[][] = Matrix.trans( d );*/


		imprime( A );
		imprime( B );
		//imprime( C );

		int incorrectos = 0;
		for( Pattern p : dataSet ) {
			double s1 = test( A, p.toDoubleVector() );
			double s2 = test( B, p.toDoubleVector() );
			//double s3 = test( C, p.toDoubleVector());

			List<Double> values = new ArrayList<Double>();
			values.add( s1 );
			values.add( s2 );
			//values.add( s3 );

			double min = Collections.min( values );

			if( min == s1 && p.getClassIndex() == 0 )
				System.out.println( "Correcto" );
			else if( min == s2 && p.getClassIndex() == 1 )
				System.out.println( "Correcto" );
			/*else if( min == s3 && p.getClassIndex() == 2 )
				System.out.println("Correcto");*/
			else {
				System.out.println( "Error // " + p.getClassIndex() );
				incorrectos++;
			}
			System.out.println( "====================================" );
		}

		System.out.println( ( 1 - incorrectos / (double) dataSet.size() ) * 100 );


	}

	public static void imprime( Double A[][] ) {
		int n = A.length, m = A[0].length;
		for( int i = 0; i < n; i++ ) {
			for( int j = 0; j < m; j++ )
				System.out.printf( "%.2f\t\t", A[i][j] );

			System.out.println( "" );
		}
		System.out.println( "" );
	}

	public static void imprime( double A[][] ) {
		int n = A.length, m = A[0].length;
		for( int i = 0; i < n; i++ ) {
			for( int j = 0; j < m; j++ )
				System.out.printf( "%.2f\t\t", A[i][j] );

			System.out.println( "" );
		}
		System.out.println( "" );
	}

}
