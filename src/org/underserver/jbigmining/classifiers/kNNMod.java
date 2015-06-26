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

import org.underserver.jbigmining.core.AlgorithmInformation;
import org.underserver.jbigmining.core.Classifier;
import org.underserver.jbigmining.core.MyHeap;
import org.underserver.jbigmining.core.Pattern;
import org.underserver.jbigmining.distances.Distance;
import org.underserver.jbigmining.distances.EuclideanDistance;
import org.underserver.jbigmining.utils.Matrix;

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
public class kNNMod extends Classifier {

	private Distance distance;
	private int k = 3;
	private double[] gains;

	public kNNMod() {
		super( "kNN" );
		setDistance( new EuclideanDistance() );
	}

	public kNNMod( int k ) {
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
		int attributes = getTrainSet().getAttributes().size();
		gains = new double[attributes];
		Set<Pattern> partition = new HashSet<Pattern>( getTrainSet() );
		for( int i = 0; i < attributes; i++ ) {
			gains[i] = gain( partition, i ); //Math.pow(gain( partition, i ), 4)/10000;
			System.out.println( "Gain f="+i+", g=" + gain( partition, i ) );
		}
		for( int i = 0; i < attributes; i++ ) {
			System.out.println( "Gain2 f="+i+", g=" + gain2( partition, i ) );
		}
	}

	@Override
	public int classify( Pattern instance ) {
		Neighbor[] nearest = nearestNeighbors( getTrainSet(), instance, getK() );
		Pattern[] nearest2 = new Pattern[0];
		try {
			nearest2 = nearest( getTrainSet(), instance, getK() );
		} catch( Exception e ) {
			e.printStackTrace();
		}

		double[] votes = new double[getTrainSet().getDistribution().length];
		Arrays.fill( votes, 0 );

		/*for( Pattern near : nearest2 ) {
			if( near != null )
				votes[near.getClassIndex()] += 1;///near.getDistance();
		}*/
		for( Neighbor near : nearest ) {
			if( near != null )
				votes[near.getPattern().getClassIndex()] += 1;///near.getDistance();
		}

		double maxVotes = 0;
		int classCalculated = -1;
		for( int clazz = 0; clazz < votes.length; clazz++ ) {
			double vote = votes[clazz];
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

	public Neighbor[] nearestNeighbors( List<Pattern> instances, Pattern other, int kNearest ) {
		List<Neighbor> neighbors = new ArrayList<Neighbor>();
		for( Pattern pattern : instances ) {
			//double dist = getDistance().distance( pattern.toDoubleVector(), other.toDoubleVector() );
			double distance = 0d;
			double distances[] = new double[pattern.size()];
			for( int i = 0; i < other.size(); i++ ) {
				double dist = Double.MAX_VALUE;
				if( pattern.get( i ) == null || other.get( i ) == null ) {
					if( pattern.get( i ) == null && other.get( i ) == null ) {
						dist = 0;
					}
				} else {
					dist = dist( pattern.getDouble( i ), other.getDouble( i ) ) / (gains[i]);
				}
				distance += dist;
				distances[i] = dist;
			}
			neighbors.add( new Neighbor( distance, pattern ) );
		}

		Collections.sort( neighbors );

		int index = 0;
		Neighbor[] nearest = new Neighbor[kNearest];
		for( Neighbor neighbor : neighbors ) {
			if( index >= kNearest ) break;
			nearest[index++] = neighbor;
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
				distance = distance( other, instances.get( i ) );
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
				distance = distance( other, instances.get( i ) );
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

	private double distance(Pattern pattern, Pattern other){
		double distance = 0d;
		for( int i = 0; i < other.size(); i++ ) {
			double dist = Double.MAX_VALUE;
			if( pattern.get( i ) == null || other.get( i ) == null ) {
				if( pattern.get( i ) == null && other.get( i ) == null ) {
					dist = 0;
				}
			} else {
				dist = dist( pattern.getDouble( i ), other.getDouble( i ) ) / (1+Math.pow(gains[i], 1));
			}
			distance += dist;
		}
		return distance;
	}

	private double dist(double a, double b){
		return Math.abs( a - b );
	}

	private double gain(Set<Pattern> partition, int feature){
		double s = entropy( partition );

		Map<Double, Set<Pattern>> subpartitions = new HashMap<Double, Set<Pattern>>();
		for( Pattern pattern : partition ) {
			double key = -1;
			if( pattern.get( feature ) != null )
				key = pattern.getDouble( feature );
			if( subpartitions.containsKey( key ) )
				subpartitions.get( key ).add( pattern );
			else {
				Set<Pattern> subpartition = new HashSet<Pattern>();
				subpartition.add( pattern );
				subpartitions.put( key, subpartition);
			}
		}

		for( Set<Pattern> subpartition : subpartitions.values() ) {
			s += -((subpartition.size() / (double)partition.size()) * entropy( subpartition ));
		}

		return s;//(1/Math.pow( s, 2 ))*100;
	}

	private double gain2(Set<Pattern> partition, int feature){
		double s = entropy2( partition, feature );

		Map<Double, Set<Pattern>> subpartitions = new HashMap<Double, Set<Pattern>>();
		for( Pattern pattern : partition ) {
			double key = pattern.getClassIndex();
			if( subpartitions.containsKey( key ) )
				subpartitions.get( key ).add( pattern );
			else {
				Set<Pattern> subpartition = new HashSet<Pattern>();
				subpartition.add( pattern );
				subpartitions.put( key, subpartition);
			}
		}

		for( Set<Pattern> subpartition : subpartitions.values() ) {
			s += -((subpartition.size() / (double)partition.size()) * entropy2( subpartition, feature ));
		}

		return s;//(1/Math.pow( s, 2 ))*100;
	}

	private double entropy( Set<Pattern> partition ){
		Map<Integer, Integer> distribution = new HashMap<Integer, Integer>();
		for( Pattern pattern : partition ){
			if( distribution.containsKey( pattern.getClassIndex() ) )
				distribution.put( pattern.getClassIndex(), distribution.get( pattern.getClassIndex() ) + 1 );
			else
				distribution.put( pattern.getClassIndex(), 1 );
		}
		double total = partition.size();
		return entropy( distribution, total );
	}

	private double entropy2( Set<Pattern> partition, int feature ){
		Map<Double, Integer> distribution = new HashMap<Double, Integer>();
		for( Pattern pattern : partition ){
			if( pattern.get( feature ) != null ) {
				double key = pattern.getDouble( feature );
				if( distribution.containsKey( key ) )
					distribution.put( key, distribution.get( key ) + 1 );
				else
					distribution.put( key, 1 );
			}
		}
		double total = partition.size();
		return entropy2( distribution, total );
	}

	private double entropy(Map<Integer, Integer> distribution, double total){
		double s = 0d;
		for( Integer classIndex : distribution.keySet() ) {
			double div = (distribution.get( classIndex ) / total);
			s += -( div * log2( div ));
		}
		return s;
	}

	private double entropy2(Map<Double, Integer> distribution, double total){
		double s = 0d;
		for( Integer value : distribution.values() ) {
			double div = (value / total);
			s += -( div * log2( div ));
		}
		return s;
	}

	private double log2(double x){
		return Math.log( x ) / Math.log( 2 );
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

	public int getK() {
		return k;
	}

	public void setK( int k ) {
		this.k = k;
	}
}
