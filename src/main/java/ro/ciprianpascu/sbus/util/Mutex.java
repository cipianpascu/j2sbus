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

package ro.ciprianpascu.sbus.util;

/**
 * A simple non-reentrant mutual exclusion lock.
 * The lock is free upon construction. Each acquire gets the
 * lock, and each release frees it. Releasing a lock that
 * is already free has no effect.
 * 
 * This implementation makes no attempt to provide any fairness
 * or ordering guarantees. If you need them, consider using one of
 * the Semaphore implementations as a locking mechanism.
 * 
 * <b>Sample usage</b><br>
 * 
 * Mutex can be useful in constructions that cannot be
 * expressed using java synchronized blocks because the
 * acquire/release pairs do not occur in the same method or
 * code block. For example, you can use them for hand-over-hand
 * locking across the nodes of a linked list. This allows
 * extremely fine-grained locking, and so increases
 * potential concurrency, at the cost of additional complexity and
 * overhead that would normally make this worthwhile only in cases of
 * extreme contention.
 *
 * <pre>
 * class Node {
 *     Object item;
 *     Node next;
 *     Mutex lock = new Mutex(); // each node keeps its own lock
 *     
 *     Node(Object x, Node n) { item = x; next = n; }
 * }
 * 
 * class List {
 *     protected Node head; // pointer to first node of list
 *     
 *     // Use plain java synchronization to protect head field.
 *     protected synchronized Node getHead() { return head; }
 *     
 *     boolean search(Object x) throws InterruptedException {
 *         Node p = getHead();
 *         if (p == null) return false;
 *         
 *         p.lock.acquire(); // Prime loop by acquiring first lock.
 *         for (;;) {
 *             if (x.equals(p.item)) {
 *                 p.lock.release();
 *                 return true;
 *             } else {
 *                 Node nextp = p.next;
 *                 if (nextp == null) {
 *                     p.lock.release();
 *                     return false;
 *                 } else {
 *                     try {
 *                         nextp.lock.acquire();
 *                     } catch (InterruptedException ex) {
 *                         p.lock.release();
 *                         throw ex;
 *                     }
 *                     p.lock.release();
 *                     p = nextp;
 *                 }
 *             }
 *         }
 *     }
 *     
 *     synchronized void add(Object x) {
 *         head = new Node(x, head);
 *     }
 * }
 * </pre>
 *
 * @author Doug Lea
 * @version %I% (%G%)
 */
public class Mutex {

    /**
     * The lock status. True if the lock is in use, false if it's free.
     */
    protected boolean inuse_ = false;

    /**
     * Acquires the lock. If the lock is already in use, waits until it becomes available.
     * The method will wait indefinitely unless interrupted.
     *
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    public void acquire() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        synchronized (this) {
            try {
                while (inuse_) {
                    wait();
                }
                inuse_ = true;
            } catch (InterruptedException ex) {
                notify();
                throw ex;
            }
        }
    }

    /**
     * Releases the lock. If any other threads are waiting to acquire the lock,
     * one of them will be notified and allowed to proceed.
     * Releasing an already free lock has no effect.
     */
    public synchronized void release() {
        inuse_ = false;
        notify();
    }

    /**
     * Attempts to acquire the lock, waiting up to a specified time if necessary.
     * If the lock is already in use, waits for the specified time for it to
     * become available.
     *
     * @param msecs maximum time to wait in milliseconds
     * @return true if the lock was acquired, false if the timeout elapsed
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    public boolean attempt(long msecs) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        synchronized (this) {
            if (!inuse_) {
                inuse_ = true;
                return true;
            } else if (msecs <= 0) {
                return false;
            } else {
                long waitTime = msecs;
                long start = System.currentTimeMillis();
                try {
                    for (;;) {
                        wait(waitTime);
                        if (!inuse_) {
                            inuse_ = true;
                            return true;
                        } else {
                            waitTime = msecs - (System.currentTimeMillis() - start);
                            if (waitTime <= 0) {
                                return false;
                            }
                        }
                    }
                } catch (InterruptedException ex) {
                    notify();
                    throw ex;
                }
            }
        }
    }
}
