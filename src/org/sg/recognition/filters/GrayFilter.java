package org.sg.recognition.filters;

import org.sg.recognition.*;
import org.sg.recognition.exceptions.FilterException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: Sergio Ceron
 * Date: 28/05/13
 * Time: 07:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class GrayFilter extends Filter {
	private DataSet newDataSet;
    private int[] bits;

    public GrayFilter() {
	    super("Gray-Code Binary Filter");
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
			    int maxBits = (int) Math.ceil( Math.log( feature ) / Math.log( 2 ) );
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
		    Long feature = features[i].longValue();
            String binary = String.format("%" + bits[i] + "s",
		            Long.toBinaryString((feature >> 1) ^ feature)).
		            replace(' ', '0');
		    for( Character c : binary.toCharArray() ) {
			    filtered.add( Double.parseDouble( c + "" ) );
		    }
	    }
	    return filtered;
    }

	@Override
	public DataSet getNewDataSet() {
		return newDataSet;
	}

}
