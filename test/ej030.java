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

import org.underserver.jbigmining.DataSet;
import org.underserver.jbigmining.Parser;
import org.underserver.jbigmining.Pattern;
import org.underserver.jbigmining.parsers.CSVParser;
import org.underserver.jbigmining.utils.Matrix;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 26/06/14 04:26 PM
 */
public class ej030 {

	private static final String FILE = "./banks/iris.csv";

	private DataSet dataSet = null;

	public void init() {
		Parser parser = new CSVParser( FILE );
		dataSet = parser.parse();
	}

	public ej030() {

		init();

		Double d[][] = new Double[50][];
		int index = 0;
		for( Pattern p : dataSet ) {
			if( p.getClassIndex() == 0 ) {
				//A[index++] = p.toDoubleVector();
				//System.out.println( test( p.toDoubleVector() ) );
				d[index++] = p.toDoubleVector();
			}
		}

		/*double A[][] = {
				{1,  3,  2, 5},
				{2,  1,  0, 4},
				{1,  0,  1, 0},
				{4, -1, -3, 5}};*/
		Double A[][] = Matrix.trans( d );

		double b[] = { 0, 0, 0, 0 };
		double x[] = new double[b.length];

		imprime( A );
		EliminacionGaussiana( A, x, b );
		imprime( A );
		imprime( x );
	}

	public static void main( String[] args ) {
		new ej030();
	}

	/**
	 * Metodo de elemininación gaussiana para resolver el sistema ax =b
	 *
	 * @param a
	 * 		double[][]
	 * @param x
	 * 		double[]
	 * @param b
	 * 		double[]
	 */

	static public void EliminacionGaussiana( Double a[][], double x[], double b[] ) {
		int n = a.length;

		for( int k = 0; k <= n - 2; k++ ) {
			for( int i = k + 1; i <= ( n - 1 ); i++ ) {
				b[i] -= a[i][k] * b[k] / a[k][k];
				for( int j = n - 1; j >= k; j-- )
					a[i][j] -= a[i][k] * a[k][j] / a[k][k];
			}
		}
	}

	/**
	 * Descompisicion LU = A utilizando eliminacion gaussiana
	 *
	 * @param a
	 * 		double[][]
	 */

	static public void LU_EG( double a[][] ) {
		int n = a.length;
		double factor;

		for( int k = 0; k <= n - 2; k++ ) {
			for( int i = k + 1; i < n; i++ ) {
				factor = a[i][k] / a[k][k];
				a[i][k] = factor;
				for( int j = k + 1; j < n; j++ )
					a[i][j] -= factor * a[k][j];
			}
		}
	}

	/**
	 * Descomposicion triangula LU de Crout
	 *
	 * @param a
	 * 		double[][]
	 */

	static public void LU( double a[][] ) {
		int n = a.length;
		int i, j, k;
		double suma;

		for( i = 0; i < n; i++ ) {

			for( j = 0; j <= i - 1; j++ ) {
				suma = 0;
				for( k = 0; k <= j - 1; k++ )
					suma += a[i][k] * a[k][j];
				a[i][j] = ( a[i][j] - suma ) / a[j][j];
			}

			for( j = i; j < n; j++ ) {
				suma = 0;
				for( k = 0; k <= i - 1; k++ )
					suma += a[i][k] * a[k][j];
				a[i][j] = a[i][j] - suma;
			}
		}
	}

	/**
	 * Factorizacion de Cholesky
	 *
	 * @param A
	 * 		double[][]
	 */

	static public void Cholesky( double A[][] ) {
		int i, j, k, n, s;
		double fact, suma = 0;

		n = A.length;

		for( i = 0; i < n; i++ ) { //k = i
			for( j = 0; j <= i - 1; j++ ) { //i = j
				suma = 0;
				for( k = 0; k <= j - 1; k++ ) // j = k
					suma += A[i][k] * A[j][k];

				A[i][j] = ( A[i][j] - suma ) / A[j][j];
			}

			suma = 0;
			for( k = 0; k <= i - 1; k++ )
				suma += A[i][k] * A[i][k];
			A[i][i] = Math.sqrt( A[i][i] - suma );
		}
	}

