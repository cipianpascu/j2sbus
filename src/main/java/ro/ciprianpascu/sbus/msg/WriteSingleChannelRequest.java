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
import ro.ciprianpascu.sbus.procimg.IllegalAddressException;
import ro.ciprianpascu.sbus.procimg.ProcessImageImplementation;
import ro.ciprianpascu.sbus.procimg.Register;
import ro.ciprianpascu.sbus.procimg.SimpleInputRegister;

/**
 * Class implementing a {@link WriteSingleChannelRequest}.
 * The implementation directly correlates with the class 0
 * function <i>write single register (FC 6)</i>. It
 * encapsulates the corresponding request message.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu

 * @version %I% (%G%)
 */
public final class WriteSingleChannelRequest extends ModbusRequest {

    // instance attributes
    private int m_channelNo;
    private Register m_Register;

    /**
     * Constructs a new {@link WriteSingleChannelRequest}
     * instance.
     */
    public WriteSingleChannelRequest() {
        super();
        setFunctionCode(Modbus.WRITE_SINGLE_CHANNEL_REQUEST);
        // 4 bytes (unit id and function code is excluded)
        setDataLength(4);
    }// constructor

    /**
     * Constructs a new {@link WriteSingleChannelRequest}
     * instance with a given reference and value to be written.
     * 
     *
     * @param channelNo the reference number of the register
     *            to write to.
     * @param reg the register containing the data to be written.
     */
    public WriteSingleChannelRequest(int channelNo, Register reg) {
        super();
        setFunctionCode(Modbus.WRITE_SINGLE_CHANNEL_REQUEST);
        m_channelNo = channelNo;
        m_Register = reg;
        // 4 bytes (unit id and function code is excluded)
        setDataLength(4);
    }// constructor

    @Override
    public ModbusResponse createResponse(ProcessImageImplementation procimg) {
        WriteSingleChannelResponse response = null;
        Register reg = null;

        // 1. get register
        try {
            reg = procimg.getRegister(m_channelNo);
            // 3. set Register
            reg.setValue(m_Register.toBytes());
        } catch (IllegalAddressException iaex) {
            return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION);
        }
        response = new WriteSingleChannelResponse(this.getChannelNo(), reg.getValue());
        // transfer header data
        response.setSourceSubnetID(this.getSourceSubnetID());
		response.setSourceUnitID(this.getSourceUnitID());
		response.setSourceDeviceType(this.getSourceDeviceType());
        response.setSubnetID(this.getSubnetID());
        response.setUnitID(this.getUnitID());
        response.setFunctionCode(this.getFunctionCode());
        return response;
    }// createResponse

    /**
     * Sets the reference of the register to be written
     * to with this {@link WriteSingleChannelRequest}.
     * 
     *
     * @param channelNo channelNo 
     *            to be written to.
     */
    public void setChannelNo(int channelNo) {
    	m_channelNo = channelNo;
        // setChanged(true);
    }// setReference

    /**
     * Returns the channel No. to be
     * written to with this
     * {@link WriteSingleChannelRequest}.
     * 
     *
     * @return the reference of the register
     *         to be written to.
     */
    public int getChannelNo() {
        return m_channelNo;
    }// getReference

    /**
     * Sets the value that should be written to the
     * register with this {@link WriteSingleChannelRequest}.
     * 
     *
     * @param reg the register to be written.
     */
    public void setRegister(Register reg) {
        m_Register = reg;
    }// setRegister

    /**
     * Returns the value that should be written to the
     * register with this {@link WriteSingleChannelRequest}.
     * 
     *
     * @return the value to be written to the register.
     */
    public Register getRegister() {
        return m_Register;
    }// getRegister

    @Override
    public void writeData(DataOutput dout) throws IOException {
        dout.writeShort(m_channelNo);
        dout.write(m_Register.toBytes(), 0, 2);
    }// writeData

    @Override
    public void readData(DataInput din) throws IOException {
    	m_channelNo = din.readUnsignedShort();
        m_Register = new SimpleInputRegister(din.readByte(), din.readByte());
    }// readData

}// class WriteSingleChannelRequest
