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

import org.underserver.jbigmining.classifiers.*;
import org.underserver.jbigmining.core.*;
import org.underserver.jbigmining.evaluation.BasicsMetric;
import org.underserver.jbigmining.functions.Eggholder;
import org.underserver.jbigmining.functions.*;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 18/03/14 11:46 AM
 */
public class ValidationThreadRecursive implements Runnable {
	private static final Algorithm[] algorithms = {
			new LDC(1), new LDC(7),
			new LDC(1, new PolynomialInv()), new LDC(7, new PolynomialInv()),
			new LDC(1, new PolynomialInv2()), new LDC(7, new PolynomialInv2()),
			new LDC(1, new Trigonometric()), new LDC(1, new TrigonometricInv()),
			new LDC(7, new Trigonometric()), new LDC(7, new TrigonometricInv()),
			new LDC(1, new Exponential()), new LDC(7, new Exponential()),
			new LDC(1, new Schwefel()), new LDC(1, new SchwefelGains()),
			new LDC(7, new Schwefel()), new LDC(7, new SchwefelGains()),
			new LDC(1, new Eggholder()), new LDC(7, new Eggholder())
	};
	private int index;
	private Pattern instance;
	private DataSet trainSet;
	private Algorithm algorithm;
	private ValidationMethod validationMethod;

	public ValidationThreadRecursive( ValidationMethod validationMethod, int index ) {
		this.validationMethod = validationMethod;
		this.index = index;
	}

	public void setAlgorithm( Algorithm algorithm ) {
		this.algorithm = algorithm;
	}

	public void setInstance( Pattern instance ) {
		this.instance = instance;
	}

	public void setTrainSet( DataSet trainSet ) {
		this.trainSet = trainSet;
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		synchronized( algorithm ) {
			Algorithm bestAlgorithm = null;
			Algorithm knownAlgorithm = null;
			double bestPerformance = 0d;
			System.out.println(index + " - Global instance: " + instance);
			double perfKnown = 0d;
			for( Algorithm _algorithm : algorithms ) {
				ValidationMethod validation = new LeaveOneOutValidation();
				validation.setAlgorithm( _algorithm );
				validation.setDataSet( trainSet );
				validation.build();
				validation.validate();

				BasicsMetric metric = new BasicsMetric();
				metric.setConfusionMatrix( validation.getConfusionMatrix() );
				metric.evaluate();

				if( _algorithm.getName().equals( "[g^(1/x);1]" ) ){
					//System.out.println(_algorithm.getName());
					perfKnown = metric.getPerformance();
					knownAlgorithm = _algorithm;
				}


				if( metric.getPerformance() > bestPerformance ) {
					bestAlgorithm = _algorithm;
					bestPerformance = metric.getPerformance();
				}

				//System.out.printf("%d - Algorithm: %s, Performance: %.4f\n", index, _algorithm.getName(), metric.getPerformance());
			}

			System.out.printf("%d - Best Algorithm: %s\n", index, bestAlgorithm.getName());
			System.out.printf("%d - Best Performance: %.4f\n", index, bestPerformance);
			System.out.printf("%d - Known Performance: %.4f\n", index, perfKnown);

			bestAlgorithm.setTrainSet( trainSet );
			bestAlgorithm.train();

			knownAlgorithm.setTrainSet( trainSet );
			knownAlgorithm.train();

			int calculated = ( (Classifier) bestAlgorithm ).classify( instance );
			int knowncalculated = ( (Classifier) knownAlgorithm ).classify( instance );
			int correct = instance.getClassIndex();
			validationMethod.evaluate( calculated, correct );

			System.out.printf( "%d - Calculated: %d, Real: %d, Correct: %b\n", index, calculated, correct, calculated == correct );
			System.out.printf( "%d - Known Calculated: %d, Real: %d, Correct: %b\n", index, knowncalculated, correct, knowncalculated == correct );
		}
		long end = System.currentTimeMillis();

		//System.out.println( "Partial Time: " + ( end - start ) );

	}
}
