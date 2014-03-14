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
public class EuclideanMod extends Classifier {

	private Map<Integer, Double[]> dists;
	private Map<Integer, Double[]> means;

	private Distance distance;

	public EuclideanMod() {
		super("EuclideanMod");
		setDistance( new EuclideanDistance() );
		dists = new HashMap<Integer, Double[]>();
		means = new HashMap<Integer, Double[]>();
	}

	@Override
	public AlgorithmInformation getInformation() {
		AlgorithmInformation result;

		result = new AlgorithmInformation();
		result.setField(AlgorithmInformation.Field.AUTHOR, "Unknown");
		result.setField(AlgorithmInformation.Field.YEAR, "1991");
		result.setField(AlgorithmInformation.Field.TITLE, "Distance-based euclidean algorithms");
		result.setField(AlgorithmInformation.Field.TYPE, AlgorithmInformation.Type.CLASSIFIER);
		result.setField(AlgorithmInformation.Field.CATEGORY, "Metrics");

		return result;
	}

	@Override
	public void train() {
		dists.clear();
		means.clear();

		Map<Integer, Set<Pattern>> classes = new HashMap<Integer, Set<Pattern>>();
		for( Pattern instance : getTrainSet() ) {
			Set<Pattern> partition = classes.containsKey( instance.getClassIndex() ) ?
									 classes.get( instance.getClassIndex() ) :
									 new HashSet<Pattern>();
			partition.add(instance);
			classes.put( instance.getClassIndex(), partition );
		}

		for( Integer clazz : classes.keySet() ){
			Set<Pattern> partition = classes.get( clazz );
			Double[] mean = mean( partition );
			Double[] dist = new Double[mean.length];
			Arrays.fill(dist, 0d);

			// Promedio de diferencia de distancias a la media
			for( Pattern instance : partition ) {
				Double[] vector = instance.toDoubleVector();
				for( int i = 0; i < vector.length; i++ ) {
					dist[i] += Math.abs( vector[i] - mean[i] ) / (double) partition.size();
				}
			}

			dists.put( clazz, dist );
			means.put( clazz, mean );
		}
	}

	@Override
	public int classify( Pattern instance ) {

		Map<Double, Integer> probabilities = new HashMap<Double, Integer>();
		for( Integer clazz : dists.keySet() ) {
			Double[] distPromPerFeature = dists.get( clazz );

			Double[] distCalcPerFeature = new Double[distPromPerFeature.length];
			for( int i = 0; i < distCalcPerFeature.length; i++ ) {
				double d = Math.abs( means.get(clazz)[i] - instance.toDoubleVector()[i] );
				distCalcPerFeature[i] = d;
			}

			double efectiveDistance = getDistance().distance( distPromPerFeature, distCalcPerFeature );
			probabilities.put( efectiveDistance, clazz );
		}

		Map<Double, Integer> sortedAsc = new TreeMap<Double, Integer>(probabilities);

		return (new ArrayList<Integer>(sortedAsc.values())).get(0);
	}

	public Distance getDistance() {
		return distance;
	}

	public void setDistance( Distance distance ) {
		this.distance = distance;
	}

	public Double[] mean( Set<Pattern> instances ) {
		Double[] mean = new Double[instances.iterator().next().size()];
		Arrays.fill(mean, 0d);
		for( Pattern instance : instances ) {
			Double[] vector = instance.toDoubleVector();
			for( int i = 0; i < instance.size(); i++ ) {
				mean[i] += vector[i] / (double) instance.size();
			}
		}
		return mean;
	}


}
