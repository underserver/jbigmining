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
import org.underserver.jbigmining.core.Algorithm;
import org.underserver.jbigmining.core.DataSet;
import org.underserver.jbigmining.core.Parser;
import org.underserver.jbigmining.evaluation.EvaluationManager;
import org.underserver.jbigmining.exceptions.ParserException;
import org.underserver.jbigmining.functions.*;
import org.underserver.jbigmining.parsers.SmartParser;
import org.underserver.jbigmining.validations.LeaveOneOutValidation;
import org.underserver.jbigmining.validations.LeaveOneOutValidationThreaded;
import org.underserver.jbigmining.validations.LeaveOneOutValidationThreadedRecursive;
import org.underserver.jbigmining.validations.ValidationMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 2/10/13 09:43 PM
 */
public class EucliedianTest {

	private static final String DATASET = "./datasets/%s/%s.arff";

	private static ValidationMethod validationMethod = new LeaveOneOutValidationThreadedRecursive();
	private static final Algorithm[] algorithms = {
			new LDC(1)/*, new LDC(7),
			new LDC(1, new PolynomialInv()), new LDC(7, new PolynomialInv()),
			new LDC(1, new PolynomialInv2()), new LDC(7, new PolynomialInv2()),
			new LDC(1, new Trigonometric()), new LDC(1, new TrigonometricInv()),
			new LDC(7, new Trigonometric()), new LDC(7, new TrigonometricInv()),
			new LDC(1, new Exponential()), new LDC(7, new Exponential()),
			new LDC(1, new Schwefel()), new LDC(1, new SchwefelGains()),
			new LDC(7, new Schwefel()), new LDC(7, new SchwefelGains()),
			new LDC(1, new Eggholder()), new LDC(7, new Eggholder())*/
	};
	private static final String[] datasets = { "glass" , /*"tae", "tic-tac-toe", "wine", "ecoli", "breast-cancer", "lymph", "glass", "diabetes", "anneal", "audiology", "autos", "breast-w", "dermatology", "ecoli", "heart-c", "heart-h", "heart-statlog", "wine", "vote", "credit-a", "credit-g", "cylinder-bands", "haberman", "hayes-roth", "hepatitis", "ionosphere", "labor", "liver-disorders", "lung-cancer", "postoperative-patient-data", "car", "nursery", "kr-vs-kp" */};
	//private static final String[] datasets = { "credit-a", "credit-g", "cylinder-bands", "haberman", "hayes-roth", "hepatitis", "ionosphere", "kr-vs-kp", "labor", "liver-disorders", "lung-cancer", "nursery", "postoperative-patient-data" };

	private List<DataSet> ds = new ArrayList<DataSet>();

	public void init() throws ParserException, IOException {
		for( String dataset : datasets ) {
			Parser trainParser = new SmartParser( String.format( DATASET, dataset, dataset ) );
			ds.add( trainParser.parse() );
		}
	}

	public void test() {
		EvaluationManager evaluationManager = new EvaluationManager();
		evaluationManager.setDatasets( ds );
		evaluationManager.setAlgorithms( Arrays.asList(algorithms) );
		evaluationManager.setValidationMethod( validationMethod );
		evaluationManager.evaluate();
	}


	public static void main( String[] args ) throws ParserException, IOException {
		EucliedianTest test = new EucliedianTest();
		test.init();
		test.test();
	}

}
