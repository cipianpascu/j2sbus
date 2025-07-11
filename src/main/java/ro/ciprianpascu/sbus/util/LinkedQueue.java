/**
 * Copyright 2002-2010 jamod development team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***/

/**
 *
 * Copied with style from
 * Lea, Doug: "Concurrent Programming in Java: Design Principles and Patterns",
 * Second Edition, Addison-Wesley, ISBN 0-201-31009-0, November 1999
 *
 *
 */
package ro.ciprianpascu.sbus.util;

/**
 * A thread-safe linked queue implementation that supports concurrent access.
 * This class provides a FIFO (first-in-first-out) queue with synchronized methods
 * for adding and removing elements, with support for blocking operations.
 *
 * @author Jamod developers
 *
 */
public class LinkedQueue {

    /**
     * Dummy header node of list. The first actual node, if it exists, is always
     * at m_Head.m_NextNode. After each take, the old first node becomes the head.
     **/
    protected LinkedNode m_Head;

    /**
     * Helper monitor for managing access to last node.
     **/
    protected final Object m_PutLock = new Object();

    /**
     * The last node of list. Put() appends to list, so modifies m_Tail_
     **/
    protected LinkedNode m_Tail;

    /**
     * The number of threads waiting for a take.
     * Notifications are provided in put only if greater than zero.
     * The bookkeeping is worth it here since in reasonably balanced
     * usages, the notifications will hardly ever be necessary, so
     * the call overhead to notify can be eliminated.
     **/
    protected int m_WaitingForTake = 0;

    /**
     * LinkedQueue constructor
     */
    public LinkedQueue() {
        m_Head = new LinkedNode(null);
        m_Tail = m_Head;
    }// constructor

    /**
     * Inserts a new element at the end of the queue.
     * This method is thread-safe and uses synchronization to ensure the integrity of the queue's state.
     *
     * @param x the element to insert into the queue
     */
    protected void insert(Object x) {
        synchronized (m_PutLock) {
            LinkedNode p = new LinkedNode(x);
            synchronized (m_Tail) {
                m_Tail.m_NextNode = p;
                m_Tail = p;
            }
            if (m_WaitingForTake > 0) {
                m_PutLock.notify();
            }
        }
    }// insert

    /**
     * Removes and returns the element at the front of the queue.
     * This method is synchronized and thread-safe.
     *
     * @return the next object in the queue, or null if the queue is empty
     */
    protected synchronized Object extract() {
        synchronized (m_Head) {
            Object x = null;
            LinkedNode first = m_Head.m_NextNode;
            if (first != null) {
                x = first.m_Node;
                first.m_Node = null;
                m_Head = first;
            }
            return x;
        }
    }// extract

    /**
     * Inserts the specified element at the tail of this queue.
     *
     * @param x the element to add
     * @throws InterruptedException if interrupted while waiting
     * @throws IllegalArgumentException if the specified element is null
     */
    public void put(Object x) throws InterruptedException {
        if (x == null) {
            throw new IllegalArgumentException();
        }
        // @commentstart@if (Thread.interrupted()) throw new InterruptedException();//@commentend@
        insert(x);
    }// put

    /**
     * Inserts the specified element at the tail of this queue, waiting up to the specified wait time for space to become available.
     *
     * @param x the element to add
     * @param msecs the maximum time to wait in milliseconds
     * @return true if the element was added to this queue, else false
     * @throws InterruptedException if interrupted while waiting
     * @throws IllegalArgumentException if the specified element is null
     */
    public boolean offer(Object x, long msecs) throws InterruptedException {
        if (x == null) {
            throw new IllegalArgumentException();
        }
        // @commentstart@
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        // @commentend@
        insert(x);
        return true;
    }// offer

    /**
     * Retrieves and removes the head of this queue, waiting if necessary until an element becomes available.
     *
     * @return the head of this queue
     * @throws InterruptedException if interrupted while waiting
     */
    public Object take() throws InterruptedException {
        // @commentstart@if (Thread.interrupted()) throw new InterruptedException();//@commentend@
        // try to extract. If fail, then enter wait-based retry loop
        Object x = extract();
        if (x != null) {
            return x;
        } else {
            synchronized (m_PutLock) {
                try {
                    ++m_WaitingForTake;
                    for (;;) {
                        x = extract();
                        if (x != null) {
                            --m_WaitingForTake;
                            return x;
                        } else {
                            m_PutLock.wait();
                        }
                    }
                } catch (InterruptedException ex) {
                    --m_WaitingForTake;
                    m_PutLock.notify();
                    throw ex;
                }
            }
        }
    }// take

    /**
     * Retrieves, but does not remove, the head of this queue, or returns null if this queue is empty.
     *
     * @return the head of this queue, or null if this queue is empty
     */
    public Object peek() {
        synchronized (m_Head) {
            LinkedNode first = m_Head.m_NextNode;
            if (first != null) {
                return first.m_Node;
            } else {
                return null;
            }
        }
    }// peek

    /**
     * Returns true if this queue contains no elements.
     *
     * @return true if this queue is empty, else false
     */
    public boolean isEmpty() {
        synchronized (m_Head) {
            return m_Head.m_NextNode == null;
        }
    }// isEmpty

    /**
     * Retrieves and removes the head of this queue, waiting up to the specified wait time if necessary for an element to become available.
     *
     * @param msecs the maximum time to wait in milliseconds
     * @return the head of this queue, or null if the specified waiting time elapses before an element is available
     * @throws InterruptedException if interrupted while waiting
     */
    public Object poll(long msecs) throws InterruptedException {
        // @commentstart@if (Thread.interrupted()) throw new InterruptedException();//@commentend@
        Object x = extract();
        if (x != null) {
            return x;
        } else {
            synchronized (m_PutLock) {
                try {
                    long waitTime = msecs;
                    long start = (msecs <= 0) ? 0 : System.currentTimeMillis();
                    ++m_WaitingForTake;
                    for (;;) {
                        x = extract();
                        if (x != null || waitTime <= 0) {
                            --m_WaitingForTake;
                            return x;
                        } else {
                            m_PutLock.wait(waitTime);
                            waitTime = msecs - (System.currentTimeMillis() - start);
                        }
                    }
                } catch (InterruptedException ex) {
                    --m_WaitingForTake;
                    m_PutLock.notify();
                    throw ex;
                }
            }
        }
    }// pollInterval

}// LinkedQueue
