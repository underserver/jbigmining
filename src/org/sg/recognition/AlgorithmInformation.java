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

/*
 * TechnicalInformation.java
 * Copyright (C) 2006-2012 University of Waikato, Hamilton, New Zealand
 */

package org.sg.recognition;

import java.util.Hashtable;
import java.util.Map;


public class AlgorithmInformation {

	public enum Type {
		CLASSIFIER ("Classifier"),
		RECUPERATOR ("Recuperator");

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

    public void setField(Field field, Object value) {
	    fields.put(field, value.toString());
    }

    public String getField(Field field) {
        if (fields.containsKey(field))
            return fields.get(field);
        else
            return "";
    }

    public Map<Field, String> getFields() {
        return fields;
    }

}
