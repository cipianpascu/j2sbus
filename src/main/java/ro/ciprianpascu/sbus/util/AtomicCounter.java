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

import java.util.concurrent.atomic.AtomicInteger;

import ro.ciprianpascu.sbus.Modbus;

/**
 * Provides an atomic integer.
 * 
 *
 * @author Dieter Wimberger (wimpi)
 * @version %I% (%G%)
 */
public class AtomicCounter {

    private int m_Value;

    /**
     * Constructs a new {@link AtomicInteger}.
     */
    public AtomicCounter() {
        m_Value = 0;
    }// constructor

    /**
     * Constructs a new {@link AtomicInteger}
     * with a given initial value.
     *
     * @param value the initial value.
     */
    public AtomicCounter(int value) {
        m_Value = value;
    }// constructor

    /**
     * Increments this {@link AtomicInteger} by one.
     *
     * @return the resulting value.
     */
    public synchronized int increment() {
        if (m_Value == Modbus.MAX_TRANSACTION_ID) {
            m_Value = 0;
        }
        return ++m_Value;
    }// increment

    /**
     * Returns the value of this {@link AtomicInteger}.
     *
     * @return the actual value.
     */
    public synchronized int get() {
        return m_Value;
    }// get

}// class AtomicCounter
