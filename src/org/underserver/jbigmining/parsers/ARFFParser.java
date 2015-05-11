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

package org.underserver.jbigmining.parsers;

import org.underserver.jbigmining.core.Attribute;
import org.underserver.jbigmining.core.DataSet;
import org.underserver.jbigmining.core.Parser;
import org.underserver.jbigmining.core.Pattern;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 28/05/13 02:30 PM
 */
public class ARFFParser implements Parser {
    private final File file;

    public ARFFParser( String fileName ) {
        this.file = new File( fileName );
    }

    public ARFFParser( File file ) {
        this.file = file;
    }

    public DataSet parse() {
        DataSet dataSet = new DataSet();

        try {
            readHeaders( dataSet );
            readData( dataSet );
        } catch( IOException e ) {
            e.printStackTrace();
            return null;
        }

        return dataSet;
    }

    public void readData( DataSet dataSet ) throws IOException {
        FileInputStream fis = new FileInputStream( file );
        DataInputStream din = new DataInputStream( fis );
        BufferedReader br = new BufferedReader( new InputStreamReader( din ) );

        dataSet.setDistribution( new int[dataSet.getClasses().getValues().size()] );

        boolean dataFlag = false;
        String line;
        while( ( line = br.readLine() ) != null ) {

            if( line.toUpperCase().startsWith( "@DATA" ) ) {
                dataFlag = true;
                continue;
            }

            if( dataFlag && !line.trim().equals( "%" ) ) {
                Pattern instance = new Pattern();
                instance.setDataSet( dataSet );

                String values[] = line.split( "," );
                for( int i = 0; i < values.length - 1; i++ ) {
                    Attribute attribute = dataSet.getAttributes().get( i );
                    String rawValue = values[i].trim();
                    if( rawValue.equals( "?" ) || rawValue.isEmpty() ) {
                        instance.addMissing();
                        continue;
                    }
                    if( attribute.getType() == Attribute.Type.NUMERIC ) {
                        instance.add( Double.valueOf( rawValue ) );
                    } else {
                        attribute.putValue( rawValue );
                        instance.add( rawValue );
                    }
                }

                dataSet.add( instance );

                if( !values[values.length - 1].equals( "?" ) && !values[values.length - 1].isEmpty() ) {
                    instance.setClassValue( values[values.length - 1].trim() );
                    dataSet.getDistribution()[instance.getClassIndex()] += 1;
                }
            }
        }

        br.close();
        din.close();
        fis.close();
    }

    public void readHeaders( DataSet dataSet ) throws IOException {
        FileInputStream fis = new FileInputStream( file );
        DataInputStream din = new DataInputStream( fis );
        BufferedReader br = new BufferedReader( new InputStreamReader( din ) );

        String line;
        while( ( line = br.readLine() ) != null ) {
            if( line.toUpperCase().startsWith( "@RELATION" ) ) {
                dataSet.setName( readRelation( line ) );
            } else if( line.toUpperCase().startsWith( "@ATTRIBUTE" ) ) {
                Attribute attribute = readAttribute( line );
                dataSet.addAttribute( attribute );
            } else if( line.toUpperCase().startsWith( "@DATA" ) ) {
                break;
            }
        }

        List<Attribute> attributes = dataSet.getAttributes();
        Attribute classes = attributes.get( attributes.size() - 1 );

        dataSet.setClasses( classes );
        dataSet.removeAttribute( attributes.size() - 1 );  // remove class attribute from all others attributes

        br.close();
        din.close();
        fis.close();
    }

    private Attribute readAttribute( String line ) {
        line = line.replaceAll( "\\t", " " );
        line = line.substring( "@ATTRIBUTE".length() ).trim();
        String name;
        if( line.startsWith( "\"" ) ) {
            int begin = line.indexOf( '"' );
            int last = line.lastIndexOf( '"' );
            name = line.substring( begin + 1, last );
        } else {
            name = line.split( " " )[0];
        }

        line = line.replaceFirst( name, "" ).trim();

        Attribute attribute = new Attribute( name );
        if( line.startsWith( "{" ) ) {
            line = line.replaceAll( "\"", "" );
            String[] values = line.replaceAll( "\\{", "" ).replaceAll( "\\}", "" ).split( "," );
            attribute.setType( Attribute.Type.NOMINAL );
            for( int i = 0; i < values.length; i++ ) {
                values[i] = values[i].trim();
            }
            attribute.setValues( Arrays.asList( values ) );
            if( isBoolean( attribute.getValues() ) )
                attribute.setType( Attribute.Type.BOOLEAN );
        } else {
            if( line.toUpperCase().equals( "REAL" )
                    || line.toUpperCase().equals( "NUMERIC" )
                    || line.toUpperCase().equals( "INTEGER" ) )
                attribute.setType( Attribute.Type.NUMERIC );
        }

        return attribute;
    }

    private boolean isBoolean( List<String> values ) {
        for( String value : values ) {
            if( !( value.equals( "0" ) || value.equals( "1" )
                    || value.toLowerCase().equals( "true" )
                    || value.toLowerCase().equals( "false" ) ) ) {
                return false;
            }
        }
        return true;
    }

    private String readRelation( String line ) {
        line = line.replaceAll( "\\t", " " );
        return line.substring( 9 ).trim();
    }
}
