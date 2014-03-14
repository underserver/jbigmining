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

package org.sg.recognition.classifiers;

import org.sg.recognition.AlgorithmInformation;
import org.sg.recognition.Classifier;
import org.sg.recognition.DataSet;
import org.sg.recognition.Pattern;
import org.sg.recognition.utils.Matrix;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 28/05/13 07:45 PM
 */public class Correlograph extends Classifier {
    private Double[][] M;

    public Correlograph() {
        super("Correlograph");
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

        // Entrenamiento
        M = Matrix.fill(m, n, 0d);
        for ( Pattern instance : trainSet ) {
	        for( int i = 0; i < instance.size(); i++ ) {
		        M[instance.getClassIndex()][i] = instance.toDoubleVector()[i] == 1d ? 1 : M[instance.getClassIndex()][i];
	        }
        }

    }

	@Override
	public int classify( Pattern instance ) {
		Double[][] xM  = Matrix.trans( new Double[][]{ instance.toDoubleVector() } );
		Double[][] yM = Matrix.mult( M, xM );

		double u = Math.log(xM.length)/Math.log( 2 );

		int result = -1, count = 0;
		for( int j = 0; j < yM.length; j++ ) {
			if( yM[j][0] == u ){
				yM[j][0] = 1d;
				result = j;
				count ++;
			} else {
				yM[j][0] = 0d;
			}
		}

		if( count > 1 ) result = -1;

		return result;
	}


}
