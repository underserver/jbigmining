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
		long foldSizes[] = new long[ distribution.length ];
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
				if( foldMatrix[fold][clazz] < foldSizes[clazz] ){
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

			Set<Pattern> testSet = new HashSet<Pattern>( folds[foldIndex] );
			Set<Pattern> trainingSet = new DataSet( getDataSet() );
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
