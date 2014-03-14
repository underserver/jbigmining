package org.sg.recognition;

/**
 * Project Name: PatternRecognition
 * Project Url: http://www.dotrow.com/projects/java/jcase
 * Author: Sergio Ceron
 * Version: 1.0
 * Date: 3/09/13 11:21 PM
 * Desc:
 */
public class Metrics {
	private double performance = 0d;
	private int vp = 0;
	private int fp = 0;
	private int fn = 0;
	private int vn = 0;

	public Metrics() {

	}

	public void setPerformance( double performance ) {
		this.performance = performance;
	}

	public void setVp( int vp ) {
		this.vp = vp;
	}

	public void setFp( int fp ) {
		this.fp = fp;
	}

	public void setFn( int fn ) {
		this.fn = fn;
	}

	public void setVn( int vn ) {
		this.vn = vn;
	}

	public double getPerformance() {
		return performance;
	}

	public double getRvp() {
		if( vp + fn == 0 ) return Double.NaN;
		return (double)vp / (double)(vp + fn);
	}

	public double getRfp() {
		if( fp + vn == 0 ) return Double.NaN;
		return (double)fp / (double)(fp + vn);
	}

	public int getVp() {
		return vp;
	}

	public int getFp() {
		return fp;
	}

	public int getFn() {
		return fn;
	}

	public int getVn() {
		return vn;
	}

}
