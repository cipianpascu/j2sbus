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
import ro.ciprianpascu.sbus.procimg.InputRegister;
import ro.ciprianpascu.sbus.procimg.WordRegister;

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
public final class ReadTemperatureResponse extends SbusResponse {

    // instance attributes
    private int m_ByteCount;
    private int m_TemperatureUnit;
    // private int[] m_RegisterValues;
    private InputRegister[] m_Registers;

    /**
     * Constructs a new {@link ReadTemperatureResponse}
     * instance. Reader focus
     */
    public ReadTemperatureResponse() {
        super();
        setFunctionCode(Sbus.READ_TEMPERATURE_REQUEST + 1);
    }// constructor

    /**
     * Constructs a new {@link ReadTemperatureResponse}
     * instance. Writer focus
     *
     * @param registers the InputRegister[] holding response input registers.
     */
    public ReadTemperatureResponse(InputRegister[] registers) {
        super();
        setFunctionCode(Sbus.READ_TEMPERATURE_REQUEST + 1);
        m_ByteCount = registers.length * 2;
        m_Registers = registers;
        // set correct data length excluding unit id and fc
        setDataLength(m_ByteCount + 2);
    }// constructor

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
     * Returns the number of words that have been read.
     * The returned value should be twice as much as
     * the byte count of the response.
     *
     *
     * @return the number of words that have been read
     *         as {@link int}.
     */
    public int getWordCount() {
        return m_ByteCount / 2;
    }// getWordCount

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

        if (index >= getWordCount()) {
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

        if (index >= getWordCount()) {
            throw new IndexOutOfBoundsException();
        } else {
            return m_Registers[index].getValue();
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

    /**
     * Sets the temperature unit
     * from with this {@link ReadTemperatureRequest}.
     *
     *
     * @param unit the temperature unit 0 Fahrenheit, 1 Celsius
     */
    public void setTemperatureUnit(int unit) {
        m_TemperatureUnit = unit;
        // setChanged(true);
    }// setReference

    /**
     * Returns the temperature unit from this
     * {@link ReadTemperatureRequest}.
     *
     *
     * @return the temperature unit 0 Fahrenheit, 1 Celsius
     */
    public int getTemperatureUnit() {
        return m_TemperatureUnit;
    }// getReference

    @Override
    public void writeData(DataOutput dout) throws IOException {
        dout.writeByte(m_TemperatureUnit);
        // value
        for (int k = 0; k < getWordCount(); k++) {
            dout.write(m_Registers[k].toBytes()[1]);
        }
        // sign
        for (int k = 0; k < getWordCount(); k++) {
            dout.write(m_Registers[k].toBytes()[0]);
        }
    }// writeData

    @Override
    public void readData(DataInput din) throws IOException {
        setByteCount(8 * 2); // value & sign
        setTemperatureUnit(din.readUnsignedByte());

        // read value
        byte[] data = new byte[getWordCount()];
        din.readFully(data);

        InputRegister[] registers = new InputRegister[getWordCount()];
        // read sign and add previous value
        for (int k = 0; k < getWordCount(); k++) {
            registers[k] = new WordRegister(din.readByte(), data[k]);
        }
        m_Registers = registers;
        // update data length
        setDataLength(getByteCount() + 2);
    }// readData

}// class ReadStatusChannelsResponse
