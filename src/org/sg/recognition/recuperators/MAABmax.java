package org.sg.recognition.recuperators;

import org.sg.recognition.AlgorithmInformation;
import org.sg.recognition.DataSet;
import org.sg.recognition.Pattern;
import org.sg.recognition.Recuperator;
import org.sg.recognition.utils.Matrix;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 28/05/13 07:45 PM
 */public class MAABmax extends Recuperator {

	private Double[][] M;

	public MAABmax() {
		super( "Memoria Alfa Beta Autoasociativa MAX" );
	}

	@Override
	public AlgorithmInformation getInformation() {
		AlgorithmInformation result;

		result = new AlgorithmInformation();
		result.setField( AlgorithmInformation.Field.AUTHOR, "Ritter,Sussner & Diaz-de-Leon" );
		result.setField( AlgorithmInformation.Field.YEAR, "1998" );
		result.setField( AlgorithmInformation.Field.TITLE, "Memorias Asociativas Basadas en Relaciones de Orden y Operaciones Binarias" );
		result.setField( AlgorithmInformation.Field.JOURNAL, "Computación y Sistemas" );
		result.setField( AlgorithmInformation.Field.VOLUME, "6" );
		result.setField( AlgorithmInformation.Field.PAGES, "300-311" );
		result.setField( AlgorithmInformation.Field.TYPE, AlgorithmInformation.Type.RECUPERATOR );
		result.setField( AlgorithmInformation.Field.CATEGORY, "Associative" );

		return result;
	}

	@Override
	public void train() {
		DataSet trainSet = getTrainSet();

		int n = trainSet.getAttributes().size();
		int m = trainSet.getAttributes().size();

		// Entrenamiento
		M = Matrix.fill( m, n, -Double.MAX_VALUE );
		for( Pattern instance : trainSet ) {
			Double[] xM = instance.toDoubleVector();
			Double[] yM = instance.toDoubleVector();
			Double[][] zM = Y( xM, yM );
			M = Matrix.max( zM, M );
		}
	}

	@Override
	public Pattern recover( Pattern pattern ) {
		Double[] xM = pattern.toDoubleVector();

		return new Pattern( Z( M, xM ) );
	}

	public Double[][] Y( Double[] xM, Double[] yM ) {
		Double[][] zM = Matrix.fill( yM.length, xM.length, 0d );
		for( int i = 0; i < yM.length; i++ ) {
			for( int j = 0; j < xM.length; j++ ) {
				zM[i][j] = (double) Alfa( yM[i].intValue(), xM[j].intValue() );
			}
		}
		return zM;
	}

	public Double[] Z( Double[][] M, Double[] xM ) {
		int m1rows = M.length;
		int m1cols = M[0].length;

		Double[] result = new Double[m1rows];

		for( int i = 0; i < m1rows; i++ ) {
			double _min = 2;
			for( int k = 0; k < m1cols; k++ ) {
				double b = (double) Beta( M[i][k].intValue(), xM[k].intValue() );
				_min = b < _min ? b : _min;
			}
			result[i] = _min;
		}

		return result;
	}

	public Integer Alfa( Integer x, Integer y ) {
		int z = 0;
		if( x == 0 && y == 0 ) {
			z = 1;
		} else if( x == 0 && y == 1 ) {
			z = 0;
		} else if( x == 1 && y == 0 ) {
			z = 2;
		} else if( x == 1 && y == 1 ) {
			z = 1;
		}
		return z;
	}

	public Integer Beta( Integer x, Integer y ) {
		int z = 0;
		if( x == 0 && y == 0 ) {
			z = 0;
		} else if( x == 0 && y == 1 ) {
			z = 0;
		} else if( x == 1 && y == 0 ) {
			z = 0;
		} else if( x == 1 && y == 1 ) {
			z = 1;
		} else if( x == 2 && y == 0 ) {
			z = 1;
		} else if( x == 2 && y == 1 ) {
			z = 1;
		}
		return z;
	}


}
