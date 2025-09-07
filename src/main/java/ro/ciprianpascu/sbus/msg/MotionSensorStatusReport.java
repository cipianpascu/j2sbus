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

package ro.ciprianpascu.sbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import ro.ciprianpascu.sbus.Sbus;
import ro.ciprianpascu.sbus.procimg.ByteRegister;
import ro.ciprianpascu.sbus.procimg.InputRegister;

/**
 * Class implementing a {@link MotionSensorStatusReport}.
 * This is a broadcast message sent by 9-in-1/6-in-1/5-in-1 devices
 * to report status changes. It encapsulates the corresponding message
 * for OpCode 0x02CA. The message is sent 3 times at 1-second intervals
 * to avoid loss and is broadcast to FF:FF.
 *
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public final class MotionSensorStatusReport extends SbusResponse {

    // instance attributes
    private int m_ByteCount;
    private InputRegister[] m_Registers;

    /**
     * Constructs a new {@link MotionSensorStatusReport}
     * instance. Reader focus
     */
    public MotionSensorStatusReport() {
        super();
        setFunctionCode(Sbus.MOTION_SENSOR_STATUS_REPORT);
    }// constructor

    /**
     * Constructs a new {@link MotionSensorStatusReport}
     * instance. Writer focus
     *
     * @param registers the InputRegister[] holding sensor status data (8 bytes).
     */
    public MotionSensorStatusReport(InputRegister[] registers) {
        super();
        setFunctionCode(Sbus.MOTION_SENSOR_STATUS_REPORT);
        m_ByteCount = registers.length;
        m_Registers = registers;
        // set correct data length excluding unit id and fc
        setDataLength(m_ByteCount + 1);
    }// constructor

    /**
     * Returns the number of bytes that have been read.
     *
     * @return the number of bytes that have been read as {@link int}.
     */
    public int getByteCount() {
        return m_ByteCount;
    }// getByteCount

    /**
     * Sets the number of bytes that have been returned.
     *
     * @param count the number of bytes as {@link int}.
     */
    private void setByteCount(int count) {
        m_ByteCount = count;
    }// setByteCount

    /**
     * Returns the number of dry contacts.
     *
     * @return number of dry contacts as {@link int} (9-in-1 has 2).
     */
    public int getNumberOfDryContacts() {
        return m_Registers[0].toUnsignedShort();
    }// getNumberOfDryContacts

    /**
     * Returns the type of dry contact 1.
     *
     * @param contactNumber
     * @return type of dry contact 1 as {@link int} (NC=1, NO=0).
     */
    public int getDryContactType(int contactNumber) {
        return m_Registers[contactNumber].toUnsignedShort();
    }// getDryContact1Type

    /**
     * Returns the status of dry contact 1.
     *
     * @param contactNumber
     * @return status of dry contact 1 as {@link int} (Open=1, Close=0).
     */
    public int getDryContactStatus(int contactNumber) {
        return m_Registers[getNumberOfDryContacts() + contactNumber].toUnsignedShort();
    }// getDryContact1Status

    /**
     * Returns the motion status.
     *
     * @return motion status as {@link int} (1=motion, 0=no motion).
     */
    public int getMotionStatus() {
        return m_Registers[getNumberOfDryContacts() + 1].toUnsignedShort();
    }// getMotionStatus

    /**
     * Returns the LUX value as a 2-byte value.
     *
     * @return LUX value as {@link int} (combined from bytes 6-7).
     */
    public int getLuxValue() {
        int offset = getNumberOfDryContacts() + 2;
        // Combine bytes 6 and 7 to form the LUX value
        int luxHigh = m_Registers[offset].toUnsignedShort();
        int luxLow = m_Registers[offset+1].toUnsignedShort();
        return (luxHigh << 8) | luxLow;
    }// getLuxValue

    /**
     * Returns the {@link InputRegister} at
     * the given position (relative to the reference
     * used in the request).
     *
     * @param index the relative index of the {@link InputRegister}.
     * @return the register as {@link InputRegister}.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    public InputRegister getRegister(int index) throws IndexOutOfBoundsException {
        if (index >= getByteCount()) {
            throw new IndexOutOfBoundsException();
        } else {
            return m_Registers[index];
        }
    }// getRegister

    /**
     * Returns the value of the register at
     * the given position (relative to the reference
     * used in the request) interpreted as unsigned
     * short.
     *
     * @param index the relative index of the register
     *            for which the value should be retrieved.
     * @return the value as {@link int}.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    public int getRegisterValue(int index) throws IndexOutOfBoundsException {
        if (index >= getByteCount()) {
            throw new IndexOutOfBoundsException();
        } else {
            return m_Registers[index].toUnsignedShort();
        }
    }// getRegisterValue

    /**
     * Returns a reference to the array of input
     * registers read.
     *
     * @return a {@link InputRegister[]} instance.
     */
    public InputRegister[] getRegisters() {
        return m_Registers;
    }// getRegisters

    @Override
    public void writeData(DataOutput dout) throws IOException {
        dout.writeByte(m_ByteCount + 1);
        for (int k = 0; k < getByteCount(); k++) {
            dout.write(m_Registers[k].getValue());
        }
    }// writeData

    @Override
    public void readData(DataInput din) throws IOException {
        setByteCount(din.readUnsignedByte());

        InputRegister[] registers = new InputRegister[getByteCount()];
        for (int k = 0; k < getByteCount(); k++) {
            registers[k] = new ByteRegister(din.readByte());
        }
        m_Registers = registers;
        // update data length
        setDataLength(getByteCount() + 1);
    }// readData

}// class MotionSensorStatusReport
