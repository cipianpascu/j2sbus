
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
public class WordRegister  implements Register {

    /**
     * The byte[4] holding the state of this
     * register.
     */
    protected byte[] m_Register = new byte[2];


    /**
     * Constructs a new {@link WordRegister} instance.
     *
     * @param value the (hi&lo) byte of the word.
     */
    public WordRegister(short value) {
        m_Register[0] = (byte) (value >> 8);
		m_Register[1] = (byte) (value & 0xff);
    }// constructor


	@Override
	public int getValue() {
        return (short) ((m_Register[0] << 8) | (m_Register[1] & 0xff));
	}


	@Override
	public int toUnsignedShort() {
        return (short) ((m_Register[0] << 8) | (m_Register[1] & 0xff));
	}


	@Override
	public short toShort() {
        return (short) ((m_Register[0] << 8) | (m_Register[1] & 0xff));
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
		m_Register[0] = (byte) (s >> 8);	
		m_Register[1] = (byte) (s & 0xff);	
	}


	@Override
	public void setValue(byte[] bytes) {
        if (bytes.length != 2) 
            throw new IllegalArgumentException();

		m_Register = bytes;		
	}// constructor


}// SimpleInputRegister
