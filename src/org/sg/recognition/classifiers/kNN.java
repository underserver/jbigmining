package org.sg.recognition.classifiers;

import org.sg.recognition.AlgorithmInformation;
import org.sg.recognition.Classifier;
import org.sg.recognition.Pattern;
import org.sg.recognition.distances.Distance;
import org.sg.recognition.distances.EuclideanDistance;

import java.util.*;

import static org.sg.recognition.AlgorithmInformation.Field.*;
import static org.sg.recognition.AlgorithmInformation.Type.CLASSIFIER;

/**
 * Project Name: PatternRecognition
 * Project Url: http://www.dotrow.com/projects/java/jcase
 * Author: Sergio Ceron
 * Version: 1.0
 * Date: 15/09/13 01:22 PM
 * Desc:
 */
public class kNN extends Classifier {

	private Distance distance;
	private int k = 3;

	public kNN() {
		super("kNN");
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
		result.setField( AUTHOR, "D. Aha and D. Kibler");
		result.setField( YEAR, "1991");
		result.setField( TITLE, "Instance-based learning algorithms");
		result.setField( JOURNAL, "Machine Learning");
		result.setField( VOLUME, "6");
		result.setField( PAGES, "37-66");
		result.setField( TYPE, CLASSIFIER);
		result.setField( CATEGORY, "Metrics");

		return result;
	}

	@Override
	public void train() {
	}

	@Override
	public int classify( Pattern instance ) {
		Pattern[] nearest = nearestNeighbors( getTrainSet(), instance, getK() );

		int[] votes = new int[getTrainSet().getDistribution().length];
		Arrays.fill( votes, 0 );

		for( Pattern near : nearest ){
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

	public Pattern[] nearestNeighbors( Set<Pattern> instances, Pattern other, int kNearest ){
		Map<Double, Pattern> neighbors = new TreeMap<Double, Pattern>();
		for ( Pattern pattern : instances ) {
			double dist = getDistance().distance( pattern.toDoubleVector(), other.toDoubleVector() );
			neighbors.put( dist, pattern );
		}

		int index = 0;
		Pattern[] nearest = new Pattern[kNearest];
		for( Pattern neighbor : neighbors.values() ) {
			if( index >= kNearest ) break;
			nearest[index++] = neighbor;
		}
		return nearest;
	}

	public int getK() {
		return k;
	}

	public void setK( int k ) {
		this.k = k;
	}
}
