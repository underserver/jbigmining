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
 * Created by sergio on 09/06/15.
 */

public class MyHeap {

    /** the heap. */
    MyHeapElement m_heap[] = null;

    /**
     * constructor.
     *
     * @param maxSize the maximum size of the heap
     */
    public MyHeap(int maxSize) {
        if ((maxSize % 2) == 0) {
            maxSize++;
        }

        m_heap = new MyHeapElement[maxSize + 1];
        m_heap[0] = new MyHeapElement(0, 0);
    }

    /**
     * returns the size of the heap.
     *
     * @return the size
     */
    public int size() {
        return m_heap[0].index;
    }

    /**
     * peeks at the first element.
     *
     * @return the first element
     */
    public MyHeapElement peek() {
        return m_heap[1];
    }

    /**
     * returns the first element and removes it from the heap.
     *
     * @return the first element
     * @throws Exception if no elements in heap
     */
    public MyHeapElement get() throws Exception {
        if (m_heap[0].index == 0) {
            throw new Exception("No elements present in the heap");
        }
        MyHeapElement r = m_heap[1];
        m_heap[1] = m_heap[m_heap[0].index];
        m_heap[0].index--;
        downheap();
        return r;
    }

    /**
     * adds the value to the heap.
     *
     * @param i the index
     * @param d the distance
     * @throws Exception if the heap gets too large
     */
    public void put(int i, double d) throws Exception {
        if ((m_heap[0].index + 1) > (m_heap.length - 1)) {
            throw new Exception("the number of elements cannot exceed the "
                    + "initially set maximum limit");
        }
        m_heap[0].index++;
        m_heap[m_heap[0].index] = new MyHeapElement(i, d);
        upheap();
    }

    /**
     * Puts an element by substituting it in place of the top most element.
     *
     * @param i the index
     * @param d the distance
     * @throws Exception if distance is smaller than that of the head element
     */
    public void putBySubstitute(int i, double d) throws Exception {
        MyHeapElement head = get();
        put(i, d);
        // System.out.println("previous: "+head.distance+" current: "+m_heap[1].distance);
        if (head.distance == m_heap[1].distance) { // Utils.eq(head.distance,
            // m_heap[1].distance)) {
            putKthNearest(head.index, head.distance);
        } else if (head.distance > m_heap[1].distance) { // Utils.gr(head.distance,
            // m_heap[1].distance)) {
            m_KthNearest = null;
            m_KthNearestSize = 0;
            initSize = 10;
        } else if (head.distance < m_heap[1].distance) {
            throw new Exception("The substituted element is smaller than the "
                    + "head element. put() should have been called "
                    + "in place of putBySubstitute()");
        }
    }

    /** the kth nearest ones. */
    MyHeapElement m_KthNearest[] = null;

    /** The number of kth nearest elements. */
    int m_KthNearestSize = 0;

    /** the initial size of the heap. */
    int initSize = 10;

    /**
     * returns the number of k nearest.
     *
     * @return the number of k nearest
     * @see #m_KthNearestSize
     */
    public int noOfKthNearest() {
        return m_KthNearestSize;
    }

    /**
     * Stores kth nearest elements (if there are more than one).
     *
     * @param i the index
     * @param d the distance
     */
    public void putKthNearest(int i, double d) {
        if (m_KthNearest == null) {
            m_KthNearest = new MyHeapElement[initSize];
        }
        if (m_KthNearestSize >= m_KthNearest.length) {
            initSize += initSize;
            MyHeapElement temp[] = new MyHeapElement[initSize];
            System.arraycopy(m_KthNearest, 0, temp, 0, m_KthNearest.length);
            m_KthNearest = temp;
        }
        m_KthNearest[m_KthNearestSize++] = new MyHeapElement(i, d);
    }

    /**
     * returns the kth nearest element or null if none there.
     *
     * @return the kth nearest element
     */
    public MyHeapElement getKthNearest() {
        if (m_KthNearestSize == 0) {
            return null;
        }
        m_KthNearestSize--;
        return m_KthNearest[m_KthNearestSize];
    }

    /**
     * performs upheap operation for the heap to maintian its properties.
     */
    protected void upheap() {
        int i = m_heap[0].index;
        MyHeapElement temp;
        while (i > 1 && m_heap[i].distance > m_heap[i / 2].distance) {
            temp = m_heap[i];
            m_heap[i] = m_heap[i / 2];
            i = i / 2;
            m_heap[i] = temp; // this is i/2 done here to avoid another division.
        }
    }

    /**
     * performs downheap operation for the heap to maintian its properties.
     */
    protected void downheap() {
        int i = 1;
        MyHeapElement temp;
        while (((2 * i) <= m_heap[0].index && m_heap[i].distance < m_heap[2 * i].distance)
                || ((2 * i + 1) <= m_heap[0].index && m_heap[i].distance < m_heap[2 * i + 1].distance)) {
            if ((2 * i + 1) <= m_heap[0].index) {
                if (m_heap[2 * i].distance > m_heap[2 * i + 1].distance) {
                    temp = m_heap[i];
                    m_heap[i] = m_heap[2 * i];
                    i = 2 * i;
                    m_heap[i] = temp;
                } else {
                    temp = m_heap[i];
                    m_heap[i] = m_heap[2 * i + 1];
                    i = 2 * i + 1;
                    m_heap[i] = temp;
                }
            } else {
                temp = m_heap[i];
                m_heap[i] = m_heap[2 * i];
                i = 2 * i;
                m_heap[i] = temp;
            }
        }
    }

    /**
     * returns the total size.
     *
     * @return the total size
     */
    public int totalSize() {
        return size() + noOfKthNearest();
    }

    public static class MyHeapElement {

        /** the index of this element. */
        public int index;

        /** the distance of this element. */
        public double distance;

        /**
         * constructor.
         *
         * @param i the index
         * @param d the distance
         */
        public MyHeapElement(int i, double d) {
            distance = d;
            index = i;
        }

    }
}
