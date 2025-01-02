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
 * Class that implements a {@link SbusSlaveException}.
 * Instances of this exception are thrown when
 * the slave returns a Sbus exception.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu

 * @version %I% (%G%)
 */
public class SbusSlaveException extends SbusException {

    // instance attributes
    private int m_Type = -1;

    /**
     * Constructs a new {@link SbusSlaveException}
     * instance with the given type.<br>
     * Types are defined according to the protocol
     * specification in {@link ro.ciprianpascu.sbus.Sbus}.
* 
     *
     * @param TYPE the type of exception that occured.
     *
     * @see ro.ciprianpascu.sbus.Sbus
     */
    public SbusSlaveException(int TYPE) {
        super();
        m_Type = TYPE;
    }// constructor

    /**
     * Returns the type of this {@link SbusSlaveException}.
     * <br>
     * Types are defined according to the protocol
     * specification in {@link ro.ciprianpascu.sbus.Sbus}.
* 
     *
     * @return the type of this {@link SbusSlaveException}.
     *
     * @see ro.ciprianpascu.sbus.Sbus
     */
    public int getType() {
        return m_Type;
    }// getType

    /**
     * Tests if this {@link SbusSlaveException}
     * is of a given type.
     * <br>
     * Types are defined according to the protocol
     * specification in {@link ro.ciprianpascu.sbus.Sbus}.
* 
     *
     * @param TYPE the type to test this
     *            {@link SbusSlaveException} type against.
     *
     * @return true if this {@link SbusSlaveException}
     *         is of the given type, false otherwise.
     *
     * @see ro.ciprianpascu.sbus.Sbus
     */
    public boolean isType(int TYPE) {
        return (TYPE == m_Type);
    }// isType

    @Override
    public String getMessage() {
        return "Error Code = " + m_Type;
    }// getMessage

}// SbusSlaveException
