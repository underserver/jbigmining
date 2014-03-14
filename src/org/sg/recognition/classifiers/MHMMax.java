package org.sg.recognition.classifiers;

import org.sg.recognition.AlgorithmInformation;
import org.sg.recognition.Classifier;
import org.sg.recognition.DataSet;
import org.sg.recognition.Pattern;
import org.sg.recognition.utils.Matrix;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 28/05/13 07:45 PM
 */public class MHMMax extends Classifier {

	private Map<Double[], Integer> lookupTable;
	private Double[][] M;

	public MHMMax() {
		super( "Memoria Morfologica Heteroasociativa MAX" );
	}

	@Override
	public AlgorithmInformation getInformation() {
		AlgorithmInformation result;

		result = new AlgorithmInformation();
		result.setField( AlgorithmInformation.Field.AUTHOR, "Ritter,Sussner & Diaz-de-Leon" );
		result.setField( AlgorithmInformation.Field.YEAR, "1998" );
		result.setField( AlgorithmInformation.Field.TITLE, "Morphological associative memories" );
		result.setField( AlgorithmInformation.Field.JOURNAL, "IEEE Transactions on Neural Networks" );
		result.setField( AlgorithmInformation.Field.VOLUME, "9" );
		result.setField( AlgorithmInformation.Field.PAGES, "281-293" );
		result.setField( AlgorithmInformation.Field.TYPE, AlgorithmInformation.Type.CLASSIFIER );
		result.setField( AlgorithmInformation.Field.CATEGORY, "Associative" );

		return result;
	}

	@Override
	public void train() {
		lookupTable = new HashMap<Double[], Integer>();

		DataSet trainSet = getTrainSet();

		int n = trainSet.getAttributes().size();
		int m = trainSet.size();
		int c = trainSet.getClasses().getValues().size();

		// Entrenamiento
		M = Matrix.fill( m, n, -Double.MAX_VALUE );
		for( Pattern instance : trainSet ) {
			int clazz = instance.getClassIndex();

			Double[]  xM = instance.toDoubleVector();
			Double[]  yM = Matrix.oneHotD( c, clazz );
			Double[][] zM = Y( xM, yM );

			M = Matrix.max( zM, M );

			lookupTable.put( yM, clazz );
		}

	}

	@Override
	public int classify( Pattern instance ) {
		Double[] xM = instance.toDoubleVector();
		Double[] yM = Z( M, xM );

		if( !lookupTable.containsKey( yM ) )
			return -1;

		return lookupTable.get( yM );
	}

	public Double[][] Y( Double[] xM, Double[] yM ) {
		Double[][] zM = Matrix.fill( yM.length, xM.length, 0d );
		for( int i = 0; i < yM.length; i++ ) {
			for( int j = 0; j < xM.length; j++ ) {
				zM[i][j] = A( yM[i], xM[j] );
			}
		}
		return zM;
	}

	public Double[] Z( Double[][] M, Double[] xM ) {
		int m1rows = M.length;
		int m1cols = M[0].length;

		Double[] result = new Double[m1rows];

		for( int i = 0; i < m1rows; i++ ) {
			Double _min = Double.MAX_VALUE;
			for( int k = 0; k < m1cols; k++ ) {
				Double b = B( M[i][k], xM[k] );
				_min = b < _min ? b : _min;
			}
			result[i] = _min;
		}

		return result;
	}

	public Double A( Double x, Double y ) {
		return round( x - y );
	}

	public Double B( Double x, Double y ) {
		return round( x + y );
	}

	private Double round( Double value ) {
		return (double) Math.round( value * 100 ) / 100;
	}

}
