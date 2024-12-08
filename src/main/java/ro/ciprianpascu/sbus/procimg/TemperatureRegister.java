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
 * Class implementing a temperature {@link Register}.
 * This register stores temperature values with a sign byte and a value byte.
 * The {@link Register#setValue(int)} method is synchronized,
 * which ensures atomic access, but no specific access order.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public class TemperatureRegister implements Register {

    /**
     * The byte array holding the state of this register.
     * byte[0] represents the sign (0 for positive, 1 for negative)
     * byte[1] represents the absolute value
     */
    protected byte[] m_Register = new byte[2];

    /**
     * Constructs a new {@link TemperatureRegister} instance.
     *
     * @param sign the sign byte (0 for positive, 1 for negative)
     * @param value the absolute value byte
     */
    public TemperatureRegister(byte sign, byte value) {
        m_Register[0] = sign;
        m_Register[1] = value;
    }

    /**
     * Returns the temperature value, taking the sign into account.
     *
     * @return the signed temperature value
     */
    @Override
    public int getValue() {
        return m_Register[0] == 0 ? m_Register[1] : -1 * m_Register[1];
    }

    /**
     * Returns the absolute value as an unsigned short.
     *
     * @return the unsigned value
     */
    @Override
    public int toUnsignedShort() {
        return m_Register[1];
    }

    /**
     * Returns the temperature value as a signed short.
     *
     * @return the signed temperature value
     */
    @Override
    public short toShort() {
        return (short) (m_Register[0] == 0 ? m_Register[1] : -m_Register[1]);
    }

    /**
     * Returns the raw byte array containing the sign and value.
     *
     * @return byte array containing [sign, value]
     */
    @Override
    public byte[] toBytes() {
        return m_Register;
    }

    /**
     * Sets the temperature value from an integer.
     * The sign will be extracted automatically.
     *
     * @param v the temperature value to set
     */
    @Override
    public void setValue(int v) {
        setValue((short) v);
    }

    /**
     * Sets the temperature value from a short.
     * The sign will be extracted automatically.
     *
     * @param s the temperature value to set
     */
    @Override
    public void setValue(short s) {
        m_Register[0] = s < 0 ? (byte) 1 : (byte) 0;    
        m_Register[1] = (byte) (0xff & s);    
    }

    /**
     * Sets the register value from a byte array.
     * The array must contain exactly 2 bytes: [sign, value]
     *
     * @param bytes byte array containing [sign, value]
     * @throws IllegalArgumentException if bytes.length != 2
     */
    @Override
    public void setValue(byte[] bytes) {
        if (bytes.length != 2) 
            throw new IllegalArgumentException("Byte array must have length 2");

        m_Register = bytes;        
    }

}
