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

package org.underserver.jbigmining.evaluation;

import org.underserver.jbigmining.core.Algorithm;
import org.underserver.jbigmining.core.DataSet;
import org.underserver.jbigmining.validations.KFoldCrossValidation;
import org.underserver.jbigmining.validations.ValidationMethod;

import java.util.Arrays;
import java.util.List;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 1/12/13 08:20 PM
 */
public class EvaluationManager {
	private DataSet dataSet;
	private Algorithm algorithm;
	private ValidationMethod validationMethod;
	private List<EvaluationMetric> metrics;

	public EvaluationManager() {
		metrics = Arrays.asList( new EvaluationMetric[]{ new BasicsMetric() } );
		validationMethod = new KFoldCrossValidation();
	}

	public void evaluate() {
		validationMethod.setDataSet( dataSet );
		validationMethod.setAlgorithm( algorithm );

		validationMethod.build();
		validationMethod.validate();

		System.out.println( validationMethod.getConfusionMatrix() );

		for( EvaluationMetric metric : metrics ) {
			metric.setConfusionMatrix( validationMethod.getConfusionMatrix() );
			metric.evaluate();
		}
	}

	public void addMetric( EvaluationMetric metric ) {
		metrics.add( metric );
	}

	public List<EvaluationMetric> getMetrics() {
		return metrics;
	}

	public ValidationMethod getValidationMethod() {
		return validationMethod;
	}

	public void setValidationMethod( ValidationMethod validationMethod ) {
		this.validationMethod = validationMethod;
	}

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
		this.algorithm = algorithm;
	}
}
