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
public final class WriteSingleChannelResponse extends SbusResponse {

	
    // instance attributes
    private int m_ChannelNo;
    private boolean m_StatusValue;

    /**
     * Constructs a new {@link WriteSingleChannelResponse}
     * instance.
     */
    public WriteSingleChannelResponse() {
        super();
		setFunctionCode(Sbus.WRITE_SINGLE_CHANNEL_REQUEST+1);
        setDataLength(2);
    }// constructor

    /**
     * Constructs a new {@link WriteSingleChannelResponse}
     * instance.
     *
     * @param channelNo the offset of the register written.
     * @param success notify success/failure of the write operation to the register.
     */
    public WriteSingleChannelResponse(int channelNo, boolean success) {
        super();
		setFunctionCode(Sbus.WRITE_SINGLE_CHANNEL_REQUEST+1);
        setChannelNo(channelNo);
        setStatusValue(success);
        setDataLength(2);
    }// constructor

    /**
     * Returns the value that has been returned in
     * this {@link WriteSingleChannelResponse}.
* 
     *
     * @return the value of the register.
     */
    public boolean getStatusValue() {
        return m_StatusValue;
    }// getValue

    /**
     * Sets the value that has been returned in the
     * response message.
* 
     *
     * @param value the returned register value.
     */
    private void setStatusValue(boolean value) {
        m_StatusValue = value;
    }// setStatusValue

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
    }// setReference

    @Override
    public void writeData(DataOutput dout) throws IOException {
        dout.writeByte(m_ChannelNo);
        dout.writeByte(m_StatusValue ? Sbus.SUCCESS : Sbus.FAILURE);
    }// writeData

    @Override
    public void readData(DataInput din) throws IOException {
        setChannelNo(din.readByte());
        if(Sbus.SUCCESS == din.readByte()) {
	        setStatusValue(true);
		} else {
			setStatusValue(false);
        }
        // update data length
        setDataLength(2);
    }// readData

	@Override
	public String toString() {
		return "WriteSingleChannelResponse: " + getChannelNo() + " " + getStatusValue();
	}

}// class WriteSingleChannelResponse
