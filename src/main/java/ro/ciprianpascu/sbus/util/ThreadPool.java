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
 * Class implementing a simple thread pool.
 *
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public class ThreadPool {

    // instance attributes and associations
    private LinkedQueue m_TaskPool;
    private int m_Size = 1;

    /**
     * Constructs a new {@link ThreadPool} instance.
     *
     * @param size the size of the thread pool.
     */
    public ThreadPool(int size) {
        m_Size = size;
        m_TaskPool = new LinkedQueue();
        initPool();
    }// constructor

    /**
     * Execute the {@link Runnable} instance
     * through a thread in this {@link ThreadPool}.
     *
     * @param task the {@link Runnable} to be executed.
     */
    public synchronized void execute(Runnable task) {
        try {
            m_TaskPool.put(task);
        } catch (InterruptedException ex) {
            // FIXME: Handle!?
        }
    }// execute

    /**
     * Initializes the pool, populating it with
     * n started threads.
     */
    protected void initPool() {
        for (int i = m_Size; --i >= 0;) {
            new PoolThread().start();
        }
    }// initPool

    /**
     * Inner class implementing a thread that can be
     * run in a {@link ThreadPool}.
     *
     * @author Ciprian Pascu
     * @version %I% (%G%)
     */
    private class PoolThread extends Thread {

        /**
         * Runs the {@link PoolThread}.
* 
         * This method will infinitely loop, picking
         * up available tasks from the {@link LinkedQueue}.
         */
        @Override
        public void run() {
            // Running PoolThread
            do {
                try {
                    ((Runnable) m_TaskPool.take()).run();
                } catch (Exception ex) {
                    // FIXME: Handle somehow!?
                    ex.printStackTrace();
                }
            } while (true);
        }
    }// PoolThread

}// ThreadPool
