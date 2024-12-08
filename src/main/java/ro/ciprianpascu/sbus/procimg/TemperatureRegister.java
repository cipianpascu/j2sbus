
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
 * Class implementing a simple {@link Register}.
* 
 * The {@link Register#setValue(int)} method is synchronized,
 * which ensures atomic access, but no specific access order.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu

 * @version %I% (%G%)
 */
public class TemperatureRegister  implements Register {

    /**
     * The byte[4] holding the state of this
     * register.
     */
    protected byte[] m_Register = new byte[2];


    /**
     * Constructs a new {@link SimpleRegister} instance.
     *
     * @param b1 the first (hi) byte of the word.
     */
    public TemperatureRegister(byte sign, byte value) {
        m_Register[0] = sign;
		m_Register[1] = value;
    }// constructor


	@Override
	public int getValue() {
		return m_Register[0] == 0 ? m_Register[1] : -1 * m_Register[1];
	}


	@Override
	public int toUnsignedShort() {
		return m_Register[1];
	}


	@Override
	public short toShort() {
		return (short) (m_Register[0] == 0 ? m_Register[1] : -m_Register[1]);
	}


	@Override
	public byte[] toBytes() {
		return m_Register;
	}


	@Override
	public void setValue(int v) {
		setValue((short) v);
	}


	@Override
	public void setValue(short s) {
		m_Register[0] = s < 0 ? (byte) 1 : (byte) 0;	
		m_Register[1] = (byte) (0xff & s);	
	}


	@Override
	public void setValue(byte[] bytes) {
        if (bytes.length != 2) 
            throw new IllegalArgumentException();

		m_Register = bytes;		
	}// constructor


}// SimpleInputRegister
