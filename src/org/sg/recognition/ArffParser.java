package org.sg.recognition;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Author: Antonio, Elias, Cero
 * Date: 28/05/13
 * Time: 02:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArffParser implements Parser {
    private File file;

    public ArffParser( String fileName ) {
		this.file = new File(fileName);
	}

	public ArffParser( File file ) {
		this.file = file;
	}

    public DataSet parse() {
	    DataSet dataSet = new DataSet();
	    int[] distribution = null;

        try {
            FileInputStream fis = new FileInputStream(file);
            DataInputStream din = new DataInputStream(fis);
            BufferedReader  br  = new BufferedReader( new InputStreamReader( din ) );

            boolean dataFlag = false;
	        String line;
            while ((line = br.readLine()) != null) {

	            if( line.toUpperCase().startsWith("@RELATION") ) {
		            dataSet.setName( readRelation(line) );
	            }

	            if( line.toUpperCase().startsWith("@ATTRIBUTE") ) {
		            Attribute attribute = readAttribute( line );
					dataSet.addAttribute( attribute );
	            }

	            if( line.startsWith("%") && dataFlag ) {
		            dataSet.setDistribution( distribution );

		            dataFlag = false;
	            }

	            if( dataFlag ) {
		            Pattern instance = new Pattern();
		            instance.setDataSet( dataSet );

		            String values[] = line.split(",");
		            for ( int i = 0; i < values.length - 1; i++ ) {
			            Attribute attribute = dataSet.getAttributes().get(i);
			            String rawValue = values[i].trim();
			            if( attribute.getType() == Attribute.Type.NUMERIC ||
					            attribute.getType() == Attribute.Type.REAL ) {
				            instance.add( Double.valueOf( rawValue ) );
			            } else {
				            attribute.putValue( rawValue );
			                instance.add( rawValue );
			            }
		            }
		            instance.setClassValue( values[values.length - 1] );

		            dataSet.add(instance);

		            distribution[instance.getClassIndex()] += 1;
	            }

	            if( line.toUpperCase().startsWith("@DATA") ) {
		            List<Attribute> attributes = dataSet.getAttributes();
		            Attribute classes = attributes.get( attributes.size() - 1 );

		            dataSet.setClasses( classes );
		            dataSet.removeAttribute( attributes.size() - 1 );  // remove class attribute from all others attributes

		            distribution = new int[ classes.getValues().size() ];

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

	public Attribute readAttribute(String line) {
		line = line.replaceAll("\\t", " ");
		line = line.substring(10).trim();
		String name;
		if( line.startsWith("\"") ) {
			int begin = line.indexOf('"');
			int last = line.lastIndexOf('"');
			name =  line.substring(begin + 1, last);
		} else {
			name = line.split(" ")[0];
		}

		line = line.replaceFirst( name, "" ).trim();

		Attribute attribute = new Attribute( name );
		if( line.startsWith("{") ) {
			line = line.replaceAll("\"", "");
			String[] values = line.replaceAll("\\{", "").replaceAll("\\}", "").split(",");
			attribute.setType( Attribute.Type.NOMINAL );
			attribute.setValues( Arrays.asList(values) );
		} else {
			attribute.setType( Attribute.Type.valueOf(line.toUpperCase()) );
		}

		return attribute;
	}

	public String readRelation(String line) {
		line = line.replaceAll("\\t", " ");
		return line.substring(9).trim();
	}
}
