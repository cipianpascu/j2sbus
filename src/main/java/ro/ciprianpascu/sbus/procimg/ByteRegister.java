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
 * @author Ciprian Pascu

 * @version %I% (%G%)
 */
public class ByteRegister  implements Register {

    /**
     * The byte[1] holding the state of this
     * register.
     */
    protected byte[] m_Register = new byte[1];


    /**
     * Constructs a new {@link ByteRegister} instance.
     *
     * @param value 1byte,0-100. It represents a custom value
     */
    public ByteRegister(byte value) {
        m_Register[0] = value;
    }// constructor


	@Override
	public int getValue() {
		return m_Register[0];
	}


	@Override
	public int toUnsignedShort() {
		return m_Register[0];
	}


	@Override
	public short toShort() {
		return m_Register[0];
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
		m_Register[0] = (byte) (0xff & s);	
	}


	@Override
	public void setValue(byte[] bytes) {
        if (bytes.length != 1) 
            throw new IllegalArgumentException();

		m_Register[0] = bytes[0];		
	}


}// RelayRegister
