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
public class CHAT extends Classifier {
	private static final int E = 1;

	private Double[][] M;
	private Double[] mean;

	public CHAT() {
		super( "CHAT" );
	}

	@Override
	public AlgorithmInformation getInformation() {
		AlgorithmInformation result;

		result = new AlgorithmInformation();

		return result;
	}

	@Override
	public void train() {

		DataSet trainSet = getTrainSet();

		int n = trainSet.getAttributes().size();
		int m = trainSet.getClasses().getValues().size();

		mean = Matrix.fill( n, 0d );

		for( Pattern instance : trainSet ) {
			mean = Matrix.sum( mean, instance.toDoubleVector() );
		}
		mean = Matrix.div( mean, (double) trainSet.size() );

		// Entrenamiento
		M = Matrix.fill( m, n, 0d );
		for( Pattern instance : trainSet ) {
			for( int i = 0; i < instance.size(); i++ ) {
				Double[] xM = Matrix.res( instance.toDoubleVector(), mean );
				Double[] yM = Matrix.oneHotD( m, instance.getClassIndex() );
				M = Matrix.sum( M, Matrix.mult( yM, xM ) );
			}
		}

	}

	@Override
	public int classify( Pattern instance ) {
		Double[][] xM = Matrix.trans( new Double[][]{ Matrix.res( instance.toDoubleVector(), mean ) } );

		Double[][] yM = Matrix.mult( M, xM );

		Double[][] yMCopy = new Double[yM.length][1];
		for( int ix = 0; ix < yM.length; ix++ )
			System.arraycopy( yM[ix], 0, yMCopy[ix], 0, yM[ix].length );

		double max = -Double.MAX_VALUE;
		for( int i = 0; i < yM.length; i++ ) {
			if( yM[i][0] > max ) {
				max = yM[i][0];
			}
		}

		int result = -1;
		for( int j = 0; j < yM.length; j++ ) {
			if( yM[j][0] == max ) {
				yM[j][0] = 1d;
				result = j;
			} else {
				yM[j][0] = 0d;
			}
		}

		return result;
	}


}
