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

package org.underserver.jbigmining.cuda;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 17/03/14 09:19 PM
 */

public class Add {

	public static void main( String[] _args ) {
		final int size = 1024 * 1024 * 50;

		final float[] a = new float[size];
		final float[] b = new float[size];

		for( int i = 0; i < size; i++ ) {
			a[i] = (float) ( Math.random() * 100 );
			b[i] = (float) ( Math.random() * 100 );
		}

		final float[] sum = new float[size];

		Kernel kernel = new Kernel() {
			@Override
			public void run() {
				int gid = getGlobalId();
				sum[gid] = (float) Math.acos( a[gid] * b[gid] / Math.sqrt( b[gid] ) );
				sum[gid] = (float) Math.exp( sum[gid] * a[gid] );
			}
		};

		long start = System.currentTimeMillis();
		kernel.execute( Range.create( 1024 ) );
		long end = System.currentTimeMillis();

		kernel.dispose();

		System.out.printf( "GPU: %d milis\n", end - start );

		start = System.currentTimeMillis();
		for( int i = 0; i < sum.length; i++ ) {
			sum[i] = (float) Math.acos( a[i] * b[i] / Math.sqrt( b[i] ) );
			sum[i] = (float) Math.exp( sum[i] * a[i] );
		}
		end = System.currentTimeMillis();

		System.out.printf( "CPU: %d milis\n", end - start );
	}

}
