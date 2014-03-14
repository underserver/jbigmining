package org.sg.recognition.validations;

import org.sg.recognition.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * Author: Antonio, Elias, Cero
 * Date: 28/05/13
 * Time: 02:29 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ValidationMethod {
	private DataSet dataSet;
	private Algorithm algorithm;
	private ConfusionMatrix confusionMatrix;

    protected ValidationMethod() { }

	public void build() {
		if( algorithm instanceof Recuperator ) {
			confusionMatrix = new ConfusionMatrix( 2 );
		} else if( algorithm instanceof Classifier ) {
			confusionMatrix = new ConfusionMatrix( dataSet.getDistribution().length );
		}
	}
    public abstract void validate();

	public DataSet getDataSet() {
		return dataSet;
	}

	public void setDataSet( DataSet dataSet ) {
		this.dataSet = dataSet;
	}

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
	    if( confusionMatrix != null )
	        confusionMatrix.init();
        this.algorithm = algorithm;
    }

	public ConfusionMatrix getConfusionMatrix() {
		return confusionMatrix;
	}

	protected void evaluate(int calculated, int correct) {
		confusionMatrix.add( correct, calculated, 1 );
	}

	protected void evaluate(Pattern recuperated, Pattern correct) {
		if( recuperated.equals( correct ) ) {
			confusionMatrix.add( 0, 0, 1 );
		} else {
			confusionMatrix.add( 1, 0, 1 );
		}
	}

}
