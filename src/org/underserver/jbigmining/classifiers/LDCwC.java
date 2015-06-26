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
import org.underserver.jbigmining.core.Pattern;
import org.underserver.jbigmining.functions.Polynomial;
import org.underserver.jbigmining.functions.ScalarFunction;

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
public class LDCwC extends Classifier {

	private double[] gains;
	private int k = 7;
	private ScalarFunction scalarFunction = new Polynomial();

	public LDCwC( int k ) {
		super( "" );
		this.k = k;
		setName( "[" + scalarFunction.getName() + ";" + k + "]" );
	}

	public LDCwC( int k, ScalarFunction scalarFunction ) {
		super( "" );
		this.k = k;
		this.scalarFunction = scalarFunction;
		setName( "[" + scalarFunction.getName() + ";" + k + "]" );
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
			gains[i] = gain( partition, i );
		}
	}

	public Neighbor[] nearestNeighbors( List<Pattern> instances, Pattern other, int kNearest ) {
		List<Neighbor> neighbors = new ArrayList<Neighbor>();
		for( Pattern pattern : instances ) {
			double dist = distance( scalarFunction.f( appendClass(pattern.toDoubleVector(), pattern.getClassIndex()), appendClass( gains, .9) ), scalarFunction.f( other.toDoubleVector(), gains ) );
			neighbors.add( new Neighbor( dist, pattern ) );
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

	Double[] appendClass(Double[] vector, int clazz){
		Double[] result = new Double[vector.length + 1];
		for( int i = 0; i < vector.length; i++ ) {
			result[i] = vector[i];
		}
		result[vector.length] = (double) clazz;
		return result;
	}

	double[] appendClass(double[] vector, double value){
		double[] result = new double[vector.length + 1];
		for( int i = 0; i < vector.length; i++ ) {
			result[i] = vector[i];
		}
		result[vector.length] = value;
		return result;
	}

	double distance(double a, double b){
		return Math.abs( a-b );
	}

	@Override
	public int classify( Pattern instance ) {
		Neighbor[] nearest = nearestNeighbors( getTrainSet(), instance, getK() );

		double[] votes = new double[getTrainSet().getDistribution().length];
		Arrays.fill( votes, 0 );

		for( Neighbor near : nearest ) {
			votes[near.getPattern().getClassIndex()] += 1;
		}

		double maxVote = 0;
		int classCalculated = -1;
		for( int clazz = 0; clazz < votes.length; clazz++ ) {
			double vote = votes[clazz];
			if( vote > maxVote ) {
				maxVote = vote;
				classCalculated = clazz;
			}
		}
		return classCalculated;
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

	private double entropy(Map<Integer, Integer> distribution, double total){
		double s = 0d;
		for( Integer classIndex : distribution.keySet() ) {
			double div = (distribution.get( classIndex ) / total);
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

	public ScalarFunction getScalarFunction() {
		return scalarFunction;
	}

	public void setScalarFunction( ScalarFunction scalarFunction ) {
		this.scalarFunction = scalarFunction;
	}
}
