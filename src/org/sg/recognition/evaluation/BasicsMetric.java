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

package org.sg.recognition.evaluation;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 1/12/13 09:16 PM
 */
public class BasicsMetric extends EvaluationMetric {
	private double[] specificity;
	private double[] accuracy;
	private double[] fpr;
	private double[] tpr;
	private double[] ppv;
	private double[] npv;
	private double[] fdr;
	private double[] mcc;
	private double[] f1s;

	public BasicsMetric() {
		super( "BasicsMetric" );
	}

	@Override
	public void evaluate() {
		int[][] confusionMatrix = getConfusionMatrix().getMatrix();
		int length = confusionMatrix.length;

		specificity = new double[length];
		accuracy = new double[length];
		fpr = new double[length];
		tpr = new double[length];
		ppv = new double[length];
		npv = new double[length];
		fdr = new double[length];
		mcc = new double[length];
		f1s = new double[length];

		for( int clazz = 0; clazz < length; clazz++ ) {
			double tp = confusionMatrix[clazz][clazz];
			double fp = 0d;
			double fn = 0d;
			double tn = 0d;
			for( int i = 0; i < length; i++ ) {
				if( i != clazz ) {
					for( int j = 0; j < length; j++ ) {
						if( j == clazz ) {
							fp += confusionMatrix[i][j];
						} else {
							tn += confusionMatrix[i][j];
						}
					}
				} else {
					for( int j = 0; j < length; j++ ) {
						if( j != clazz ) {
							fn += confusionMatrix[i][j];
						}
					}
				}
			}

			specificity[clazz] = tn / ( fp + tn );
			accuracy[clazz] = ( tp + tn ) / ( tp + fp + fn + tn );
			tpr[clazz] = tp / ( tp + fn );
			fpr[clazz] = fp / ( fp + tn );
			ppv[clazz] = tp / ( tp + fp );
			npv[clazz] = tn / ( tn + fn );
			fdr[clazz] = 1 - ppv[clazz];
			mcc[clazz] = ( tp * tn - fp * fn ) / Math.sqrt( ( tp + fp ) * ( tp + fn ) * ( tn + fp ) * ( tn + fn ) );
			f1s[clazz] = 2 * tp / ( 2 * tp + fp + fn );
		}
	}

	public double[] getSpecificity() {
		return specificity;
	}

	public double[] getAccuracy() {
		return accuracy;
	}

	public double[] getFpr() {
		return fpr;
	}

	public double[] getTpr() {
		return tpr;
	}

	public double[] getPpv() {
		return ppv;
	}

	public double[] getNpv() {
		return npv;
	}

	public double[] getFdr() {
		return fdr;
	}

	public double[] getMcc() {
		return mcc;
	}

	public double[] getF1s() {
		return f1s;
	}

}
