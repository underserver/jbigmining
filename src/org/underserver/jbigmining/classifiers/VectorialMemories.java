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

package org.underserver.jbigmining.classifiers;

import org.underserver.jbigmining.core.AlgorithmInformation;
import org.underserver.jbigmining.core.Classifier;
import org.underserver.jbigmining.core.Pattern;
import org.underserver.jbigmining.distances.Distance;
import org.underserver.jbigmining.distances.EuclideanDistance;
import org.underserver.jbigmining.utils.GaussJordan;
import org.underserver.jbigmining.utils.Matrix;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static org.underserver.jbigmining.core.AlgorithmInformation.Field.*;
import static org.underserver.jbigmining.core.AlgorithmInformation.Type.CLASSIFIER;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 15/09/13 01:22 PM
 */
public class VectorialMemories extends Classifier {

	private Distance distance;

	private Memory[] memories;

	public VectorialMemories() {
		super( "VectorialMemories" );
		setDistance( new EuclideanDistance() );
	}

	@Override
	public AlgorithmInformation getInformation() {
		AlgorithmInformation result;

		result = new AlgorithmInformation();
		result.setField( AUTHOR, "Sergio Ceron Figueroa" );
		result.setField( YEAR, "2014" );
		result.setField( TITLE, "Space Vector Memories" );
		result.setField( JOURNAL, "-" );
		result.setField( VOLUME, "-" );
		result.setField( PAGES, "-" );
		result.setField( TYPE, CLASSIFIER );
		result.setField( CATEGORY, "Associative" );

		return result;
	}

	@Override
	public void train() {
		/*memories = new Memory[]{
			new Memory( 0, new Double[][]{
					{ 5.10, 5.10, 5.10, 5.10 }, // Clase 0
					{ 3.80, 3.80, 3.80, 3.80 },
					{ 1.60, 1.60, 1.60, 1.60 },
					{ 0.20, 0.20, 0.20, 0.20 }
			}
			),
			new Memory( 1, new Double[][]{
					{ 5.70, 5.70, 5.70, 5.70 }, // Clase 1
					{ 3.00, 3.00, 3.00, 3.00 },
					{ 4.20, 4.20, 4.20, 3.20 },
					{ 1.40, 1.40, 1.40, 1.40 }
			}
			),
			new Memory( 2, new Double[][]{
					{ 6.50, 6.50, 6.50, 6.50 }, // Clase 2
					{ 3.00, 3.00, 3.00, 3.00 },
					{ 5.20, 5.20, 5.20, 5.20 },
					{ 2.00, 2.00, 2.00, 2.00 }
			}
			)
		};*/
		/*memories = new Memory[]{
				new Memory( 0, new Double[][]{
						{ 5.10, 0.00, 0.00, 0.00 }, // Clase 0
						{ 0.00, 3.40, 0.00, 0.00 },
						{ 0.00, 0.00, 1.50, 0.00 },
						{ 0.00, 0.00, 0.00, 0.40 }
				}
				),
				new Memory( 1, new Double[][]{
						{ 5.70, 0.00, 0.00, 0.00 }, // Clase 1
						{ 0.00, 3.00, 0.00, 0.00 },
						{ 0.00, 0.00, 4.70, 0.00 },
						{ 0.00, 0.00, 0.00, 1.40 }
				}
				),
				new Memory( 2, new Double[][]{
						{ 6.50, 0.00, 0.00, 0.00 }, // Clase 2
						{ 0.00, 2.80, 0.00, 0.00 },
						{ 0.00, 0.00, 6.30, 0.00 },
						{ 0.00, 0.00, 0.00, 2.10 }
				}
				)
		};*/
		memories = new Memory[]{
				new Memory( 0, new Double[][]{
													{ 5.10, 4.80, 5.20, 5.10 }, // Clase 0
													{ 3.80, 3.40, 3.50, 3.70 },
													{ 1.60, 1.90, 1.50, 1.50 },
													{ 0.20, 0.20, 0.20, 0.40 }
											}
				),
				new Memory( 1, new Double[][]{
													{ 5.70, 6.60, 5.9, 5.20 }, // Clase 1
													{ 3.00, 3.00, 3.2, 2.70 },
													{ 4.20, 4.40, 4.70, 3.90 },
													{ 1.20, 1.40, 1.40, 1.40 }
											}
				),
				new Memory( 2, new Double[][]{
													{ 6.50, 6.40, 7.30, 6.70 }, // Clase 2
													{ 3.00, 2.80, 2.90, 3.30 },
													{ 5.20, 5.60, 6.30, 5.70 },
													{ 2.00, 2.10, 1.80, 2.10 }
											}
				)
		};
		/*memories = new Memory[getTrainSet().getDistribution().length];

		Map<Integer, Set<Pattern>> classes = new HashMap<Integer, Set<Pattern>>();
		for( Pattern instance : getTrainSet() ) {
			Set<Pattern> partition = classes.containsKey( instance.getClassIndex() ) ?
					classes.get( instance.getClassIndex() ) :
					new HashSet<Pattern>();
			partition.add( instance );
			classes.put( instance.getClassIndex(), partition );
		}

		for( Integer clazz : classes.keySet() ) {
			Set<Pattern> partition = classes.get( clazz );
			Double _mean[] = mean( partition, getTrainSet().getAttributes().size() );

			Pattern[] nearest = nearestNeighbors( partition, new Pattern( _mean ), _mean.length );   // n vectores para n rasgos

			Double M[][] = new Double[_mean.length][_mean.length];
			int index = 0;
			for( Pattern pattern : nearest ) {
				M[index++] = pattern.toDoubleVector();
			}

			memories[clazz] = new Memory( clazz, Matrix.trans( M ) );
		}

		for( Memory memory : memories ) {
			Matrix.print( memory.getM() );
		}*/

	}

