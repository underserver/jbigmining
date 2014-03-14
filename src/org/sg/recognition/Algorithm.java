package org.sg.recognition;

/**
 * Created with IntelliJ IDEA.
 * Author: Antonio, Elias, Cero
 * Date: 28/05/13
 * Time: 07:46 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Algorithm {
    private String name;
    private DataSet trainSet;

    Algorithm( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

	public DataSet getTrainSet() {
		return trainSet;
	}

	public void setTrainSet( DataSet trainSet ) {
		this.trainSet = trainSet;
	}

	public abstract AlgorithmInformation getInformation();

	public abstract void train();

	@Override
	public boolean equals( Object o ) {
		if( this == o ) return true;
		if( o == null || getClass() != o.getClass() ) return false;

		Algorithm algorithm = (Algorithm) o;

		return !( name != null ? !name.equals( algorithm.name ) : algorithm.name != null );

	}

}
