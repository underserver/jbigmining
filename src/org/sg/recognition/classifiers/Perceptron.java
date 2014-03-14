package org.sg.recognition.classifiers;

import org.sg.recognition.AlgorithmInformation;
import org.sg.recognition.Classifier;
import org.sg.recognition.DataSet;
import org.sg.recognition.Pattern;
import org.sg.recognition.utils.Matrix;

import java.util.Arrays;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 9/12/13 11:29 PM
 */
public class Perceptron extends Classifier {
	private double weights[];
	private double threshold;

	public Perceptron() {
		super( "Perceptron Simple" );
		threshold = 0.5;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold( double threshold ) {
		this.threshold = threshold;
	}

	@Override
	public AlgorithmInformation getInformation() {
		return new AlgorithmInformation();
	}

	@Override
	public void train() {
		DataSet dataSet = getTrainSet();

		weights = new double[dataSet.getAttributes().size()];
		Arrays.fill( weights, 1d );

		boolean error = true;
		while( error ) {
			error = false;
			for( Pattern instance : dataSet ) {
				Double[] vector = instance.toDoubleVector();

				double s = 0;
				for( int i = 0; i < weights.length; i++ ) {
					s  += vector[i] * weights[i];
				}
				double y = instance.getClassIndex() - s;
				/*weights = Matrix.sum( weights, Matrix.wise( vector, e ) );
				threshold += e;
				error = e < 0.1 ? false : true;*/
			}
		}
	}

	@Override
	public int classify( Pattern instance ) {
		Double[] vector = instance.toDoubleVector();
		double a = threshold;
		for( int i = 0; i < weights.length; i++ ) {
			a  += vector[i] * weights[i];
		}
		return (int) a;
	}
}