	/**
	 * Resuelve un sistema triangular superior ax=b
	 *
	 * @param a
	 * 		double[][]
	 * @param x
	 * 		double[]
	 * @param b
	 * 		double[]
	 */

	static public void sustitucion_hacia_atras( double a[][], double x[], double b[] ) {
		double suma;
		int k, j;
		int n = x.length;

		for( k = n - 1; k >= 0; k-- ) {
			suma = 0;
			for( j = k + 1; j < n; j++ )
				suma += a[k][j] * x[j];
			x[k] = ( b[k] - suma ) / a[k][k];
		}
	}

	/**
	 * Resuelve un sistema triangular inferior Ay=b
	 *
	 * @param A
	 * 		double[][]
	 * @param y
	 * 		double[]
	 * @param b
	 * 		double[]
	 */


	static public void sustitucion_hacia_delante( double A[][], double y[], double b[] ) {
		int k, i, j, n;
		double suma;

		n = A.length;
		for( k = 0; k < n; k++ ) {
			suma = 0;
			for( j = 0; j < k; j++ ) {
				suma += A[k][j] * y[j];
			}
			y[k] = b[k] - suma;
		}
	}

	/**
	 * Imprime los pasos de la decomposición LU
	 *
	 * @param a
	 * 		double[][]
	 */


	static public void LU_ver( double a[][] ) {
		int n = a.length;
		int i, j, k;
		double suma;

		for( i = 0; i < n; i++ ) {

			for( j = 0; j <= i - 1; j++ ) {
				ver( i, j, 0 );
				System.out.print( "(" );
				ver( i, j, 1 );

				suma = 0;
				for( k = 0; k <= j - 1; k++ ) {
					ver( i, k, 3 );
					ver( k, j, 2 );
					suma += a[i][k] * a[k][j];
				}

				a[i][j] = ( a[i][j] - suma ) / a[j][j];
				ver( j, j, 4 );
				System.out.println();

			}
			for( j = i; j < n; j++ ) {
				suma = 0;

				ver( i, j, 0 );
				ver( i, j, 1 );

				for( k = 0; k <= i - 1; k++ ) {
					ver( i, k, 3 );
					ver( k, j, 2 );
					suma += a[i][k] * a[k][j];
				}

				a[i][j] = a[i][j] - suma;
				System.out.println();
			}

		}
	}

	/**
	 * Imprime los valores en la matriz en la posición i, j
	 *
	 * @param i
	 * 		int
	 * @param j
	 * 		int
	 * @param salto
	 * 		int
	 */

	static public void ver( int i, int j, int salto ) {
		//i++;
		//j++;

		if( salto == 0 ) System.out.print( "a(" + i + "," + j + ") = " );
		else if( salto == 1 ) System.out.print( " a(" + i + "," + j + ") " );
		else if( salto == 2 ) System.out.print( " * a(" + i + "," + j + ") " );
		else if( salto == 3 ) System.out.print( " - a(" + i + "," + j + ") " );
		else System.out.print( ") / a(" + i + "," + j + ") " );
	}

	/**
	 * Imprime el contenido de la matriz A
	 *
	 * @param A
	 * 		double[][]
	 */

	static public void imprime( Double A[][] ) {
		int n = A.length, m = A[0].length;
		for( int i = 0; i < n; i++ ) {
			for( int j = 0; j < m; j++ )
				System.out.printf( "%.2f\t\t", A[i][j] );

			System.out.println( "" );
		}
		System.out.println( "" );
	}

	/**
	 * Imprime el contenido del vector A
	 *
	 * @param A
	 * 		double[]
	 */

	static public void imprime( double A[] ) {
		int n = A.length;
		for( int i = 0; i < n; i++ )
			System.out.println( A[i] );

		System.out.println();

	}
}