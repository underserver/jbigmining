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

import org.junit.Before;
import org.junit.Test;
import org.underserver.jbigmining.DataSet;
import org.underserver.jbigmining.Parser;
import org.underserver.jbigmining.classifiers.Correlograph;
import org.underserver.jbigmining.evaluation.BasicsMetric;
import org.underserver.jbigmining.evaluation.EvaluationManager;
import org.underserver.jbigmining.evaluation.EvaluationMetric;
import org.underserver.jbigmining.parsers.ARFFParser;
import org.underserver.jbigmining.validations.LeaveOneOutValidation;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 30/12/13 10:08 PM
 */
public class CorrelographTest {
	private static final String FILE = "./banks/iris.arff";

	private DataSet dataSet = null;

	@Before
	public void setUp() throws Exception {
		Parser parser = new ARFFParser( FILE );
		dataSet = parser.parse();
	}

	@Test
	public void testCorrelograph() throws Exception {
		EvaluationManager evaluationManager = new EvaluationManager();
		evaluationManager.setDataSet( dataSet );
		evaluationManager.setAlgorithm( new Correlograph() );
		evaluationManager.setValidationMethod( new LeaveOneOutValidation() );
		evaluationManager.evaluate();

		for( EvaluationMetric metric : evaluationManager.getMetrics() ) {
			if( metric instanceof BasicsMetric ) {
				System.out.println( "\nTPR" );
				double[] tpr = ( (BasicsMetric) metric ).getTpr();
				for( double s : tpr ) {
					System.out.printf( "%.3f\n", s );
				}
				System.out.println( "\nFPR" );
				double[] fpr = ( (BasicsMetric) metric ).getFpr();
				for( double s : fpr ) {
					System.out.printf( "%.3f\n", s );
				}
			}
		}


	}
}
