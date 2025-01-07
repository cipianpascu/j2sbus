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
 * Class implementing a {@link ReadStatusChannelsRequest}.
 * The implementation directly correlates with the class 0
 * function <i>read multiple registers (FC 4)</i>. It
 * encapsulates the corresponding response message.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu
 *
 * @version %I% (%G%)
 */
public final class ReadRgbwResponse extends SbusResponse {

    // instance attributes
    private int m_LoopNumber;
    private boolean m_StatusValue;
    private int m_ByteCount;
    // private int[] m_RegisterValues;
    private InputRegister[] m_Registers;

    /**
     * Constructs a new {@link ReadRgbwResponse}
     * instance. Reader focus
     */
    public ReadRgbwResponse() {
        super();
        setFunctionCode(Sbus.READ_CUSTOM_COLORS_REQUEST + 1);
        setDataLength(1);
    }// constructor

    /**
     * Constructs a new {@link ReadRgbwResponse}
     * instance. Writer focus
     *
     * @param channelNo the offset of the register written.
     * @param success notify success/failure of the write operation to the register.
     * @param registers the InputRegister[] holding response input registers.
     */
    public ReadRgbwResponse(int channelNo, boolean success, InputRegister[] registers) {
        super();
        setFunctionCode(Sbus.READ_CUSTOM_COLORS_REQUEST + 1);
        setStatusValue(success);
        setLoopNumber(channelNo);
        m_ByteCount = registers.length;
        m_Registers = registers;
        // set correct data length excluding unit id and fc
        setDataLength(m_ByteCount + 2);
    }// constructor

    /**
     * Sets the loop number
     * from with this {@link ReadRgbwResponse}.
     *
     *
     * @param type the loop number type 0 Low, 1 High
     */
    public void setLoopNumber(int type) {
        m_LoopNumber = type;
        // setChanged(true);
    }// setReference

    /**
     * Returns the loop number from this
     * {@link ReadRgbwResponse}.
     *
     *
     * @return the loop number 0 Low, 1 High
     */
    public int getLoopNumber() {
        return m_LoopNumber;
    }// getReference

    /**
     * Returns the number of bytes that have been read.
     *
     *
     * @return the number of bytes that have been read
     *         as {@link int}.
     */
    public int getByteCount() {
        return m_ByteCount;
    }// getByteCount

    /**
     * Sets the number of bytes that have been returned.
     *
     *
     * @param count the number of bytes as {@link int}.
     */
    private void setByteCount(int count) {
        m_ByteCount = count;
    }// setByteCount

    /**
     * Returns the value that has been returned in
     * this {@link WriteSingleChannelResponse}.
     *
     *
     * @return the value of the register.
     */
    public boolean getStatusValue() {
        return m_StatusValue;
    }// getValue

    /**
     * Sets the value that has been returned in the
     * response message.
     *
     *
     * @param value the returned register value.
     */
    private void setStatusValue(boolean value) {
        m_StatusValue = value;
    }// setStatusValue

    /**
     * Returns the {@link InputRegister} at
     * the given position (relative to the reference
     * used in the request).
     *
     *
     * @param index the relative index of the {@link InputRegister}.
     * @return the register as {@link InputRegister}.
     * @throws IndexOutOfBoundsException if
     *             the index is out of bounds.
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
     * used in the request) interpreted as usigned
     * short.
     *
     *
     * @param index the relative index of the register
     *            for which the value should be retrieved.
     * @return the value as {@link int}.
     * @throws IndexOutOfBoundsException if
     *             the index is out of bounds.
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
        dout.writeByte(m_StatusValue ? Sbus.SUCCESS : Sbus.FAILURE);
        dout.writeByte(m_LoopNumber);
        for (int k = 0; k < getByteCount(); k++) {
            dout.write(m_Registers[k].getValue());
        }
    }// writeData

    @Override
    public void readData(DataInput din) throws IOException {
        if (Sbus.SUCCESS == din.readUnsignedByte()) {
            setStatusValue(true);
        } else {
            setStatusValue(false);
            return;
        }
        setLoopNumber(din.readByte());
        setByteCount(4);

        InputRegister[] registers = new InputRegister[getByteCount()];
        for (int k = 0; k < getByteCount(); k++) {
            registers[k] = new ByteRegister(din.readByte());
        }
        m_Registers = registers;
        // update data length
        setDataLength(getByteCount() + 2);
    }// readData

}// class ReadStatusChannelsResponse
