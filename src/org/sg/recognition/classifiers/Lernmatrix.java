package org.sg.recognition.classifiers;

import org.sg.recognition.AlgorithmInformation;
import org.sg.recognition.Classifier;
import org.sg.recognition.DataSet;
import org.sg.recognition.Pattern;
import org.sg.recognition.utils.Matrix;

import java.util.*;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 28/05/13 07:45 PM
 */public class Lernmatrix extends Classifier {
	private static final int E = 1;

    private Double[][] M;

    public Lernmatrix() {
        super("Lernmatrix");
    }

	@Override
	public AlgorithmInformation getInformation() {
		AlgorithmInformation result;

		result = new AlgorithmInformation();

		return result;
	}

	@Override
    public void train() {
        DataSet trainSet = getTrainSet();

		int n = trainSet.getAttributes().size();
		int m = trainSet.getClasses().getValues().size();

        // Entrenamiento
        M = Matrix.fill(m, n, 0d);
        for ( Pattern instance : trainSet ) {
	        for( int i = 0; i < instance.size(); i++ ) {
		        M[instance.getClassIndex()][i] += instance.toDoubleVector()[i] == 1d ? +E : -E;
	        }
        }

    }

	@Override
	public int classify( Pattern instance ) {
		Double[][] xM  = Matrix.trans( new Double[][]{ instance.toDoubleVector() } );

		Double[][] yM = Matrix.mult( M, xM );

		Double[][] yMCopy = new Double[yM.length][1];
		for( int ix = 0; ix<yM.length; ix++ )
			System.arraycopy( yM[ix], 0, yMCopy[ix], 0, yM[ix].length );

		double max = -Double.MAX_VALUE;
		for( int i = 0; i < yM.length; i++ ) {
			if( yM[i][0] > max ){
				max = yM[i][0];
			}
		}

		int result = -1, count = 0;
		for( int j = 0; j < yM.length; j++ ) {
			if( yM[j][0] == max ){
				yM[j][0] = 1d;
				result = j;
				count ++;
			} else {
				yM[j][0] = 0d;
			}
		}

		//if( count > 1 ) result = -1;

		return result;
	}


}
