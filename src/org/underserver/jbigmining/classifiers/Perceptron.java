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

import java.util.Arrays;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 9/12/13 11:29 PM
 */
public class Perceptron extends Classifier {
	private double weights[];
	private double threshold;

	public Perceptron() {
		super( "Perceptron Simple" );
		threshold = 0.5;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold( double threshold ) {
		this.threshold = threshold;
	}

	@Override
	public AlgorithmInformation getInformation() {
		return new AlgorithmInformation();
	}

	@Override
	public void train() {
		DataSet dataSet = getTrainSet();

		weights = new double[dataSet.getAttributes().size()];
		Arrays.fill( weights, 1d );

		boolean error = true;
		while( error ) {
			error = false;
			for( Pattern instance : dataSet ) {
				Double[] vector = instance.toDoubleVector();

				double s = 0;
				for( int i = 0; i < weights.length; i++ ) {
					s += vector[i] * weights[i];
				}
				double y = instance.getClassIndex() - s;
				/*weights = Matrix.sum( weights, Matrix.wise( vector, e ) );
				threshold += e;
				error = e < 0.1 ? false : true;*/
			}
		}
	}

	@Override
	public int classify( Pattern instance ) {
		Double[] vector = instance.toDoubleVector();
		double a = threshold;
		for( int i = 0; i < weights.length; i++ ) {
			a += vector[i] * weights[i];
		}
		return (int) a;
	}
}
