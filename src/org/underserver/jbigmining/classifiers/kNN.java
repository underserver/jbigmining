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

package org.underserver.jbigmining.classifiers;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import org.underserver.jbigmining.core.AlgorithmInformation;
import org.underserver.jbigmining.core.Classifier;
import org.underserver.jbigmining.core.MyHeap;
import org.underserver.jbigmining.core.Pattern;
import org.underserver.jbigmining.distances.Distance;
import org.underserver.jbigmining.distances.EuclideanDistance;

import java.util.*;

import static org.underserver.jbigmining.core.AlgorithmInformation.Field.*;
import static org.underserver.jbigmining.core.AlgorithmInformation.Type.CLASSIFIER;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 15/09/13 01:22 PM
 */
public class kNN extends Classifier {

	private Distance distance;
	private int k = 3;

	public kNN() {
		super( "kNN" );
		setDistance( new EuclideanDistance() );
	}

	public kNN( int k ) {
		this();
		this.k = k;
	}

	@Override
	public AlgorithmInformation getInformation() {
		AlgorithmInformation result;

		result = new AlgorithmInformation();
		result.setField( AUTHOR, "D. Aha and D. Kibler" );
		result.setField( YEAR, "1991" );
		result.setField( TITLE, "Instance-based learning algorithms" );
		result.setField( JOURNAL, "Machine Learning" );
		result.setField( VOLUME, "6" );
		result.setField( PAGES, "37-66" );
		result.setField( TYPE, CLASSIFIER );
		result.setField( CATEGORY, "Metrics" );

		return result;
	}

	@Override
	public void train() {
	}

	@Override
	public int classify( Pattern instance ) {
		Pattern[] nearest2 = nearestNeighbors( getTrainSet(), instance, getK() );
		Pattern[] nearest = null;
		try {
			nearest = nearest( getTrainSet(), instance, getK() );
		} catch( Exception e ) {
			e.printStackTrace();
		}

		int[] votes = new int[getTrainSet().getDistribution().length];
		Arrays.fill( votes, 0 );

		for( Pattern near : nearest ) {
			votes[near.getClassIndex()] += 1;
		}

		int maxVotes = 0;
		int classCalculated = -1;
		for( int clazz = 0; clazz < votes.length; clazz++ ) {
			int vote = votes[clazz];
			if( vote > maxVotes ) {
				maxVotes = vote;
				classCalculated = clazz;
			}
		}
		return classCalculated;
	}

	public Distance getDistance() {
		return distance;
	}

	public void setDistance( Distance distance ) {
		this.distance = distance;
	}

	public Pattern[] nearestNeighbors( List<Pattern> instances, Pattern other, int kNearest ) {
		List<Neighbor> neighbors = new ArrayList<Neighbor>();
		for( Pattern pattern : instances ) {
			double dist = distance( pattern.toDoubleVector(), other.toDoubleVector(), Double.POSITIVE_INFINITY );
			neighbors.add( new Neighbor( dist, pattern ) );
		}

		Collections.sort( neighbors );
		int index = 0;
		Pattern[] nearest = new Pattern[kNearest];
		for( Neighbor neighbor : neighbors ) {
			if( index >= kNearest ) break;
			nearest[index++] = neighbor.getPattern();
		}
		return nearest;
	}

	private Pattern[] nearest( List<Pattern> instances, Pattern other, int kNearest ) throws Exception {
		MyHeap heap = new MyHeap(kNearest);
		double distance; int firstkNN=0;
		for(int i=0; i<instances.size(); i++) {
			if( other.equals( instances.get( i ) ) ) //for hold-one-out cross-validation
				continue;
			if(firstkNN<kNearest) {
				distance = distance( other.toDoubleVector(), instances.get( i ).toDoubleVector(), Double.POSITIVE_INFINITY );
				if(distance == 0.0 )
					if(i<instances.size()-1)
						continue;
					else
						heap.put(i, distance);
				heap.put(i, distance);
				firstkNN++;
			}
			else {
				MyHeap.MyHeapElement temp = heap.peek();
				distance = distance( other.toDoubleVector(), instances.get( i ).toDoubleVector(), temp.distance );
				if(distance == 0.0 )
					continue;
				if(distance < temp.distance) {
					heap.putBySubstitute(i, distance);
				}
				else if(distance == temp.distance) {
					heap.putKthNearest(i, distance);
				}

			}
		}

		Pattern[] neighbours = new Pattern[heap.size()+heap.noOfKthNearest()];
		double[] m_Distances = new double[heap.size()+heap.noOfKthNearest()];
		int [] indices = new int[heap.size()+heap.noOfKthNearest()];
		int i=1; MyHeap.MyHeapElement h;
		while(heap.noOfKthNearest()>0) {
			h = heap.getKthNearest();
			indices[indices.length-i] = h.index;
			m_Distances[indices.length-i] = h.distance;
			i++;
		}
		while(heap.size()>0) {
			h = heap.get();
			indices[indices.length-i] = h.index;
			m_Distances[indices.length-i] = h.distance;
			i++;
		}

		//getDistance().postProcessDistances(m_Distances);

		for(int k=0; k<indices.length; k++) {
			neighbours[k] = instances.get( indices[k] );
		}

		return neighbours;

	}

	public int getK() {
		return k;
	}

	public void setK( int k ) {
		this.k = k;
	}

	public double distance( Double[] a, Double[] b, double cutOffValue ) {
		double sum = 0.0;
		for( int i = 0; i < a.length; i++ ) {
			sum = sum + Math.pow( ( a[i] - b[i] ), 2.0 );
		}
		double distance = Math.sqrt( sum );
		if (distance > cutOffValue) {
			return Double.POSITIVE_INFINITY;
		}
		return Math.sqrt( sum );
	}

	class Neighbor implements Comparable<Neighbor> {
		private double distance;
		private Pattern pattern;

		public Neighbor( double distance, Pattern pattern ) {
			this.distance = distance;
			this.pattern = pattern;
		}

		public double getDistance() {
			return distance;
		}

		public Pattern getPattern() {
			return pattern;
		}

		@Override
		public int compareTo( Neighbor o ) {
			if( o.getDistance() == this.getDistance() ) return 0;
			return o.getDistance() > this.getDistance() ? -1 : 1;
		}
	}

}
