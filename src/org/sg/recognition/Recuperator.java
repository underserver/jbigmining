package org.sg.recognition;

/**
 * Project Name: PatternRecognition
 * Project Url: http://www.dotrow.com/projects/java/jcase
 * Author: Sergio Ceron
 * Version: 1.0
 * Date: 3/09/13 11:10 PM
 * Desc:
 */
public abstract class Recuperator extends Algorithm {

	protected Recuperator( String name ) {
		super(name);
	}

	public abstract Pattern recover(Pattern pattern );
}
