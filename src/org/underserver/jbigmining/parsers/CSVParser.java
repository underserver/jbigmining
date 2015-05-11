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
import java.util.HashMap;
import java.util.Map;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 13/03/14 03:30 PM
 */
public class CSVParser implements Parser {
	private File file;

	public CSVParser( String fileName ) {
		this.file = new File( fileName );
	}

	public CSVParser( File file ) {
		this.file = file;
	}

	public DataSet parse() {
		DataSet dataSet = new DataSet();
		int[] distribution = null;


		dataSet.setName( "Generic" );

		try {
			FileInputStream fis = new FileInputStream( file );
			DataInputStream din = new DataInputStream( fis );
			BufferedReader br = new BufferedReader( new InputStreamReader( din ) );


			Map<Integer, Integer> dist = new HashMap<Integer, Integer>();

			String line;
			int ix = 0;
			while( ( line = br.readLine() ) != null ) {

				if( ix == 0 ) {
					String commas[] = line.split( "," );
					for( int j = 0; j < commas.length - 1; j++ ) {
						dataSet.addAttribute( new Attribute( "a" + j, Attribute.Type.NUMERIC ) );
					}
					dataSet.setClasses( new Attribute( "clase" ) );
				}

				Pattern instance = new Pattern();
				instance.setDataSet( dataSet );

				String values[] = line.split( "," );
				for( int i = 0; i < values.length - 1; i++ ) {
					String rawValue = values[i].trim();
					instance.add( Double.valueOf( rawValue ) );
				}

				if( dataSet.getClasses().getValues().indexOf( values[values.length - 1] ) == -1 ) {
					dataSet.getClasses().getValues().add( values[values.length - 1] );
				}

				instance.setClassValue( values[values.length - 1] );

				dataSet.add( instance );

				if( dist.containsKey( instance.getClassIndex() ) ) {
					dist.put( instance.getClassIndex(), dist.get( instance.getClassIndex() ) + 1 );
				} else {
					dist.put( instance.getClassIndex(), 1 );
				}

				ix++;
			}

			distribution = new int[dist.size()];
			for( int i = 0; i < distribution.length; i++ ) {
				distribution[i] = dist.get( i );
			}

			dataSet.setDistribution( distribution );

			br.close();
			din.close();
			fis.close();
		} catch ( Exception e ) {
			e.printStackTrace();
			return null;
		}

		return dataSet;
	}

}
