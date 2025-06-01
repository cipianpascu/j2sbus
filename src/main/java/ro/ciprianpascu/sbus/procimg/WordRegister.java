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
 * Class implementing a word register for the SBus protocol.
 * A word register holds a 16-bit value split into high and low bytes.
 * The setValue methods are synchronized to ensure atomic access,
 * though no specific access order is guaranteed.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public class WordRegister implements Register {

    /**
     * The byte array holding the state of this register.
     * byte[0] = high byte
     * byte[1] = low byte
     */
    protected byte[] m_Register = new byte[2];

    /**
     * Constructs a new WordRegister instance.
     *
     * @param value the initial value to set (high byte in bits 8-15, low byte in bits 0-7)
     */
    public WordRegister(short value) {
        m_Register[0] = (byte) (value >> 8);
        m_Register[1] = (byte) (value & 0xff);
    }

    /**
     * Constructs a new WordRegister instance.
     *
     * @param value the initial value to set (high byte in bits 8-15, low byte in bits 0-7)
     */
    public WordRegister(byte lowValue, byte highValue) {
        m_Register[0] = highValue;
        m_Register[1] = lowValue;
    }

    /**
     * Returns the register value as a signed integer.
     *
     * @return the register value as a signed integer
     */
    @Override
    public int getValue() {
        return (short) ((m_Register[0] << 8) | (m_Register[1] & 0xff));
    }

    /**
     * Returns the register value as an unsigned short.
     *
     * @return the register value as an unsigned short
     */
    @Override
    public int toUnsignedShort() {
        return (short) ((m_Register[0] << 8) | (m_Register[1] & 0xff));
    }

    /**
     * Returns the register value as a signed short.
     *
     * @return the register value as a signed short
     */
    @Override
    public short toShort() {
        return (short) ((m_Register[0] << 8) | (m_Register[1] & 0xff));
    }

    /**
     * Returns the raw byte array containing the register value.
     *
     * @return byte array containing [high byte, low byte]
     */
    @Override
    public byte[] toBytes() {
        return m_Register;
    }

    /**
     * Sets the register value from an integer.
     * The value will be truncated to 16 bits.
     *
     * @param v the value to set
     */
    @Override
    public void setValue(int v) {
        setValue((short) v);
    }

    /**
     * Sets the register value from a short.
     *
     * @param s the value to set
     */
    @Override
    public void setValue(short s) {
        m_Register[0] = (byte) (s >> 8);
        m_Register[1] = (byte) (s & 0xff);
    }

    /**
     * Sets the register value from a byte array.
     * The array must contain exactly 2 bytes: [high byte, low byte]
     *
     * @param bytes byte array containing [high byte, low byte]
     * @throws IllegalArgumentException if bytes.length != 2
     */
    @Override
    public void setValue(byte[] bytes) {
        if (bytes.length != 2) {
            throw new IllegalArgumentException("Byte array must have length 2");
        }

        m_Register = bytes;
    }
}
