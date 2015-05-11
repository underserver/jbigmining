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

package org.underserver.jbigmining.core;


import org.underserver.jbigmining.utils.Conversions;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 20/11/13 01:57 PM
 */
public class Pattern implements Serializable {

    private static final long serialVersionUID = 6471234118516275070L;

    private List<Double> features;
    private DataSet dataSet;
    private int classIndex;

    public Pattern() {
        features = new ArrayList<Double>();
    }

    public Pattern( Double[] vector ) {
        features = Arrays.asList( vector );
    }

    public void add( String feature ) {
        Attribute attribute = getDataSet().getAttributes().get( features.size() );
        List<String> values = attribute.getValues();
        Double value = (double) values.indexOf( feature );
        add( value );
    }

    public void addMissing() {
        features.add( null );
    }

    public boolean isMissing( int index ) {
        return features.get( index ) == null;
    }

    public void add( Double feature ) {
        features.add( feature );
    }

    public void set( int index, String feature ) {
        Attribute attribute = getDataSet().getAttributes().get( index );
        List<String> values = attribute.getValues();
        Double value = (double) values.indexOf( feature );
        set( index, value );
    }

    public void set( int index, Double feature ) {
        features.set( index, feature );
    }

    public Object get( int index ) {
        Attribute attribute = getDataSet().getAttributes().get( index );
        if( attribute.getType() != Attribute.Type.NUMERIC ) {
            List<String> values = attribute.getValues();
            return values.get( features.get( index ).intValue() );
        }
        return features.get( index );
    }

    public int getInt( int index ) {
        return Conversions.doubleToInt( getDouble( index ),
                BigDecimal.ROUND_HALF_EVEN );
    }

    public double getDouble( int index ) {
        return features.get( index ).doubleValue();
    }

    public boolean getBoolean( int index ) {
        Object object = get( index );
        if( object.equals( "1" ) || object.toString().toLowerCase().equals( "true" ) ) {
            return true;
        }
        return false;
    }

    public int size() {
        return features.size();
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet( DataSet dataSet ) {
        this.dataSet = dataSet;
    }

    public int getClassIndex() {
        return classIndex;
    }

    public void setClassIndex( int classIndex ) {
        this.classIndex = classIndex;
    }

    public void setClassValue( String value ) {
        this.classIndex = dataSet.getClasses().getValues().indexOf( value );
    }

    public String getClassValue() {
        return dataSet.getClasses().getValues().get( classIndex );
    }

    public Object[] toObjectVector() {
        List original = new ArrayList();
        for( int i = 0; i < size(); i++ ) {
            original.add( get( i ) );
        }
        return original.toArray();
    }

    public Double[] toDoubleVector() {
        /*Double[] vector = new Double[size()];
        for( int i = 0; i < size(); i++ ) {
            if( get( i ) != null )
                vector[i] = (Double) get( i );
            else
                vector[i] = null;
        }
        return vector;*/
        return features.toArray( new Double[features.size()] );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "[" );
        for( int i = 0; i < size(); i++ ) {
            if( get( i ) != null )
                sb.append( get( i ) );
            else
                sb.append( "?" );
            if( i < size() - 1 )
                sb.append( "," );
        }
        sb.append( "] -> " );
        sb.append( getClassValue() );
        return sb.toString();
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o ) return true;
        if( o == null || getClass() != o.getClass() ) return false;

        Pattern pattern = (Pattern) o;

        return features.equals( pattern.features );

    }

}
