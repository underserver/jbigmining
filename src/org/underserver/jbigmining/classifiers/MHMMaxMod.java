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
import org.underserver.jbigmining.DataSet;
import org.underserver.jbigmining.Pattern;
import org.underserver.jbigmining.utils.Matrix;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 28/05/13 07:45 PM
 */
public class MHMMaxMod extends Classifier {
	private Double[][][] M;

	public MHMMaxMod() {
		super( "Memoria Morfologica Heteroasociativa MAX Mod" );
	}

	@Override
	public AlgorithmInformation getInformation() {
		AlgorithmInformation result;

		result = new AlgorithmInformation();
		result.setField( AlgorithmInformation.Field.AUTHOR, "Ritter,Sussner & Diaz-de-Leon" );
		result.setField( AlgorithmInformation.Field.YEAR, "1998" );
		result.setField( AlgorithmInformation.Field.TITLE, "Morphological associative memories" );
		result.setField( AlgorithmInformation.Field.JOURNAL, "IEEE Transactions on Neural Networks" );
		result.setField( AlgorithmInformation.Field.VOLUME, "9" );
		result.setField( AlgorithmInformation.Field.PAGES, "281-293" );
		result.setField( AlgorithmInformation.Field.TYPE, AlgorithmInformation.Type.CLASSIFIER );
		result.setField( AlgorithmInformation.Field.CATEGORY, "Associative" );

		return result;
	}

	@Override
	public void train() {

		DataSet trainSet = getTrainSet();

		int n = trainSet.getAttributes().size();
		int m = trainSet.getClasses().getValues().size();

		Double[][] MG = Matrix.fill( m, n, -Double.MAX_VALUE );
		for( Pattern instance : trainSet ) {
			Double[] xM = instance.toDoubleVector();
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
					Double[] xM = instance.toDoubleVector();
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
			Double[] yM = Z( M[clazz], xM );
			double m = min( yM );
			if( m < min ) {
				min = m;
				result = clazz;
			}
		}

		return result;
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

}
