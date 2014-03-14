package org.sg.recognition;

import java.util.*;

/**
 * Project Name: PatternRecognition
 * Project Url: http://www.dotrow.com/projects/java/jcase
 * Author: Sergio Ceron
 * Version: 1.0
 * Date: 24/09/13 09:55 PM
 * Desc:
 */
public class DataSet extends HashSet<Pattern> {
	private String name;
	private List<Attribute> attributes;
	private Attribute classes;
	private int[] distribution;

	public DataSet() {
		attributes = new ArrayList<Attribute>();
	}

	public DataSet( DataSet other ) {
		name = other.getName();
		attributes = other.getAttributes();
		classes = other.getClasses();
		distribution = other.getDistribution();
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void addAttribute(Attribute attribute) {
		attributes.add( attribute );
	}

	public void removeAttribute( int index ) {
		attributes.remove( index );
	}

	public Attribute getClasses() {
		return classes;
	}

	public void setClasses( Attribute classes ) {
		this.classes = classes;
	}

	public int[] getDistribution() {
		return distribution;
	}

	public void setDistribution( int[] distribution ) {
		this.distribution = distribution;
	}

}
