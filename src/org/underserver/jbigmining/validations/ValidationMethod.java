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

package org.underserver.jbigmining.validations;

import org.underserver.jbigmining.core.*;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 28/05/13 02:29 PM
 */
public abstract class ValidationMethod {
	private DataSet dataSet;
	private Algorithm algorithm;
	private ConfusionMatrix confusionMatrix;

	protected ValidationMethod() {
	}

	public void build() {
		if( algorithm instanceof Recuperator ) {
			confusionMatrix = new ConfusionMatrix( 2 );
		} else if( algorithm instanceof Classifier ) {
			confusionMatrix = new ConfusionMatrix( dataSet.getDistribution().length );
		}
	}

	public abstract void validate();

	public DataSet getDataSet() {
		return dataSet;
	}

	public void setDataSet( DataSet dataSet ) {
		this.dataSet = dataSet;
	}

	public Algorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm( Algorithm algorithm ) {
		if( confusionMatrix != null )
			confusionMatrix.init();
		this.algorithm = algorithm;
	}

	public ConfusionMatrix getConfusionMatrix() {
		return confusionMatrix;
	}

	protected synchronized void evaluate( int calculated, int correct ) {
		if( calculated == -1 ) {
			calculated = correct + 1;
			if( calculated >= confusionMatrix.getMatrix().length ) {
				calculated = correct - 1;
			}
		}
		confusionMatrix.add( correct, calculated, 1 );
	}

	protected synchronized void evaluate( Pattern recuperated, Pattern correct ) {
		if( recuperated.equals( correct ) ) {
			confusionMatrix.add( 0, 0, 1 );
		} else {
			confusionMatrix.add( 1, 0, 1 );
		}
	}

}
