/*
 * Copyright (c) %today.year Sergio Ceron Figueroa
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL COPYRIGHT HOLDERS OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.underserver.jbigmining.filters;

import org.underserver.jbigmining.Attribute;
import org.underserver.jbigmining.DataSet;
import org.underserver.jbigmining.Pattern;
import org.underserver.jbigmining.exceptions.FilterException;

import java.util.Arrays;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 28/05/13 07:06 PM
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
