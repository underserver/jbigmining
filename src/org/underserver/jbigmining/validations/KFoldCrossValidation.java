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

import java.util.*;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 28/05/13 02:30 PM
 */
public class KFoldCrossValidation extends ValidationMethod {
	private int k;
	private Set<Pattern>[] folds;

	public KFoldCrossValidation() {
		this( 10 );
	}

	public KFoldCrossValidation( int k ) {
		super();
		this.k = k;
		this.folds = new Set[k];
	}

	@Override
	public void build() {
		super.build();

		DataSet dataSet = getDataSet();

		// Calculate fold size per class
		int[] distribution = dataSet.getDistribution();
		long foldSizes[] = new long[distribution.length];
		for( int i = 0; i < distribution.length; i++ ) {
			foldSizes[i] = (long) Math.ceil( (double) distribution[i] / (double) k );
		}

		// Initialize folds
		for( int i = 0; i < k; i++ ) {
			folds[i] = new HashSet<Pattern>();
		}

		// TODO: optimizar para que fold sea solo de los que estan libres para recibir mas instancias
		// Send instance to random fold instead of send random instance to one fold
		Random rnd = new Random();
		int[][] foldMatrix = new int[k][distribution.length]; // folds x classes
		for( Pattern instance : dataSet ) {
			int clazz = instance.getClassIndex();
			while( true ) {
				int fold = rnd.nextInt( k );
				if( foldMatrix[fold][clazz] < foldSizes[clazz] ) {
					folds[fold].add( instance );
					foldMatrix[fold][clazz] += 1;
					break;
				}
			}
		}

	}

	@Override
	public void validate() {
		Algorithm algorithm = getAlgorithm();

		for( int foldIndex = 0; foldIndex < k; foldIndex++ ) {

			List<Pattern> testSet = new ArrayList<Pattern>( folds[foldIndex] );
			List<Pattern> trainingSet = new DataSet( getDataSet() );
			for( int fold = 0; fold < k; fold++ ) {
				if( fold != foldIndex ) {
					trainingSet.addAll( folds[fold] );   // TODO: revisar si addAll llama al metodo heredado de AbastractCollection o al implementado por DataSet
				}
			}

			algorithm.setTrainSet( (DataSet) trainingSet );
			algorithm.train();
			for( Pattern instance : testSet ) {
				if( algorithm instanceof Classifier ) {
					int calculated = ( (Classifier) algorithm ).classify( instance );
					int correct = instance.getClassIndex();
					super.evaluate( calculated, correct );
				} else if( algorithm instanceof Recuperator ) {
					Pattern recuperated = ( (Recuperator) algorithm ).recover( instance );
					super.evaluate( recuperated, instance );
				}
			}

		}
	}

	public int getK() {
		return k;
	}

	public void setK( int k ) {
		this.k = k;
	}
}
