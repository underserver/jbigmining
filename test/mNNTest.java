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

import org.underserver.jbigmining.Algorithm;
import org.underserver.jbigmining.DataSet;
import org.underserver.jbigmining.Parser;
import org.underserver.jbigmining.classifiers.LinearAssociator;
import org.underserver.jbigmining.evaluation.BasicsMetric;
import org.underserver.jbigmining.evaluation.EvaluationManager;
import org.underserver.jbigmining.evaluation.EvaluationMetric;
import org.underserver.jbigmining.filters.Filter;
import org.underserver.jbigmining.parsers.ARFFParser;
import org.underserver.jbigmining.validations.LeaveOneOutValidationThreaded;
import org.underserver.jbigmining.validations.ValidationMethod;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 2/10/13 09:43 PM
 */
public class mNNTest {

	private static final String FILE = "./banks/dbworld_bodies.arff";

	private static final ValidationMethod validationMethod = new LeaveOneOutValidationThreaded();
	private static final Algorithm algorithm = new LinearAssociator();
	private static final Filter[] filters = { /*new NormalizeFilter(), new BinaryFilter()*/ };

	private DataSet dataSet = null;

	public void init() {
		Parser parser = new ARFFParser( FILE );
		dataSet = parser.parse();
		for( Filter filter : filters ) {
			filter.setDataSet( dataSet );
			filter.processAll();
			dataSet = filter.getNewDataSet();
		}
	}

	public void test() {
		EvaluationManager evaluationManager = new EvaluationManager();
		evaluationManager.setDataSet( dataSet );
		evaluationManager.setAlgorithm( algorithm );
		evaluationManager.setValidationMethod( validationMethod );
		evaluationManager.evaluate();

		for( EvaluationMetric metric : evaluationManager.getMetrics() ) {
			if( metric instanceof BasicsMetric ) {
				System.out.println("\nTPR");
				double[] tpr = ( (BasicsMetric) metric ).getTpr();
				for( double s : tpr ) {
					System.out.printf("%.3f\n", s);
				}
				System.out.println("\nFPR");
				double[] fpr = ( (BasicsMetric) metric ).getFpr();
				for( double s : fpr ) {
					System.out.printf("%.3f\n", s);
				}
			}
		}
	}


	public static void main( String[] args ) {
		mNNTest test = new mNNTest();
		test.init();
		test.test();
	}

}
