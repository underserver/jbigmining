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

package org.underserver.jbigmining.utils;

import org.underserver.jbigmining.core.DataSet;
import org.underserver.jbigmining.core.Pattern;

/**
 * Created by sergio on 27/04/15.
 */
public class Statistics {

    public static double StandartDeviation( int featureIndex, DataSet dataSet ) {
        double mean = Mean( featureIndex, dataSet );

        double sum = 0, count = 0;
        for( Pattern pattern : dataSet ) {
            if( pattern.get( featureIndex ) != null ) {
                Double[] vector = pattern.toDoubleVector();
                sum += Math.pow( mean - vector[featureIndex], 2 );
                ++count;
            }
        }

        return Math.sqrt( 1 / ( count - 1 ) * sum );
    }

    public static double Mean( int featureIndex, DataSet dataSet ) {
        double sum = 0, count = 0;
        for( Pattern pattern : dataSet ) {
            if( pattern.get( featureIndex ) != null ) {
                Double[] vector = pattern.toDoubleVector();
                sum += vector[featureIndex];
                ++count;
            }
        }

        return ( count != 0 ) ? sum / count : 0;
    }
}
