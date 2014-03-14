package org.sg.recognition.validations;

import org.sg.recognition.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * Author: Sergio Ceron
 * Date: 28/05/13
 * Time: 02:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class SuppliedSetValidation extends ValidationMethod {
	private DataSet suppliedSet;

	public SuppliedSetValidation( DataSet suppliedSet ) {
		super();
		this.suppliedSet = suppliedSet;
	}

	@Override
	public void validate() {
		Algorithm algorithm = getAlgorithm();

		Set<Pattern> trainingSet = getDataSet();
		Set<Pattern> testSet = suppliedSet;

		algorithm.setTrainSet( (DataSet) trainingSet );
		algorithm.train();
		for( Pattern testInstance : testSet ) {
			if( algorithm instanceof Classifier ) {
				int calculated = ( (Classifier) algorithm ).classify( testInstance );
				int correct = testInstance.getClassIndex();
				super.evaluate( calculated, correct );
			} else if( algorithm instanceof Recuperator ) {
				Pattern recuperated = ( (Recuperator) algorithm ).recover( testInstance );
				super.evaluate( recuperated, testInstance );
			}
		}
	}

}
