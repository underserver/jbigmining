package org.sg.recognition.validations;

import org.sg.recognition.*;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Author: Sergio Ceron
 * Date: 28/05/13
 * Time: 02:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class LeaveOneOutValidation extends ValidationMethod {

    public LeaveOneOutValidation() {
        super();
    }

    @Override
    public void validate() {
	    Algorithm algorithm = getAlgorithm();

	    DataSet dataSet = getDataSet();

	    for(Pattern instance : dataSet) {
		    Set<Pattern> trainingSet = new DataSet( dataSet );
		    Set<Pattern> testSet = new HashSet<Pattern>();

		    trainingSet.addAll( getDataSet() );
		    trainingSet.remove( instance );
		    testSet.add( instance );

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

}
