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

import org.trifort.rootbeer.runtime.Kernel;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 17/03/14 03:42 PM
 */

public class QuickSortKernel implements Kernel {

	private int[] values;
	private int[] start;
	private int[] end;
	private int index;
	private int size;

	public QuickSortKernel( int[] a, int[] b, int[] c, int index, int size ) {

		this.start = a;
		this.end = b;
		this.values = c;
		this.index = index;
		this.size = size;

	}

	public int[] getValues() {
		return values;
	}

	@Override
	public void gpuMethod() {

		int pivot, L, R;
		start[index] = index;
		end[index] = size - 1;
		while( index >= 0 ) {
			L = start[index];
			R = end[index];
			if( L < R ) {
				pivot = values[L];
				while( L < R ) {
					while( values[R] >= pivot && L < R )
						R--;
					if( L < R )
						values[L++] = values[R];
					while( values[L] < pivot && L < R )
						L++;
					if( L < R )
						values[R--] = values[L];
				}
				values[L] = pivot;
				start[index + 1] = L + 1;
				end[index + 1] = end[index];
				end[index++] = L;
				if( end[index] - start[index] > end[index - 1] - start[index - 1] ) {
					// swap start[idx] and start[idx-1]
					int tmp = start[index];
					start[index] = start[index - 1];
					start[index - 1] = tmp;

					// swap end[idx] and end[idx-1]
					tmp = end[index];
					end[index] = end[index - 1];
					end[index - 1] = tmp;
				}

			} else {
				index--;
			}
		}
	}
}