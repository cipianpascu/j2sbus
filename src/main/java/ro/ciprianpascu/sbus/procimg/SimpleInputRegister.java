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
 * Class implementing a simple {@link InputRegister}.
* 
 * The {@link SynchronizedAbstractRegister#setValue(int)} method is synchronized,
 * which ensures atomic access, but no specific access order.
 *
 * @author Dieter Wimberger
 * @version %I% (%G%)
 */
public class SimpleInputRegister extends SynchronizedAbstractRegister implements InputRegister {

    /**
     * Constructs a new {@link SimpleInputRegister} instance.
     * Its state will be invalid.
     */
    public SimpleInputRegister() {
    }// constructor

    /**
     * Constructs a new {@link SimpleInputRegister} instance.
     *
     * @param b1 the first (hi) byte of the word.
     * @param b2 the second (low) byte of the word.
     */
    public SimpleInputRegister(byte b1, byte b2) {
        m_Register[0] = b1;
        m_Register[1] = b2;
    }// constructor

    /**
     * Constructs a new {@link SimpleInputRegister} instance
     * with the given value.
     *
     * @param value the value of this {@link SimpleInputRegister}
     *            as {@link int}.
     */
    public SimpleInputRegister(int value) {
        setValue(value);
    }// constructor(int)

}// SimpleInputRegister
