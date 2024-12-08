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
import ro.ciprianpascu.sbus.procimg.ByteRegister;
import ro.ciprianpascu.sbus.procimg.IllegalAddressException;
import ro.ciprianpascu.sbus.procimg.InputRegister;
import ro.ciprianpascu.sbus.procimg.ProcessImageImplementation;
import ro.ciprianpascu.sbus.procimg.Register;
import ro.ciprianpascu.sbus.procimg.WordRegister;

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
    private Register[] m_Registers;

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
    public WriteSingleChannelRequest(int channelNo, Register[] regs) {
        super();
        setFunctionCode(Modbus.WRITE_SINGLE_CHANNEL_REQUEST);
        m_channelNo = channelNo;
        m_Registers = regs;
        // 4 bytes (unit id and function code is excluded)
        setDataLength(4);
    }// constructor

    @Override
    public ModbusResponse createResponse(ProcessImageImplementation procimg) {
        WriteSingleChannelResponse response = null;
        boolean updateSuccessfull = false;

        // 1. get register
        try {
        	Register regValue = procimg.getRegister(m_channelNo+1);
        	Register regTimer = procimg.getRegister(m_channelNo*2+1);
            // 3. set Register
        	regValue.setValue(m_Registers[0].toBytes());
			regTimer.setValue(m_Registers[1].toBytes());
            updateSuccessfull = true;
        } catch (IllegalAddressException iaex) {
            return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION);
        }
        response = new WriteSingleChannelResponse(this.getChannelNo(), updateSuccessfull);
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

        if (index >= 2) {
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

        if (index >= 2) {
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
    
    /**
     * Sets the registers to be written with this
     * {@link WriteMultipleRegistersRequest}.
     * 
     *
     * @param registers the registers to be written
     *            as {@link Register[]}.
     */
    public void setRegisters(Register[] registers) {
        m_Registers = registers;
        setDataLength(4); // update message length in header
    }// setRegisters

    @Override
    public void writeData(DataOutput dout) throws IOException {
        dout.writeByte(m_channelNo);
        dout.write(m_Registers[0].toBytes());
        dout.write(m_Registers[1].toBytes());
    }// writeData

    @Override
    public void readData(DataInput din) throws IOException {
    	m_channelNo = din.readByte();
    	m_Registers = new Register[2];
		m_Registers[0] = new ByteRegister(din.readByte());
		m_Registers[1] = new WordRegister(din.readShort());

		setDataLength(4);
    }// readData

}// class WriteSingleChannelRequest
