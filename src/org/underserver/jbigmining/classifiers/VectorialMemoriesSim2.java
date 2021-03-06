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

import org.underserver.jbigmining.AlgorithmInformation;
import org.underserver.jbigmining.Classifier;
import org.underserver.jbigmining.Pattern;
import org.underserver.jbigmining.distances.Distance;
import org.underserver.jbigmining.distances.EuclideanDistance;
import org.underserver.jbigmining.utils.GaussJordan;
import org.underserver.jbigmining.utils.Matrix;

import java.util.*;

import static org.underserver.jbigmining.AlgorithmInformation.Field.*;
import static org.underserver.jbigmining.AlgorithmInformation.Type.CLASSIFIER;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 15/09/13 01:22 PM
 */
public class VectorialMemoriesSim2 extends Classifier {

	private Distance distance;

	private Memory[] memories;

	public VectorialMemoriesSim2() {
		super( "VectorialMemoriesSim2" );
		setDistance( new EuclideanDistance() );
	}

	@Override
	public AlgorithmInformation getInformation() {
		AlgorithmInformation result;

		result = new AlgorithmInformation();
		result.setField( AUTHOR, "Sergio Ceron Figueroa" );
		result.setField( YEAR, "2014" );
		result.setField( TITLE, "Space Vector Memories Sim" );
		result.setField( JOURNAL, "-" );
		result.setField( VOLUME, "-" );
		result.setField( PAGES, "-" );
		result.setField( TYPE, CLASSIFIER );
		result.setField( CATEGORY, "Associative" );

		return result;
	}

	@Override
	public void train() {
		int N = getTrainSet().getDistribution().length;
		memories = new Memory[N];

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

			int R = getTrainSet().getAttributes().size() + 6;
			Double M[][] = Matrix.fill( R, R, 0.0 );
			int index = 0;

			for( Pattern pattern : partition ) {
				if( index >= R ) break;
				M[index] = Matrix.complete( pattern.toDoubleVector(), R );
				//M[index][index] = 0.0;
				index++;
			}

			memories[clazz] = new Memory( clazz, Matrix.trans( M ) );
		}

		for( Memory memory : memories ) {
			Matrix.print( memory.getM() );
		}

	}

	@Override
	public int classify( Pattern instance ) {
		Map<Double, Integer> variances = new TreeMap<Double, Integer>(); // varianza, clase

		for( Memory memory : memories ) {
			double variance = variance( memory.getM(), Matrix.complete( instance.toDoubleVector(), 10 ) );
			variances.put( variance, memory.getClazz() );
		}

		return variances.values().iterator().next();   // ordenados de menor a mayor, el primero es la menor varianza
	}

	private double variance( Double[][] A, Double[] b ) {
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

	public int similitud( Double[] a, Double b[] ) {
		int sim = 0;
		for( int i = 0; i < a.length; i++ ) {
			if( a[i].equals( b[i] ) ) {
				sim++;
			}
		}
		return sim;
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

		Memory() {
		}

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
