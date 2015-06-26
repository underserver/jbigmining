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
import org.underserver.jbigmining.core.DataSet;
import org.underserver.jbigmining.core.Pattern;
import org.underserver.jbigmining.functions.Polynomial;
import org.underserver.jbigmining.functions.ScalarFunction;
import org.underserver.jbigmining.utils.Matrix;

import java.util.*;

import static org.underserver.jbigmining.core.AlgorithmInformation.Field.*;
import static org.underserver.jbigmining.core.AlgorithmInformation.Type.CLASSIFIER;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 15/09/13 01:22 PM
 */
public class LDCA extends Classifier {
	private Double[][][] M;

	private double[] gains;
	private int k = 7;
	private ScalarFunction scalarFunction = new Polynomial();

	public LDCA( int k ) {
		super( "" );
		this.k = k;
		setName( "[" + scalarFunction.getName() + ";" + k + "]" );
	}

	public LDCA( int k, ScalarFunction scalarFunction ) {
		super( "" );
		this.k = k;
		this.scalarFunction = scalarFunction;
		setName( "[" + scalarFunction.getName() + ";" + k + "]" );
	}

	@Override
	public AlgorithmInformation getInformation() {
		AlgorithmInformation result;

		result = new AlgorithmInformation();
		result.setField( AUTHOR, "D. Aha and D. Kibler" );
		result.setField( YEAR, "1991" );
		result.setField( TITLE, "Instance-based learning algorithms" );
		result.setField( JOURNAL, "Machine Learning" );
		result.setField( VOLUME, "6" );
		result.setField( PAGES, "37-66" );
		result.setField( TYPE, CLASSIFIER );
		result.setField( CATEGORY, "Metrics" );

		return result;
	}

	@Override
	public void train() {
		int attributes = getTrainSet().getAttributes().size();
		gains = new double[attributes];
		Set<Pattern> partition = new HashSet<Pattern>( getTrainSet() );
		for( int i = 0; i < attributes; i++ ) {
			gains[i] = gain( partition, i ); //Math.pow(gain( partition, i ), 4)/10000;
		}

		DataSet trainSet = getTrainSet();

		int n = trainSet.getAttributes().size();
		int m = trainSet.getClasses().getValues().size();

		Double[][] MG = Matrix.fill( m, n, -Double.MAX_VALUE );
		for( Pattern instance : trainSet ) {
			Double[] xM = new Double[]{ getScalarFunction().f( instance.toDoubleVector(), gains ) }; //instance.toDoubleVector();
			Double[] yM = Matrix.oneHotD( m, instance.getClassIndex() );
			Double[][] zM = Y( xM, yM );

			MG = Matrix.max( zM, MG );
		}

		// Entrenamiento
		M = new Double[m][][];
		for( int clazz = 0; clazz < m; clazz++ ) {
			M[clazz] = Matrix.fill( m, n, -Double.MAX_VALUE );
			for( Pattern instance : trainSet ) {
				if( instance.getClassIndex() == clazz ) {
					Double[] xM = new Double[]{ getScalarFunction().f(instance.toDoubleVector(), gains ) };
					Double[] yM = Matrix.oneHotD( m, clazz );
					Double[][] zM = Y( xM, yM );

					M[clazz] = Matrix.max( zM, M[clazz] );
				}
			}
		}

	}

	@Override
	public int classify( Pattern instance ) {
		int result = -1;
		Double[] xM = instance.toDoubleVector();

		double min = Double.MAX_VALUE;
		for( int clazz = 0; clazz < getTrainSet().getClasses().getValues().size(); clazz++ ) {
			Double[] yM = Z( M[clazz], new Double[]{ getScalarFunction().f( xM, gains ) } );
			double m = min( yM );
			if( m < min ) {
				min = m;
				result = clazz;
			}
		}
		return result;
	}

	private double gain(Set<Pattern> partition, int feature){
		double s = entropy( partition );

		Map<Double, Set<Pattern>> subpartitions = new HashMap<Double, Set<Pattern>>();
		for( Pattern pattern : partition ) {
			double key = -1;
			if( pattern.get( feature ) != null )
				key = pattern.getDouble( feature );
			if( subpartitions.containsKey( key ) )
				subpartitions.get( key ).add( pattern );
			else {
				Set<Pattern> subpartition = new HashSet<Pattern>();
				subpartition.add( pattern );
				subpartitions.put( key, subpartition);
			}
		}

		for( Set<Pattern> subpartition : subpartitions.values() ) {
			s += -((subpartition.size() / (double)partition.size()) * entropy( subpartition ));
		}

		return s;//(1/Math.pow( s, 2 ))*100;
	}

	private double entropy( Set<Pattern> partition ){
		Map<Integer, Integer> distribution = new HashMap<Integer, Integer>();
		for( Pattern pattern : partition ){
			if( distribution.containsKey( pattern.getClassIndex() ) )
				distribution.put( pattern.getClassIndex(), distribution.get( pattern.getClassIndex() ) + 1 );
			else
				distribution.put( pattern.getClassIndex(), 1 );
		}
		double total = partition.size();
		return entropy( distribution, total );
	}

	private double entropy(Map<Integer, Integer> distribution, double total){
		double s = 0d;
		for( Integer classIndex : distribution.keySet() ) {
			double div = (distribution.get( classIndex ) / total);
			s += -( div * log2( div ));
		}
		return s;
	}

	private double log2(double x){
		return Math.log( x ) / Math.log( 2 );
	}

	public Double[][] Y( Double[] xM, Double[] yM ) {
		Double[][] zM = Matrix.fill( yM.length, xM.length, 0d );
		for( int i = 0; i < yM.length; i++ ) {
			for( int j = 0; j < xM.length; j++ ) {
				zM[i][j] = A( yM[i], xM[j] );
			}
		}
		return zM;
	}

	public Double[] Z( Double[][] M, Double[] xM ) {
		int m1rows = M.length;
		int m1cols = M[0].length;

		Double[] result = new Double[m1rows];

		for( int i = 0; i < m1rows; i++ ) {
			Double _min = Double.MAX_VALUE;
			for( int k = 0; k < m1cols; k++ ) {
				Double b = B( M[i][k], xM[k] );
				_min = b < _min ? b : _min;
			}
			result[i] = _min;
		}

		return result;
	}

	public Double A( Double x, Double y ) {
		return round( x - y );
	}

	public Double B( Double x, Double y ) {
		return round( x + y );
	}

	private Double round( Double value ) {
		return (double) Math.round( value * 100 ) / 100;
	}

	private Double min( Double[] yM ) {
		double min;
		min = yM[0];

		for( Double c : yM ) {
			min = c < min ? c : min;
		}

		return Math.abs( min );
	}


	public int getK() {
		return k;
	}

	public void setK( int k ) {
		this.k = k;
	}

	public ScalarFunction getScalarFunction() {
		return scalarFunction;
	}

	public void setScalarFunction( ScalarFunction scalarFunction ) {
		this.scalarFunction = scalarFunction;
	}
}
