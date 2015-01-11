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

package org.underserver.jbigmining;

import java.util.ArrayList;
import java.util.List;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 21/11/13 12:19 PM
 */
public class Attribute {

	private List<String> values;
	private String name;
	private Type type;

	public Attribute( String name ) {
		setName( name );
		setType( Type.NUMERIC );
		setValues( new ArrayList<String>( 0 ) );
	}

	public Attribute( String name, Type type ) {
		this.name = name;
		this.type = type;
		setValues( new ArrayList<String>( 0 ) );
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues( List<String> values ) {
		this.values = values;
	}

	public void putValue( String value ) {
		if( !values.contains( value.trim() ) ) {
			try {
				values.add( value.trim() );
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType( Type type ) {
		this.type = type;
	}

	public static enum Type {
		NUMERIC,
		REAL,
		STRING,
		NOMINAL,
		RELATIONAL
	}
}
