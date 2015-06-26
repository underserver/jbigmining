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
public class LDCLimits extends Classifier {

	private double[] gains;
	private ScalarFunction scalarFunction = new Polynomial();
	private Map<Integer, Double> maxs;
	private Map<Integer, Double> mins;

	public LDCLimits( int k ) {
		super( "" );
		setName( "[" + scalarFunction.getName() + ";" + k + "]" );
	}

	public LDCLimits( int k, ScalarFunction scalarFunction ) {
		super( "" );
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

		Map<Integer, Set<Pattern>> classes = new HashMap<Integer, Set<Pattern>>();
		for( Pattern instance : getTrainSet() ) {
			Set<Pattern> partitionx = classes.containsKey( instance.getClassIndex() ) ?
					classes.get( instance.getClassIndex() ) :
					new HashSet<Pattern>();
			partitionx.add( instance );
			classes.put( instance.getClassIndex(), partitionx );
		}

		maxs = new HashMap<Integer, Double>();
		for( Integer clazz : classes.keySet() ) {
			Set<Pattern> partitionx = classes.get( clazz );
			maxs.put( clazz, maximum( partitionx ) );
		}

		mins = new HashMap<Integer, Double>();
		for( Integer clazz : classes.keySet() ) {
			Set<Pattern> partitionx = classes.get( clazz );
			mins.put( clazz, minimum( partitionx ) );
		}

	}

	private Double maximum( Set<Pattern> partitionx ) {
		double max = -Double.MAX_VALUE;
		for( Pattern pattern : partitionx ) {
			double f = getScalarFunction().f(pattern.toDoubleVector(), gains);
			if( f > max ){
				max = f;
			}
		}
		return max;
	}

	private Double minimum( Set<Pattern> partitionx ) {
		double min = Double.MAX_VALUE;
		for( Pattern pattern : partitionx ) {
			double f = getScalarFunction().f(pattern.toDoubleVector(), gains);
			if( f < min ){
				min = f;
			}
		}
		return min;
	}

	double distance(double a, double b){
		return Math.abs( a-b );
	}

	@Override
	public int classify( Pattern instance ) {
		int classesCount = getTrainSet().getDistribution().length;

		double distances[][] = new double[classesCount][2];
		int[] classes = new int[classesCount];
		int classCalculated = -1;
		boolean unique = true;
		Arrays.fill( classes, 0 );
		int indexDuplicated = 0;
		for( Integer clazz : maxs.keySet() ) {
			double f = getScalarFunction().f( instance.toDoubleVector(), gains );
			double disToMax = distance( f, maxs.get( clazz ) );
			double disToMin = distance( f, mins.get( clazz ) );
			distances[clazz][0] = disToMin;
			distances[clazz][1] = disToMax;

			if( f <= maxs.get( clazz ) && f >= mins.get( clazz ) ) {
				classes[indexDuplicated++] = clazz;
				if( classCalculated != -1 ){
					unique = false;
				}
				classCalculated = clazz;
			}
		}

		if( !unique ){
			classCalculated = classByCoverage( classes, distances );
		}

		if( classCalculated == -1 ){
			classCalculated = classByNearest( classes, distances );
		}

		return classCalculated;
	}

	private int classByCoverage(int[] classes, double distances[][]){
		int classA = classes[0];
		int classB = classes[1];
		int classMin = 0;
		int classMax = 0;
		if( mins.get(classA) < mins.get(classB) ) {
			classMin = classA;
			classMax = classB;
		} else {
			classMin = classB;
			classMax = classA;
		}
		if( distances[classMax][0] > distances[classMin][1]){
			return classMax;
		}
		return classMin;
	}

	private int classByNearest(int[] classes, double distances[][]){
		double min = Double.MAX_VALUE;
		int clazz = -1;
		for( int i = 0; i < classes.length; i++ ) {
			for( int j = 0; j < 2; j++ ) {
				if( distances[i][j] < min ){
					min = distances[i][j];
					clazz = i;
				}
			}
		}
		return clazz;
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

	public ScalarFunction getScalarFunction() {
		return scalarFunction;
	}

	public void setScalarFunction( ScalarFunction scalarFunction ) {
		this.scalarFunction = scalarFunction;
	}
}
