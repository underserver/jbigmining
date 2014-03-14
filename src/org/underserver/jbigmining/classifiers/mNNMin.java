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
import org.underserver.jbigmining.recuperators.MAMMin;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 15/09/13 01:22 PM
 */
public class mNNMin extends Classifier {
	private MAMMin mammax = new MAMMin();
	private kNN knn = new kNN();
	private int m = 3;

	public mNNMin() {
		super( "mNNMin" );
	}

	public mNNMin( int m ) {
		super( "mNNMin" );
		this.m = m;
	}

	@Override
	public AlgorithmInformation getInformation() {
		AlgorithmInformation result;

		result = new AlgorithmInformation();
		result.setField( AlgorithmInformation.Field.AUTHOR, "Sergio-Ceron, Yañez-Márquez & Itzamá-López" );
		result.setField( AlgorithmInformation.Field.TITLE, "Morphological kNN-based classifier" );
		result.setField( AlgorithmInformation.Field.YEAR, "2013" );
		result.setField( AlgorithmInformation.Field.TYPE, AlgorithmInformation.Type.CLASSIFIER );
		result.setField( AlgorithmInformation.Field.CATEGORY, "Associative" );

		return result;
	}

	@Override
	public void train() {
		knn.setK( m );
		mammax.setTrainSet( getTrainSet() );
		mammax.train();
		knn.setTrainSet( getTrainSet() );
		knn.train();
	}

	@Override
	public int classify( Pattern instance ) {
		Pattern recovered = mammax.recover( instance );
		return knn.classify( recovered );
	}

	public int getM() {
		return m;
	}

	public void setM( int m ) {
		this.m = m;
	}
}
