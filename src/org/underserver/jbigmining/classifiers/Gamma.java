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
import org.underserver.jbigmining.utils.Conversions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 28/05/13 07:45 PM
 */
public class Gamma extends Classifier {

    private DataSet dataSet;
    private List<Double> weights = new ArrayList<Double>();
    private Map<Integer, MobiusData> dic = new HashMap<Integer, MobiusData>();
    private int classResult = -1;
    private double max_similarity;
    private double ro;
    private double ro_cero;
    private int u = 0;

    public Gamma() {
        super( "GammaH" );
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

        int attributesSize = trainSet.getAttributes().size();

        dic.clear();
        weights.clear();
        dataSet = mobius( trainSet );

        for( int i = 0; i < attributesSize; i++ ) {
            weights.add( 1d );
        }

    }

    @Override
    public int classify( Pattern instance ) {
        double theta = 0;
        Pattern new_obj = mobius( instance );
        Double[] similarity = similarity( dataSet, new_obj, theta );
        boolean unique = uniqueMaximum( similarity );
        if( unique )
            return classResult;
        else {
            theta++;
            while( ( theta < ro_cero ) ) {
                similarity = similarity( dataSet, new_obj, theta );
                unique = uniqueMaximum( similarity );
                if( max_similarity > u ) {
                    if( unique )
                        return classResult;
                } else if( theta >= ro ) {
                    return classResult;
                }
                theta++;
            }
            return classResult;
        }
    }

    private boolean uniqueMaximum( Double[] similarities ) {
        double max = -Double.MAX_VALUE;
        boolean unique = true;

        for( int i = 0; i < similarities.length; i++ ) {
            if( similarities[i] > max ) {
                max = similarities[i];
                classResult = i;
                unique = true;
            } else if( similarities[i] == max ) {
                unique = false;
            }
        }
        max_similarity = max;
        return unique;
    }

    private Double[] similarity( DataSet dataSet, Pattern pattern, double theta ) {
        int[] distribution = dataSet.getDistribution();
        Double[] similarities = new Double[distribution.length];
        for( int i = 0; i < distribution.length; i++ ) {
            similarities[i] = 0d;
        }

        for( Pattern p : dataSet ) {
            double gamma = 0;
            for( int j = 0; j < dataSet.getAttributes().size(); j++ ) {
                gamma += similarity( pattern, p, theta, j ) * weights.get( j );
            }
            similarities[p.getClassIndex()] += gamma;
        }

        for( int i = 0; i < distribution.length; i++ ) {
            similarities[i] = similarities[i] / ( distribution[i] * 1.0 );
        }

        return similarities;
    }

    private double similarity( Pattern x, Pattern y, double theta, int featureIndex ) {
        double differentBits;

        Attribute attribute = x.getDataSet().getAttributes().get( featureIndex );
        //if( attribute.getType() == Attribute.Type.NUMERIC ) {
            if( !x.isMissing( featureIndex ) && !y.isMissing( featureIndex ) )
                differentBits = Math.abs( x.getInt( featureIndex ) - y.getInt( featureIndex ) );
            else
                differentBits = theta + 1.0;
        //}
        /* else {
            if( !x.isMissing( featureIndex ) && !y.isMissing( featureIndex ) )
                differentBits = x.toString().equals( y.toString() ) ? 0 : theta + 1.0;
            else
                differentBits = theta + 1.0;
        }*/
        if( differentBits <= theta )
            return 1d;
        else
            return 0d;
    }

    private DataSet mobius( DataSet training ) {
        convertMetaobject( training );
        computeParameters();
        DataSet ds = new DataSet( training );

        for( Pattern pattern : training ) {
            Double[] newVector = new Double[pattern.size()];
            for( int j = 0; j < pattern.size(); j++ ) {
                Attribute attribute = training.getAttributes().get( j );
                Double new_value = pattern.isMissing( j ) ? null : (double) pattern.getInt( j );
                if( attribute.getType() == Attribute.Type.NUMERIC ) {
                    if( dic.get( j ).min < 0 )
                        new_value -= dic.get( j ).min;
                }
                newVector[j] = new_value;
            }
            Pattern newPattern = new Pattern( newVector );
            newPattern.setDataSet( ds );
            newPattern.setClassIndex( pattern.getClassIndex() );
            ds.add( newPattern );
            /*for( int i = 0; i < pattern.size(); i++ ) {
                if( pattern.isMissing( i ) )
                    System.out.print( "?," );
                else
                    System.out.print( pattern.getDouble( i ) + "," );
            }
            System.out.print( " -> " );
            for( int i = 0; i < newVector.length; i++ ) {
                if( newVector[i] != null )
                    System.out.print( newVector[i] + "," );
                else
                    System.out.print( "?," );
            }
            System.out.println();*/
        }
        //System.out.println( "" );
        return ds;
    }

    private void convertMetaobject( DataSet training ) {
        List<Attribute> attributes = training.getAttributes();
        for( int i = 0; i < attributes.size(); i++ ) {
            Attribute attribute = attributes.get( i );
            if( attribute.getType() == Attribute.Type.NUMERIC ) {
                double min = Double.MAX_VALUE;
                double max = -Double.MAX_VALUE;
                int max_dec = -Integer.MAX_VALUE;

                for( Pattern pattern : training ) {
                    Double[] vector = pattern.toDoubleVector();
                    if( vector[i] != null ) {
                        double value = vector[i];
                        if( value < min )
                            min = value;
                        if( value > max )
                            max = value;

                        int decimals = decimals( value );
                        if( decimals > max_dec )
                            max_dec = decimals;
                    }
                }
                MobiusData mdata = new MobiusData( max_dec, min, max );
                dic.put( i, mdata );
            }
        }
    }

    private int decimals( double value ) {
        String s = value + "";
        int idx = s.indexOf( '.' );
        return s.length() - idx - 1;
    }

    private void computeParameters() {
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;

        double current;
        for( Integer idx : dic.keySet() ) {
            current = dic.get( idx ).max;
            if( dic.get( idx ).min < 0 )
                current += dic.get( idx ).min;

            if( current < min )
                min = current;
            if( current > max )
                max = current;
        }

        ro = max;
        ro_cero = min;
    }

    private Pattern mobius( Pattern pattern ) {
        Pattern result = new Pattern();
        for( int j = 0; j < pattern.size(); j++ ) {
            Attribute attribute = pattern.getDataSet().getAttributes().get( j );
            Double new_value = pattern.isMissing( j ) ? null : (double) pattern.getInt( j );
            if( attribute.getType() == Attribute.Type.NUMERIC && new_value != null ) {
                if( new_value < dic.get( j ).min ) // TODO: Error fixed
                    new_value = (double) Conversions.doubleToInt( dic.get( j ).min, BigDecimal.ROUND_HALF_EVEN );
                if( new_value > dic.get( j ).max )
                    new_value = (double) Conversions.doubleToInt( dic.get( j ).max, BigDecimal.ROUND_HALF_EVEN );
            }
            result.add( new_value );

        }
        result.setDataSet( pattern.getDataSet() );
        result.setClassIndex( pattern.getClassIndex() );
        return result;
    }

    private class MobiusData {
        private int dec;
        private double min;
        private double max;

        public MobiusData( int dec, double min, double max ) {
            this.dec = dec;
            this.min = min;
            this.max = max;
        }

    }


}
