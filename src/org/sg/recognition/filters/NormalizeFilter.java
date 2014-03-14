package org.sg.recognition.filters;

import org.sg.recognition.DataSet;
import org.sg.recognition.Pattern;
import org.sg.recognition.exceptions.FilterException;

/**
 * Created with IntelliJ IDEA.
 * Author: Antonio, Elias, Cero
 * Date: 28/05/13
 * Time: 07:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class NormalizeFilter extends Filter {
	private DataSet newDataSet;
	double min = 0;

	public NormalizeFilter() {
		super("Normalizator Filter");
	}

	@Override
    public void build() {
		min = Double.MAX_VALUE;
        for (Pattern instance : getDataSet() ) {
            for (Double feature : instance.toDoubleVector()) {
                min = feature < min ? feature : min;
            }
        }
		newDataSet = new DataSet( getDataSet() );
    }


	@Override
	public Pattern process( Pattern instance ) throws FilterException {
		Pattern filtered = new Pattern();
		filtered.setDataSet( getDataSet() );
		filtered.setClassIndex( instance.getClassIndex() );

		Double[] features = instance.toDoubleVector();
		for( int i = 0; i < features.length; i++ ) {
			Long feature = features[i].longValue();
			filtered.add( (double) Math.round( (( -min + feature.doubleValue() )+1) * 100 ) / 100 );
		}
		return filtered;
	}

	@Override
	public DataSet getNewDataSet() {
		return newDataSet;
	}

}
