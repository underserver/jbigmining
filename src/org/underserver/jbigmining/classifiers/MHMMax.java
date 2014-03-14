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

import java.util.HashMap;
import java.util.Map;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 28/05/13 07:45 PM
 */
public class MHMMax extends Classifier {

	private Map<Double[], Integer> lookupTable;
	private Double[][] M;

	public MHMMax() {
		super( "Memoria Morfologica Heteroasociativa MAX" );
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
		lookupTable = new HashMap<Double[], Integer>();

		DataSet trainSet = getTrainSet();

		int n = trainSet.getAttributes().size();
		int m = trainSet.size();
		int c = trainSet.getClasses().getValues().size();

		// Entrenamiento
		M = Matrix.fill( m, n, -Double.MAX_VALUE );
		for( Pattern instance : trainSet ) {
			int clazz = instance.getClassIndex();

			Double[] xM = instance.toDoubleVector();
			Double[] yM = Matrix.oneHotD( c, clazz );
			Double[][] zM = Y( xM, yM );

			M = Matrix.max( zM, M );

			lookupTable.put( yM, clazz );
		}

	}

	@Override
	public int classify( Pattern instance ) {
		Double[] xM = instance.toDoubleVector();
		Double[] yM = Z( M, xM );

		if( !lookupTable.containsKey( yM ) )
			return -1;

		return lookupTable.get( yM );
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

}
