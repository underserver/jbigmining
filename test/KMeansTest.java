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

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.underserver.jbigmining.core.Clusterer;
import org.underserver.jbigmining.core.DataSet;
import org.underserver.jbigmining.core.Parser;
import org.underserver.jbigmining.core.Pattern;
import org.underserver.jbigmining.clusterers.KMeans;
import org.underserver.jbigmining.parsers.ARFFParser;

import java.util.Set;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 8/12/13 11:47 PM
 */
public class KMeansTest extends TestCase {
	private static final String FILE = "./banks/iris.arff";

	private DataSet dataSet = null;

	@Before
	public void setUp() throws Exception {
		Parser parser = new ARFFParser( FILE );
		dataSet = parser.parse();
	}

	@Test
	public void testClusterer() throws Exception {
		Clusterer kMeans = new KMeans( 3 );
		kMeans.setTrainSet(dataSet);

		long time1 = System.currentTimeMillis();
		kMeans.train();
		long time2 = System.currentTimeMillis();

		Set[] clusters = kMeans.getClusters();

		System.out.println("Tiempo:" + (time2-time1)/1000d);
		System.out.println("Iteraciones:" + ((KMeans)(kMeans)).getIterations());

		for( int c = 0; c < clusters.length; c++ ) {
			Set<Pattern> cluster = clusters[c];
			for( Pattern instance : cluster ) {
				System.out.printf("%s | %d\n", instance, c);
			}
		}

	}

}
