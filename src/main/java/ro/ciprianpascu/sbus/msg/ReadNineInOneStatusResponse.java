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
 * Class implementing a {@link ReadNineInOneStatusResponse}.
 * The implementation reads status from 9-in-1 sensor device including
 * dry contacts, motion status, and LUX value. It encapsulates the 
 * corresponding response message for OpCode 0xDB01.
 *
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public final class ReadNineInOneStatusResponse extends SbusResponse {

    // instance attributes
    private int m_ByteCount;
    private InputRegister[] m_Registers;

    /**
     * Constructs a new {@link ReadNineInOneStatusResponse}
     * instance. Reader focus
     */
    public ReadNineInOneStatusResponse() {
        super();
        setFunctionCode(Sbus.READ_NINE_IN_ONE_STATUS_REQUEST + 1);
    }// constructor

    /**
     * Constructs a new {@link ReadNineInOneStatusResponse}
     * instance. Writer focus
     *
     * @param registers the InputRegister[] holding response sensor data (8 bytes).
     */
    public ReadNineInOneStatusResponse(InputRegister[] registers) {
        super();
        setFunctionCode(Sbus.READ_NINE_IN_ONE_STATUS_REQUEST + 1);
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
     * Returns the dry contact #1 status.
     *
     * @return dry contact #1 status as {@link int}.
     */
    public int getDryContact1Status() {
        return m_Registers[0].toUnsignedShort();
    }// getDryContact1Status

    /**
     * Returns the dry contact #2 status.
     *
     * @return dry contact #2 status as {@link int}.
     */
    public int getDryContact2Status() {
        return m_Registers[1].toUnsignedShort();
    }// getDryContact2Status

    /**
     * Returns the LUX value.
     *
     * @return LUX value as {@link int}.
     */
    public int getLuxValue() {
        return m_Registers[2].toUnsignedShort();
    }// getLuxValue

    /**
     * Returns the motion status.
     *
     * @return motion status as {@link int} (0=OK/no motion, 1=movement).
     */
    public int getMotionStatus() {
        return m_Registers[3].toUnsignedShort();
    }// getMotionStatus

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

}// class ReadNineInOneStatusResponse
