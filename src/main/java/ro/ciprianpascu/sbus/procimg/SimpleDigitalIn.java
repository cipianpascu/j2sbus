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

package ro.ciprianpascu.sbus.procimg;

/**
 * Class implementing a simple {@link DigitalIn}.
* 
 * The set method is synchronized, which ensures atomic
 * access, but no specific access order.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu

 * @version %I% (%G%)
 */
public class SimpleDigitalIn implements DigitalIn {

    /**
     * Field for the digital in state.
     */
    protected boolean m_Set = false;

    /**
     * Constructs a new {@link SimpleDigitalIn} instance.
     */
    public SimpleDigitalIn() {
    }// constructor

    /**
     * Constructs a new {@link SimpleDigitalIn} instance
     * with a given valid state.
     *
     * @param b true if to be set, false otherwise.
     */
    public SimpleDigitalIn(boolean b) {
        set(b);
    }// constructor(boolean)

    @Override
    public boolean isSet() {
        return m_Set;
    }// isSet

    /**
     * Sets the state of this {@link SimpleDigitalIn}.
     * This method should only be used from master/device
     * side.
     *
     * @param b true if to be set, false otherwise.
     */
    public synchronized void set(boolean b) {
        m_Set = b;
    }// set

}// SimpleDigitalIn
