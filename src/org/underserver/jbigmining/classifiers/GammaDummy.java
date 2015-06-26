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

import org.underserver.jbigmining.core.*;
import org.underserver.jbigmining.utils.Statistics;

import java.util.*;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 28/05/13 07:45 PM
 */
public class GammaDummy extends Classifier {

    private DataSet dataSet;
    private Map<Integer, Double> deviationStandard = new HashMap<Integer, Double>();

    public GammaDummy() {
        super( "GammaDummy" );
    }

    @Override
    public AlgorithmInformation getInformation() {
        AlgorithmInformation info;

        info = new AlgorithmInformation();

        return info;
    }

    @Override
    public void train() {
        DataSet trainSet = getTrainSet();

        dataSet = trainSet.clone();
        deviationStandard = new HashMap<Integer, Double>();

        List<Attribute> attributes = dataSet.getAttributes();
        for( int i = 0; i < attributes.size(); i++ ) {
            Attribute attribute = attributes.get( i );
            if( attribute.getType() == Attribute.Type.NUMERIC ) {
                deviationStandard.put( i, Statistics.StandartDeviation( i, dataSet ) );
            }
        }
    }

    @Override
    public int classify( Pattern instance ) {
        int[] distribution = dataSet.getDistribution();
        double[] similarity = similars( distribution, dataSet, instance );
        return maximum( similarity );
    }

    private int maximum( double[] dic ) {
        int classIndex = -1;
        double max = -Double.MAX_VALUE;
        for( int i = 0; i < dic.length; i++ ) {
            if( dic[i] > max ) {
                max = dic[i];
                classIndex = i;
            }
        }
        return classIndex;
    }

    private int similars( Pattern x, Pattern y, int i ) {
        int result = 0; //no similares por defecto
        double std;

        List<Attribute> attributes = dataSet.getAttributes();

        if( attributes.get( i ).getType() == Attribute.Type.NUMERIC ) {
            Double[] vectorX = x.toDoubleVector();
            Double[] vectorY = y.toDoubleVector();
            std = deviationStandard.get( i );
            if( ( x.get( i ) != null ) && ( y.get( i ) != null ) )
                if( Math.abs( vectorX[i] - vectorY[i] ) < std )
                    result = 1;
        }

        if( attributes.get( i ).getType() == Attribute.Type.NOMINAL ) {
            Object[] vectorX = x.toObjectVector();
            Object[] vectorY = y.toObjectVector();
            if( ( vectorX[i].toString().equals( vectorY[i].toString() ) ) && ( x != null ) && ( y != null ) ) // para que si son null no se parezcan
                result = 1;
        }

        return result;
    }

    private double[] similars( int[] dic, DataSet ds, Pattern obj ) {
        double[] similarity = new double[dic.length];
        for( int i = 0; i < dic.length; i++ ) {
            similarity[i] = 0;
        }

        List<Attribute> attributes = dataSet.getAttributes();

        for( Pattern pattern : ds ) {
            double gamma = 0;
            for( int j = 0; j < attributes.size(); j++ )
                gamma += similars( obj, pattern, j );
            similarity[pattern.getClassIndex()] += gamma;
        }

        for( int i = 0; i < dic.length; i++ ) {
            similarity[i] = similarity[i] / ( dic[i] * 1.0 );
        }

        return similarity;
    }


}
