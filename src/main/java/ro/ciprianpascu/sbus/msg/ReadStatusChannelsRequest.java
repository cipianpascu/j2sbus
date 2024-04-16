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
import ro.ciprianpascu.sbus.ModbusCoupler;
import ro.ciprianpascu.sbus.procimg.IllegalAddressException;
import ro.ciprianpascu.sbus.procimg.InputRegister;
import ro.ciprianpascu.sbus.procimg.ProcessImage;

/**
 * Class implementing a {@link ReadStatusChannelsRequest}.
 * The implementation directly correlates with the class 0
 * function <i>read multiple registers (FC 4)</i>. It
 * encapsulates the corresponding request message.
 *
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public final class ReadStatusChannelsRequest extends ModbusRequest {

    // instance attributes
    private int m_Reference;
    private int m_WordCount;

    /**
     * Constructs a new {@link ReadStatusChannelsRequest}
     * instance.
     */
    public ReadStatusChannelsRequest() {
        super();
        setFunctionCode(Modbus.READ_STATUS_CHANNELS_REQUEST);
        setDataLength(0);
    }// constructor


    @Override
    public ModbusResponse createResponse() {
        ReadStatusChannelsResponse response = null;
        InputRegister[] inpregs = null;

        // 1. get process image
        ProcessImage procimg = ModbusCoupler.getReference().getProcessImage();
        // 2. get input registers range
        try {
            inpregs = procimg.getInputRegisterRange(this.getReference(), this.getWordCount());
        } catch (IllegalAddressException iaex) {
            return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION);
        }
        response = new ReadStatusChannelsResponse(inpregs);
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
     * Sets the reference of the register to start reading
     * from with this {@link ReadStatusChannelsRequest}.
* 
     *
     * @param ref the reference of the register
     *            to start reading from.
     */
    public void setReference(int ref) {
        m_Reference = ref;
        // setChanged(true);
    }// setReference

    /**
     * Returns the reference of the register to to start
     * reading from with this
     * {@link ReadStatusChannelsRequest}.
* 
     *
     * @return the reference of the register
     *         to start reading from as {@link int}.
     */
    public int getReference() {
        return m_Reference;
    }// getReference

    /**
     * Sets the number of words to be read with this
     * {@link ReadStatusChannelsRequest}.
* 
     *
     * @param count the number of words to be read.
     */
    public void setWordCount(int count) {
        m_WordCount = count;
        // setChanged(true);
    }// setWordCount

    /**
     * Returns the number of words to be read with this
     * {@link ReadStatusChannelsRequest}.
* 
     *
     * @return the number of words to be read as
     *         {@link int}.
     */
    public int getWordCount() {
        return m_WordCount;
    }// getWordCount

    @Override
    public void writeData(DataOutput dout) throws IOException {
//        dout.writeShort(m_Reference);
//        dout.writeShort(m_WordCount);
    }// writeData

    @Override
    public void readData(DataInput din) throws IOException {
//        m_Reference = din.readUnsignedShort();
//        m_WordCount = din.readUnsignedShort();
    }// readData

}// class ReadStatusChannelsRequest
