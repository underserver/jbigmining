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

package org.sg.recognition.clusterers;

import org.sg.recognition.AlgorithmInformation;
import org.sg.recognition.Clusterer;
import org.sg.recognition.DataSet;
import org.sg.recognition.Pattern;
import org.sg.recognition.distances.Distance;
import org.sg.recognition.distances.EuclideanDistance;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 8/12/13 02:27 PM
 */
public class KMeans extends Clusterer {
	private Set<Pattern>[] clusters;
	private Double[][] centroids;
	private int maxIterations;
	private Distance distance;
	private int iterations;

	public KMeans() {
		super( "Simple K-Means" );
		maxIterations = Integer.MAX_VALUE;
		distance = new EuclideanDistance();
	}

	public KMeans( int numCentroids ) {
		this();
		this.centroids = new Double[numCentroids][];
		this.clusters = new Set[numCentroids];
	}

	public KMeans( Double[][] centroids ) {
		this();
		this.centroids = centroids;
		this.clusters = new Set[centroids.length];
	}

	public int getMaxIterations() {
		return maxIterations;
	}

	public void setMaxIterations( int maxIterations ) {
		this.maxIterations = maxIterations;
	}

	public Distance getDistance() {
		return distance;
	}

	public void setDistance( Distance distance ) {
		this.distance = distance;
	}

	public int getIterations() {
		return iterations;
	}

	@Override
	public AlgorithmInformation getInformation() {
		return new AlgorithmInformation();
	}

	@Override
	public void train() {
		if( centroids[0] == null ) {
			centroids = randomCentroids(centroids.length);
		}

		for( int c = 0; c < centroids.length; c++ ) {
			clusters[c] = new HashSet<Pattern>();
		}

		DataSet dataSet = getTrainSet();

		iterations = 0;
		for( int iteration = 0; iteration < maxIterations; iteration++ ) {

			for( int c = 0; c < centroids.length; c++ ) {
				clusters[c].clear();
			}

			// Step 1. Assignation
			for( Pattern instance : dataSet ) {
				int clusterIndex = 0;
				double distmin = distance.distance( instance.toDoubleVector(), centroids[0] );
				for( int centroid = 0; centroid < centroids.length; centroid++ ) {
					double dist = distance.distance( instance.toDoubleVector(), centroids[centroid] );
					if( dist < distmin ) {
						distmin = dist;
						clusterIndex = centroid;
					}
				}
				clusters[clusterIndex].add( instance );
			}

			// Step 2. Update
			int updates = 0;
			for( int centroid = 0; centroid < centroids.length; centroid++ ) {
				Double[] newCentroid = mean( clusters[centroid], centroids[centroid].length );
				if( distance.distance( centroids[centroid], newCentroid ) > 0.0001 ) {
					centroids[centroid] = newCentroid;
					updates++;
				}
			}

			iterations ++;

			if( updates == 0 ) break;
		}
	}

	// TODO: optimize when d(x,y)=0 break
	@Override
	public int cluster( Pattern instance ) {
		int clusterIndex = 0;
		double distmin = distance.distance( instance.toDoubleVector(), centroids[0] );
		for( int i = 1; i < centroids.length; i++ ) {
			double dist = distance.distance( instance.toDoubleVector(), centroids[i] );
			if( dist < distmin ) {
				distmin = dist;
				clusterIndex = i;
			}
		}
		return clusterIndex;
	}

	@Override
	public Set[] getClusters() {
		return clusters;
	}

	private Double[][] randomCentroids( int numCentroids ) {
		int features = getTrainSet().getAttributes().size();

		double[][] maxmin = new double[features][2];
		for( int a = 0; a < features; a++ ) {
			maxmin[a][0] = +Double.MAX_VALUE;
			maxmin[a][1] = -Double.MAX_VALUE;
		}

		// find min and max for each attribute
		for( Pattern instance : getTrainSet() ) {
			Double[] vector = instance.toDoubleVector();
			for( int i = 0; i < vector.length; i++ ) {
				double feature = vector[i];
				if( feature < maxmin[i][0] )
					maxmin[i][0] = feature;
				if( feature > maxmin[i][1] )
					maxmin[i][1] = feature;

			}
		}

		Random random = new Random();

		Double[][] centroids = new Double[numCentroids][];
		for( int centroid = 0; centroid < numCentroids; centroid++ ) {
			centroids[centroid] = new Double[features];
			for (int a = 0; a < features; a++) {
				centroids[centroid][a] = maxmin[a][0] + (maxmin[a][1] - maxmin[a][0]) * random.nextDouble();
			}
		}

		return centroids;
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
