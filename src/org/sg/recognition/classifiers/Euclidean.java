package org.sg.recognition.classifiers;

import org.sg.recognition.AlgorithmInformation;
import org.sg.recognition.Classifier;
import org.sg.recognition.Pattern;
import org.sg.recognition.distances.Distance;
import org.sg.recognition.distances.EuclideanDistance;

import java.util.*;

/**
 * Project Name: PatternRecognition
 * Project Url: http://www.dotrow.com/projects/java/jcase
 * Author: Sergio Ceron
 * Version: 1.0
 * Date: 15/09/13 01:22 PM
 * Desc:
 */
public class Euclidean extends Classifier {

	private Map<Integer, Double[]> means;
	private Distance distance;

	public Euclidean() {
		super( "Euclidean" );
		setDistance( new EuclideanDistance() );
		means = new HashMap<Integer, Double[]>();
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
		means.clear();

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
			means.put( clazz, mean( partition, getTrainSet().getAttributes().size() ) );
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
		double distmin = Double.MAX_VALUE;
		int close = -1;
		for( Integer clazz : means.keySet() ) {
			Double[] a = means.get( clazz );
			Double[] b = instance.toDoubleVector();
			double dist = getDistance().distance( a, b );
			if( dist < distmin ) {
				close = clazz;
				distmin = dist;
			}
		}

		return close;
	}

	public Double[] mean( Set<Pattern> instances, int features ) {
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

}
