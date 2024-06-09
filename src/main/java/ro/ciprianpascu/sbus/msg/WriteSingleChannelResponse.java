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

import ro.ciprianpascu.sbus.Modbus;

/**
 * Class implementing a {@link WriteSingleChannelResponse}.
 * The implementation directly correlates with the class 0
 * function <i>write single register (FC 6)</i>. It
 * encapsulates the corresponding response message.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu

 * @version %I% (%G%)
 */
public final class WriteSingleChannelResponse extends ModbusResponse {

    // instance attributes
    private int m_ChannelNo;
    private int m_RegisterValue;

    /**
     * Constructs a new {@link WriteSingleChannelResponse}
     * instance.
     */
    public WriteSingleChannelResponse() {
        super();
		setFunctionCode(Modbus.WRITE_SINGLE_CHANNEL_REQUEST+1);
        setDataLength(2);
    }// constructor

    /**
     * Constructs a new {@link WriteSingleChannelResponse}
     * instance.
     *
     * @param channelNo the offset of the register written.
     * @param value the value of the register.
     */
    public WriteSingleChannelResponse(int channelNo, int value) {
        super();
		setFunctionCode(Modbus.WRITE_SINGLE_CHANNEL_REQUEST+1);
        setChannelNo(channelNo);
        setRegisterValue(value);
        setDataLength(2);
    }// constructor

    /**
     * Returns the value that has been returned in
     * this {@link WriteSingleChannelResponse}.
* 
     *
     * @return the value of the register.
     */
    public int getRegisterValue() {
        return m_RegisterValue;
    }// getValue

    /**
     * Sets the value that has been returned in the
     * response message.
* 
     *
     * @param value the returned register value.
     */
    private void setRegisterValue(int value) {
        m_RegisterValue = value;
    }// setRegisterValue

    /**
     * Returns the reference of the register
     * that has been written to.
* 
     *
     * @return the reference of the written register.
     */
    public int getChannelNo() {
        return m_ChannelNo;
    }// getReference

    /**
     * Sets the reference of the register that has
     * been written to.
* 
     *
     * @param ref the reference of the written register.
     */
    private void setChannelNo(int channelNo) {
    	m_ChannelNo = channelNo;
        // setChanged(true);
    }// setReference

    @Override
    public void writeData(DataOutput dout) throws IOException {
        dout.writeShort(m_ChannelNo);
        dout.writeShort(getRegisterValue());
    }// writeData

    @Override
    public void readData(DataInput din) throws IOException {
        setChannelNo(din.readUnsignedShort());
        setRegisterValue(din.readUnsignedShort());
        // update data length
        setDataLength(4);
    }// readData
    /*
     * protected void assembleData() throws IOException {
     * m_DataOut.writeShort(getReference());
     * m_DataOut.writeShort(getRegisterValue());
     * }//assembleData
     *
     * protected void readData(DataInputStream in)
     * throws EOFException, IOException {
     *
     * setReference(in.readUnsignedShort());
     * setRegisterValue(in.readUnsignedShort());
     * //update data length
     * setDataLength(4);
     * }//readData
     */

}// class WriteSingleChannelResponse
