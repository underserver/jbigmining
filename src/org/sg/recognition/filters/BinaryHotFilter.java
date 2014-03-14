package org.sg.recognition.filters;

import org.sg.recognition.Attribute;
import org.sg.recognition.DataSet;
import org.sg.recognition.Pattern;
import org.sg.recognition.exceptions.FilterException;
import org.sg.recognition.utils.Matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: Sergio Ceron
 * Date: 28/05/13
 * Time: 07:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class BinaryHotFilter extends Filter {
	private DataSet newDataSet;
	private int[] bits;

	public BinaryHotFilter() {
		super( "Binary One-Hot Filter" );
	}

	@Override
	public void build() {
		DataSet oldDataSet = getDataSet();

		bits = new int[oldDataSet.getAttributes().size()];
		Arrays.fill( bits, 1 );

		for( Pattern instance : oldDataSet ) {
			Double[] features = instance.toDoubleVector();
			for( int i = 0; i < features.length; i++ ) {
				Double feature = features[i];
				int maxBits = feature.intValue();
				bits[i] = Math.max( bits[i], maxBits );
			}
		}

		int totalBits = 0;
		for( int bit : bits ) {
			totalBits += bit;
		}

		newDataSet = new DataSet();
		newDataSet.setName( oldDataSet.getName() );
		newDataSet.setDistribution( oldDataSet.getDistribution() );
		newDataSet.setClasses( oldDataSet.getClasses() );

		for( int i = 0; i < totalBits; i++ ) {
			newDataSet.addAttribute( new Attribute( "bit" + i ) );
		}
	}

	@Override
	public Pattern process( Pattern instance ) throws FilterException {
		Pattern filtered = new Pattern();
		filtered.setDataSet( newDataSet );
		filtered.setClassIndex( instance.getClassIndex() );

		Double[] features = instance.toDoubleVector();
		for( int i = 0; i < features.length; i++ ) {
			int feature = features[i].intValue();
			for( int j = 0; j < bits[i]; j++ ) {
				filtered.add( j == feature ? 1d : 0d );
			}
		}
		return filtered;
	}

	@Override
	public DataSet getNewDataSet() {
		return newDataSet;
	}

}
