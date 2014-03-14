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
public class HoldOutValidation extends ValidationMethod {
    private int percent = 70;
	private Set<Pattern> trainingSet;
	private Set<Pattern> testSet;

    public HoldOutValidation( int percent ) {
        super();
        this.percent = percent;
    }

	// in this case are two folds for training and test
	@Override
	public void build() {
		super.build();

		DataSet dataSet = getDataSet();

		trainingSet = new DataSet( dataSet );
		testSet = new HashSet<Pattern>();

		// Calculate fold size per class
		int[] distribution = dataSet.getDistribution();
		long foldSizes[] = new long[ distribution.length ];
		for( int i = 0; i < distribution.length; i++ ) {
			foldSizes[i] = Math.round( ((double) distribution[i] * percent) / 100d );
		}

		// Send instance to random fold instead of send random instance to one fold
		Random rnd = new Random();
		int[][] foldMatrix = new int[2][distribution.length]; // folds x classes
		for( Pattern instance : dataSet ) {
			int clazz = instance.getClassIndex();
			while( true ) {
				int fold = rnd.nextInt( 2 );  // (0=train, 1=test)
				if( foldMatrix[fold][clazz] < foldSizes[clazz] ){
					if( fold == 0 ) {
						trainingSet.add( instance );
					} else {
						testSet.add( instance );
					}
					foldMatrix[fold][clazz] += 1;
					break;
				}
			}
		}

	}

    @Override
    public void validate() {
	    Algorithm algorithm =  getAlgorithm();

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

	public int getPercent() {
		return percent;
	}

	public void setPercent( int percent ) {
		this.percent = percent;
	}
}
