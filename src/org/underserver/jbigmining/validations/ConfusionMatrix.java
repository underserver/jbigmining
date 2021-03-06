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

package org.underserver.jbigmining.validations;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 27/11/13 10:07 PM
 */
public class ConfusionMatrix {
	private int[][] matrix;

	public ConfusionMatrix( int size ) {
		matrix = new int[size][size];
		init();
	}

	public void set( int i, int j, int val ) {
		matrix[i][j] = val;
	}

	public int get( int i, int j ) {
		return matrix[i][j];
	}

	public void add( int i, int j, int val ) {
		matrix[i][j] += val;
	}

	public void init() {
		for( int i = 0; i < matrix.length; i++ ) {
			for( int j = 0; j < matrix.length; j++ ) {
				matrix[i][j] = 0;
			}
		}
	}

	public int[][] getMatrix() {
		return matrix;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for( int i = 0; i < matrix.length; i++ ) {
			for( int j = 0; j < matrix.length; j++ ) {
				sb.append( matrix[i][j] );
				sb.append( " " );
			}
			sb.append( "\n" );
		}
		return sb.toString();
	}
}
