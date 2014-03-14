package org.sg.recognition.classifiers;

import org.sg.recognition.AlgorithmInformation;
import org.sg.recognition.Classifier;
import org.sg.recognition.Pattern;
import org.sg.recognition.recuperators.MAMMin;

/**
 * Project Name: PatternRecognition
 * Project Url: http://www.dotrow.com/projects/java/jcase
 * Author: Sergio Ceron
 * Version: 1.0
 * Date: 15/09/13 01:22 PM
 * Desc:
 */
public class mNNMin extends Classifier {
	private MAMMin mammax = new MAMMin();
	private kNN knn = new kNN();
	private int m = 3;

	public mNNMin() {
		super( "mNNMin" );
	}

	public mNNMin( int m ) {
		super( "mNNMin" );
		this.m = m;
	}

	@Override
	public AlgorithmInformation getInformation() {
		AlgorithmInformation result;

		result = new AlgorithmInformation();
		result.setField( AlgorithmInformation.Field.AUTHOR, "Sergio-Ceron, Yañez-Márquez & Itzamá-López" );
		result.setField( AlgorithmInformation.Field.TITLE, "Morphological kNN-based classifier" );
		result.setField( AlgorithmInformation.Field.YEAR, "2013" );
		result.setField( AlgorithmInformation.Field.TYPE, AlgorithmInformation.Type.CLASSIFIER );
		result.setField( AlgorithmInformation.Field.CATEGORY, "Associative" );

		return result;
	}

	@Override
	public void train() {
		knn.setK( m );
		mammax.setTrainSet( getTrainSet() );
		mammax.train();
		knn.setTrainSet( getTrainSet() );
		knn.train();
	}

	@Override
	public int classify( Pattern instance ) {
		Pattern recovered = mammax.recover( instance );
		return knn.classify( recovered );
	}

	public int getM() {
		return m;
	}

	public void setM( int m ) {
		this.m = m;
	}
}
