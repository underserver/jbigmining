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

import org.underserver.jbigmining.core.DataSet;
import org.underserver.jbigmining.core.Pattern;
import org.underserver.jbigmining.exceptions.FilterException;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 28/05/13 07:06 PM
 */
public class NormalizeFilter extends Filter {
	private DataSet newDataSet;
	double min = 0;

	public NormalizeFilter() {
		super( "Normalizator Filter" );
	}

	@Override
	public void build() {
		min = Double.MAX_VALUE;
		for( Pattern instance : getDataSet() ) {
			for( Double feature : instance.toDoubleVector() ) {
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
			filtered.add( (double) Math.round( ( ( -min + feature.doubleValue() ) + 1 ) * 100 ) / 100 );
		}
		return filtered;
	}

	@Override
	public DataSet getNewDataSet() {
		return newDataSet;
	}

}
