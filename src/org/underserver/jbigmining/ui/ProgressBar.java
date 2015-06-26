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

package org.underserver.jbigmining.ui;

/**
 * Created by sergio on 11/06/15.
 */
public class ProgressBar {
    private int max;

    public ProgressBar() {
    }

    public void draw(){
        System.out.print("[          ]");
        System.out.flush(); // the flush method prints it to the screen

        // 11 '\b' chars: 1 for the ']', the rest are for the spaces
        System.out.print("\b\b\b\b\b\b\b\b\b\b\b");
        System.out.flush();
        try {
            Thread.sleep(500); // just to make it easy to see the changes
        } catch( InterruptedException e ) {
            e.printStackTrace();
        }

        for(int i = 0; i < 10; i++)
        {
            System.out.print("█"); //overwrites a space
            System.out.flush();
            try {
                Thread.sleep( 100 );
            } catch( InterruptedException e ) {
                e.printStackTrace();
            }
        }

        System.out.print("] Done\n"); //overwrites the ']' + adds chars
        System.out.flush();
    }
}
