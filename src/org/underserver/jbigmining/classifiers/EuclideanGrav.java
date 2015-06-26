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
import org.underserver.jbigmining.distances.Distance;
import org.underserver.jbigmining.distances.EuclideanDistance;

import java.util.*;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 15/09/13 01:22 PM
 */
public class EuclideanGrav extends Classifier {

	private Map<Integer, Double[]> distMeans;
	private Map<Integer, Double> gravMass;
	private Distance distance;

	public EuclideanGrav() {
		super( "Euclidean Grav" );
		setDistance( new EuclideanDistance() );
		distMeans = new HashMap<Integer, Double[]>();
		gravMass = new HashMap<Integer, Double>();
	}

	@Override
	public AlgorithmInformation getInformation() {
		AlgorithmInformation result;

		result = new AlgorithmInformation();
		result.setField( AlgorithmInformation.Field.AUTHOR, "Unknown" );
		result.setField( AlgorithmInformation.Field.YEAR, "1991" );
		result.setField( AlgorithmInformation.Field.TITLE, "Distance-based euclidean algorithms" );
		result.setField( AlgorithmInformation.Field.TYPE, AlgorithmInformation.Type.CLASSIFIER );
		result.setField( AlgorithmInformation.Field.CATEGORY, "Metrics" );

		return result;
	}

	@Override
	public void train() {
		distMeans.clear();
		gravMass.clear();

		Map<Integer, Set<Pattern>> classes = new HashMap<Integer, Set<Pattern>>();
		for( Pattern instance : getTrainSet() ) {
			Set<Pattern> partition = classes.containsKey( instance.getClassIndex() ) ?
					classes.get( instance.getClassIndex() ) :
					new HashSet<Pattern>();
			partition.add( instance );
			classes.put( instance.getClassIndex(), partition );
		}

		for( Integer clazz : classes.keySet() ) {
			Set<Pattern> partition = classes.get( clazz );
			distMeans.put( clazz, distMean( partition, getTrainSet().getAttributes().size() ) );
			gravMass.put( clazz, classMass( partition, getTrainSet().getAttributes().size() ) );
		}
	}

	public Distance getDistance() {
		return distance;
	}

	public void setDistance( Distance distance ) {
		this.distance = distance;
	}

	@Override
	public int classify( Pattern instance ) {
		double maxAttractor = -Double.MAX_VALUE;
		int clase = -1;
		for( Integer clazz : distMeans.keySet() ) {
			Double[] a = distMeans.get( clazz );
			Double[] b = instance.toDoubleVector();
			Double classMass = gravMass.get( clazz );
			double dist = getDistance().distance( a, b );
			double force = F( classMass, a );
			double attraction = force / (dist*dist);
			if( attraction > maxAttractor ) {
				clase = clazz;
				maxAttractor = attraction;
			}
		}

		return clase;
	}

	private double F(Double mc, Double[] mx){
		return mc * mass( mx );
	}

	public Double[] distMean( Set<Pattern> instances, int features ) {
		Double[] mean = new Double[features];
		Arrays.fill( mean, 0d );
		for( Pattern instance : instances ) {
			Double[] vector = instance.toDoubleVector();
			for( int i = 0; i < instance.size(); i++ ) {
				mean[i] += vector[i] / (double) instances.size();
			}
		}
		return mean;
	}

	public double classMass( Set<Pattern> instances, int features ) {
		double mass = 0d;
		for( Pattern instance : instances ) {
			Double[] vector = instance.toDoubleVector();
			mass = Math.max(mass, mass( vector ));
		}
		return mass;
	}

	private double mass( Double[] vector ){
		double val = 0d;
		//Double max = Collections.max( Arrays.asList( vector ) );
		//Double min = Collections.min( Arrays.asList( vector ) );
		int i = 1;
		for( Double v : vector ) {
			val += Math.atan2(v, i++);
		}
		return  val ;

		//return max*min;
	}

}
