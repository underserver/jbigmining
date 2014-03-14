package org.sg.recognition.filters;

import org.sg.recognition.DataSet;
import org.sg.recognition.Pattern;
import org.sg.recognition.exceptions.FilterException;

/**
 * Project Name: PatternRecognition
 * Project Url: http://www.dotrow.com/projects/java/jcase
 * Author: Sergio Ceron
 * Version: 1.0
 * Date: 14/10/13 11:33 PM
 * Desc:
 */
public abstract class Filter {
	private String name;
	private DataSet dataSet;

	public Filter( String name ) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public DataSet getDataSet() {
		return dataSet;
	}

	public void setDataSet( DataSet dataSet ) {
		this.dataSet = dataSet;
	}

	public abstract void build();
	public abstract Pattern process( Pattern instance ) throws FilterException;
	public abstract DataSet getNewDataSet();

	public void processAll() {
		build();
		DataSet newDataSet = getNewDataSet();
		for( Pattern instance : dataSet ) {
			try {
				Pattern filtered = process( instance );
				newDataSet.add( filtered );
			} catch ( FilterException e ) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals( Object o ) {
		if( this == o ) return true;
		if( o == null || getClass() != o.getClass() ) return false;

		Filter filter = (Filter) o;

		return !( name != null ? !name.equals( filter.name ) : filter.name != null );

	}

}
