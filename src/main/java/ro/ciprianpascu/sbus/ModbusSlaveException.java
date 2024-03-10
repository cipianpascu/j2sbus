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
 * Class that implements a {@link ModbusSlaveException}.
 * Instances of this exception are thrown when
 * the slave returns a Modbus exception.
 *
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public class ModbusSlaveException extends ModbusException {

    // instance attributes
    private int m_Type = -1;

    /**
     * Constructs a new {@link ModbusSlaveException}
     * instance with the given type.<br>
     * Types are defined according to the protocol
     * specification in {@link ro.ciprianpascu.sbus.Modbus}.
* 
     *
     * @param TYPE the type of exception that occured.
     *
     * @see ro.ciprianpascu.sbus.Modbus
     */
    public ModbusSlaveException(int TYPE) {
        super();
        m_Type = TYPE;
    }// constructor

    /**
     * Returns the type of this {@link ModbusSlaveException}.
     * <br>
     * Types are defined according to the protocol
     * specification in {@link ro.ciprianpascu.sbus.Modbus}.
* 
     *
     * @return the type of this {@link ModbusSlaveException}.
     *
     * @see ro.ciprianpascu.sbus.Modbus
     */
    public int getType() {
        return m_Type;
    }// getType

    /**
     * Tests if this {@link ModbusSlaveException}
     * is of a given type.
     * <br>
     * Types are defined according to the protocol
     * specification in {@link ro.ciprianpascu.sbus.Modbus}.
* 
     *
     * @param TYPE the type to test this
     *            {@link ModbusSlaveException} type against.
     *
     * @return true if this {@link ModbusSlaveException}
     *         is of the given type, false otherwise.
     *
     * @see ro.ciprianpascu.sbus.Modbus
     */
    public boolean isType(int TYPE) {
        return (TYPE == m_Type);
    }// isType

    @Override
    public String getMessage() {
        return "Error Code = " + m_Type;
    }// getMessage

}// ModbusSlaveException
