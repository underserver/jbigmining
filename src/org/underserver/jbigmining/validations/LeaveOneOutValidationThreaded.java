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

package org.underserver.jbigmining.validations;

import org.underserver.jbigmining.core.Algorithm;
import org.underserver.jbigmining.core.DataSet;
import org.underserver.jbigmining.core.Pattern;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 28/05/13 02:30 PM
 */
public class LeaveOneOutValidationThreaded extends ValidationMethod {

	public LeaveOneOutValidationThreaded() {
		super("Leave One-Out Validation Threaded");
	}

	@Override
	public void validate() {
		Algorithm algorithm = getAlgorithm();

		DataSet dataSet = getDataSet();

		//int nrOfProcessors = Runtime.getRuntime().availableProcessors();
		//ExecutorService es = Executors.newFixedThreadPool( nrOfProcessors );
		ExecutorService es = Executors.newFixedThreadPool( dataSet.size() );

		long start = System.currentTimeMillis();

		for( Pattern instance : dataSet ) {
			List<Pattern> trainingSet = new DataSet( dataSet );

			trainingSet.addAll( getDataSet() );
			trainingSet.remove( instance );

			ValidationThread task = new ValidationThread( this );
			task.setAlgorithm( algorithm );
			task.setInstance( instance );
			task.setTrainSet( (DataSet) trainingSet );
			es.submit( task );

		}

		es.shutdown();
		try {
			es.awaitTermination( Long.MAX_VALUE, TimeUnit.NANOSECONDS );
		} catch ( InterruptedException e ) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();

		//System.out.println( "Time: " + ( end - start ) );

	}

}
