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

package org.underserver.jbigmining.core;

/**
 * Project Name: PatternRecognition
 * Project Url: http://www.dotrow.com/projects/java/jcase
 * Author: Sergio Ceron F.
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
		return (double) vp / (double) ( vp + fn );
	}

	public double getRfp() {
		if( fp + vn == 0 ) return Double.NaN;
		return (double) fp / (double) ( fp + vn );
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