	@Override
	public int classify( Pattern instance ) {
		Map<Double, Integer> variances = new TreeMap<Double, Integer>(); // varianza, clase
		System.out.println("=========================================================");
		for( Memory memory : memories ) {
			double variance = calculateVariance( memory.getM(), instance.toDoubleVector() );
			variances.put( variance, memory.getClazz() );
		}
		int calculated = variances.values().iterator().next();
		return calculated;   // ordenados de menor a mayor, el primero es la menor varianza
	}

	private double calculateVariance( Double[][] A, Double[] b ) {
		GaussJordan gaussian = new GaussJordan( A, b );
		if( gaussian.isFeasible() ) {
			double suma = 0.0;
			double[] x = gaussian.primal();
			for( int i = 0; i < x.length; i++ ) {
				System.out.printf( "%9.2f\t", x[i] );
				suma += Math.abs( x[i] );
			}
			System.out.printf( "\t(%.2f)\t(%.2f)\n", suma, Matrix.varianza( x ) );
			return Matrix.varianza( x );
		} else {
			System.out.println( "no hay solucion" );
			System.out.println();
		}
		return Double.MAX_VALUE;
	}

	public Double[] mean( Set<Pattern> instances, int features ) {
		Double[] mean = new Double[features];
		Arrays.fill( mean, 0d );
		for( Pattern instance : instances ) {
			Double[] vector = instance.toDoubleVector();
			for( int i = 0; i < instance.size(); i++ ) {
				mean[i] += vector[i] / (double) instances.size();
			}
		}
		return mean;
	}

	private double mad(double[] ratios) {
		double[] deviations = new double[ratios.length];
		double med = median(ratios);
		for (int i = 0; i < ratios.length; i++) {
			deviations[i] = Math.abs(ratios[i] - med);
		}
		return median(deviations);
	}

	private double median(double[] ratios) {
		Arrays.sort(ratios);
		int length = ratios.length;
		if (ratios.length == 1) {
			return ratios[0];
		}
		if (length % 2 == 1) {
			return ratios[(length - 1) / 2];
		} else {
			return (ratios[length / 2] + ratios[(length) / 2 - 1]) / 2;
		}
	}

	public Distance getDistance() {
		return distance;
	}

	public void setDistance( Distance distance ) {
		this.distance = distance;
	}

	class Memory {
		private int clazz;
		private Double[][] M;

		Memory( int clazz, Double[][] m ) {
			this.clazz = clazz;
			M = m;
		}

		public int getClazz() {
			return clazz;
		}

		public void setClazz( int clazz ) {
			this.clazz = clazz;
		}

		public Double[][] getM() {
			return M;
		}

		public void setM( Double[][] m ) {
			M = m;
		}
	}
}
