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
import org.trifort.rootbeer.runtime.Rootbeer;
import org.trifort.rootbeer.runtime.util.Stopwatch;

import java.util.ArrayList;
import java.util.List;

/**
 * -
 *
 * @author Sergio Ceron F.
 * @version rev: %I%
 * @date 17/03/14 03:41 PM
 */

public class QuickSort {

	public int[] backrdsArray( int len ) {
		int[] ret = new int[len];
		for( int i = 0; i < len; ++i ) {
			ret[i] = len - i;
		}
		return ret;
	}

	public void printArray( String title, int[] array ) {
		System.out.print( title + ": [ " );
		for( int i = 0; i < 64; ++i ) {
			System.out.print( array[i] + " " );
		}
		System.out.println( "]" );
	}

	public void sortGPU() {
		List<Kernel> jobs = new ArrayList<Kernel>();
		for( int i = 0; i < 50; ++i ) {
			int len = 200;
			int[] a = new int[len];
			int[] b = new int[len];
			int[] c = backrdsArray( len );
			QuickSortKernel kernel = new QuickSortKernel( a, b, c, 0, len );
			jobs.add( kernel );
		}
		Rootbeer rootbeer = new Rootbeer();
		rootbeer.run( jobs );

		QuickSortKernel job0 = (QuickSortKernel) jobs.get( 0 );
		int[] result = job0.getValues();
		printArray( "gpu_result", result );
	}

	public void swap( int array[], int index1, int index2 ) {
		int temp = array[index1];
		array[index1] = array[index2];
		array[index2] = temp;
	}

	public void quicksort( int array[], int start, int end ) {
		int i = start;                          // index of left-to-right scan
		int k = end;                            // index of right-to-left scan

		if( end - start >= 1 ) {                  // check that there are at least two elements to sort
			int pivot = array[start];       // set the pivot as the first element in the partition

			while( k > i ) {                   // while the scan indices from left and right have not met,
				while( array[i] <= pivot && i <= end && k > i ) {  // from the left, look for the first
					i++;                                    // element greater than the pivot
				}
				while( array[k] > pivot && k >= start && k >= i ) { // from the right, look for the first
					k--;                                        // element not greater than the pivot
				}
				if( k > i ) {                                       // if the left seekindex is still smaller than
					swap( array, i, k );                      // the right index, swap the corresponding elements
				}
			}
			swap( array, start, k );          // after the indices have crossed, swap the last element in
			// the left partition with the pivot
			quicksort( array, start, k - 1 ); // quicksort the left partition
			quicksort( array, k + 1, end );   // quicksort the right partition
		} else {    // if there is only one element in the partition, do not do any sorting
			return;                     // the array is sorted, so exit
		}
	}

	public void sortCPU() {
		int[] result = null;
		for( int i = 0; i < 50; ++i ) {
			int len = 200;
			int[] values = backrdsArray( len );
			quicksort( values, 0, len - 1 );
			if( result == null ) {
				result = values;
			}
		}
		printArray( "cpu_result", result );
	}

	public void execTest() {
		Stopwatch watch1 = new Stopwatch();
		watch1.start();
		sortGPU();
		watch1.stop();

		Stopwatch watch2 = new Stopwatch();
		watch2.start();
		sortCPU();
		watch2.stop();

		System.out.println();
		System.out.println( "gpu time: " + watch1.elapsedTimeMillis() );
		System.out.println( "cpu time: " + watch2.elapsedTimeMillis() );
	}

	public static void main( String[] args ) {
		QuickSort app = new QuickSort();
		app.execTest();
	}
}