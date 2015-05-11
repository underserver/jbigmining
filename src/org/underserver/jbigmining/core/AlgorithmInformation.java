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

/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.underserver.jbigmining.core;

import java.util.Hashtable;
import java.util.Map;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 3/09/13 11:10 PM
 */
public class AlgorithmInformation {

	public enum Type {
		CLASSIFIER( "Classifier" ),
		RECUPERATOR( "Recuperator" );

		private final String title;

		private Type( String title ) {
			this.title = title;
		}

		@Override
		public String toString() {
			return title;
		}
	}

	public enum Field {
		ADDRESS,
		AUTHOR,
		BOOKTITLE,
		YEAR,
		CHAPTER,
		CROSSREF,
		EDITION,
		EDITOR,
		HOWPUBLISHED,
		INSTITUTION,
		JOURNAL,
		KEY,
		MONTH,
		NOTE,
		NUMBER,
		ORGANIZATION,
		PAGES,
		PUBLISHER,
		SCHOOL,
		SERIES,
		TITLE,
		TYPE,
		CATEGORY,
		VOLUME,
		ABSTRACT,
		COPYRIGHT,
		ISBN,
		ISSN,
		KEYWORDS,
		LANGUAGE,
		LOCATION,
		PRICE,
		SIZE,
		URL
	}

	protected Hashtable<Field, String> fields = new Hashtable<Field, String>();

	public AlgorithmInformation() {
	}

	public void setField( Field field, Object value ) {
		fields.put( field, value.toString() );
	}

	public String getField( Field field ) {
		if( fields.containsKey( field ) )
			return fields.get( field );
		else
			return "";
	}

	public Map<Field, String> getFields() {
		return fields;
	}

}
