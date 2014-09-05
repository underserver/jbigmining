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

import org.underserver.jbigmining.Attribute;
import org.underserver.jbigmining.DataSet;
import org.underserver.jbigmining.Parser;
import org.underserver.jbigmining.Pattern;

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
	private File file;

	public ARFFParser( String fileName ) {
		this.file = new File( fileName );
	}

	public ARFFParser( File file ) {
		this.file = file;
	}

	public DataSet parse() {
		DataSet dataSet = new DataSet();
		int[] distribution = null;

		try {
			FileInputStream fis = new FileInputStream( file );
			DataInputStream din = new DataInputStream( fis );
			BufferedReader br = new BufferedReader( new InputStreamReader( din ) );

			boolean dataFlag = false;
			String line;
			while( ( line = br.readLine() ) != null ) {

				if( line.toUpperCase().startsWith( "@RELATION" ) ) {
					dataSet.setName( readRelation( line ) );
				}

				if( line.toUpperCase().startsWith( "@ATTRIBUTE" ) ) {
					Attribute attribute = readAttribute( line );
					dataSet.addAttribute( attribute );
				}

				if( line.startsWith( "%" ) && dataFlag ) {
					dataSet.setDistribution( distribution );

					dataFlag = false;
				}

				if( dataFlag ) {
					Pattern instance = new Pattern();
					instance.setDataSet( dataSet );

					String values[] = line.split( "," );
					for( int i = 0; i < values.length - 1; i++ ) {
						Attribute attribute = dataSet.getAttributes().get( i );
						String rawValue = values[i].trim();
						if( attribute.getType() == Attribute.Type.NUMERIC ||
								attribute.getType() == Attribute.Type.REAL ) {
							instance.add( Double.valueOf( rawValue ) );
						} else {
							attribute.putValue( rawValue );
							instance.add( rawValue );
						}
					}
					instance.setClassValue( values[values.length - 1].trim() );

					dataSet.add( instance );

					distribution[instance.getClassIndex()] += 1;
				}

				if( line.toUpperCase().startsWith( "@DATA" ) ) {
					List<Attribute> attributes = dataSet.getAttributes();
					Attribute classes = attributes.get( attributes.size() - 1 );

					dataSet.setClasses( classes );
					dataSet.removeAttribute( attributes.size() - 1 );  // remove class attribute from all others attributes

					distribution = new int[classes.getValues().size()];

					dataFlag = true;
				}
			}

			br.close();
			din.close();
			fis.close();
		} catch ( Exception e ) {
			e.printStackTrace();
			return null;
		}

		return dataSet;
	}

	public Attribute readAttribute( String line ) {
		line = line.replaceAll( "\\t", " " );
		line = line.substring( 10 ).trim();
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
			attribute.setValues( Arrays.asList( values ) );
		} else {
			attribute.setType( Attribute.Type.valueOf( line.toUpperCase() ) );
		}

		return attribute;
	}

	public String readRelation( String line ) {
		line = line.replaceAll( "\\t", " " );
		return line.substring( 9 ).trim();
	}
}
