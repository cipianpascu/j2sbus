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

package ro.ciprianpascu.sbus;

/**
 * Superclass of all specialised exceptions in
 * this package.
 *
 * @author Dieter Wimberger
 * @version %I% (%G%)
 */
public class ModbusException extends Exception {

    /**
     * Constructs a new {@link ModbusException}
     * instance.
     */
    public ModbusException() {
        super();
    }// constructor

    /**
     * Constructs a new {@link ModbusException}
     * instance with the given message.
* 
     *
     * @param message the message describing this
     *            {@link ModbusException}.
     */
    public ModbusException(String message) {
        super(message);
    }// constructor

}// ModbusException
