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
public class LDCE extends Classifier {
	private Map<Integer, Double> means;

	private double[] gains;
	private int k = 7;
	private ScalarFunction scalarFunction = new Polynomial();

	public LDCE( int k ) {
		super( "" );
		this.k = k;
		setName( "[" + scalarFunction.getName() + ";" + k + "]" );
		means = new HashMap<Integer, Double>();
	}

	public LDCE( int k, ScalarFunction scalarFunction ) {
		super( "" );
		this.k = k;
		this.scalarFunction = scalarFunction;
		setName( "[" + scalarFunction.getName() + ";" + k + "]" );
		means = new HashMap<Integer, Double>();
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
		/*for( Pattern pattern : getTrainSet() ) {
			System.out.println( pattern + ", f=" + f(pattern.toDoubleVector()) );
		}
		System.out.println("=======================================");*/
		int attributes = getTrainSet().getAttributes().size();
		gains = new double[attributes];
		Set<Pattern> partition = new HashSet<Pattern>( getTrainSet() );
		for( int i = 0; i < attributes; i++ ) {
			gains[i] = gain( partition, i ); //Math.pow(gain( partition, i ), 4)/10000;
		}

		means.clear();

		Map<Integer, Set<Pattern>> classes = new HashMap<Integer, Set<Pattern>>();
		for( Pattern instance : getTrainSet() ) {
			Set<Pattern> partitionx = classes.containsKey( instance.getClassIndex() ) ?
					classes.get( instance.getClassIndex() ) :
					new HashSet<Pattern>();
			partitionx.add( instance );
			classes.put( instance.getClassIndex(), partitionx );
		}

		for( Integer clazz : classes.keySet() ) {
			Set<Pattern> partitionx = classes.get( clazz );
			means.put( clazz, mean( partitionx, getTrainSet().getAttributes().size() ) );
		}
	}

	double distance(double a, double b){
		return Math.abs( a-b );
	}

	public Double mean( Set<Pattern> instances, int features ) {
		double mean = 0d;
		for( Pattern instance : instances ) {
			Double[] vector = instance.toDoubleVector();
			mean += getScalarFunction().f( vector, gains );
		}
		mean /= (double) instances.size();
		return mean;
	}

	@Override
	public int classify( Pattern instance ) {
		double distmin = Double.MAX_VALUE;
		int close = -1;
		for( Integer clazz : means.keySet() ) {
			Double a = means.get( clazz );
			Double b = getScalarFunction().f( instance.toDoubleVector(), gains );
			double dist = distance( a, b );
			if( dist < distmin ) {
				close = clazz;
				distmin = dist;
			}
		}

		return close;
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

		return s;
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
