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
 * Class implementing a {@link WriteRgbwResponse}.
 * The implementation directly correlates with the class 0
 * function <i>write single register (FC 6)</i>. It
 * encapsulates the corresponding response message.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu

 * @version %I% (%G%)
 */
public final class WriteRgbwResponse extends SbusResponse {

	private static final int SUCCESS = 0xF8;
	private static final int FAILURE = 0xF5;
	
    // instance attributes
    private boolean m_StatusValue;

    /**
     * Constructs a new {@link WriteRgbwResponse}
     * instance.
     */
    public WriteRgbwResponse() {
        super();
		setFunctionCode(Sbus.WRITE_RGBW_REQUEST+1);
        setDataLength(1);
    }// constructor

    /**
     * Constructs a new {@link WriteRgbwResponse}
     * instance.
     *
     * @param success notify success/failure of the write operation to the register.
     */
    public WriteRgbwResponse( boolean success) {
        super();
		setFunctionCode(Sbus.WRITE_RGBW_REQUEST+1);
        setStatusValue(success);
        setDataLength(1);
    }// constructor

    /**
     * Returns the value that has been returned in
     * this {@link WriteRgbwResponse}.
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


    @Override
    public void writeData(DataOutput dout) throws IOException {
        dout.writeByte(m_StatusValue ? SUCCESS : FAILURE);
    }// writeData

    @Override
    public void readData(DataInput din) throws IOException {
        if(SUCCESS == din.readByte()) {
	        setStatusValue(true);
		} else {
			setStatusValue(false);
        }
        // update data length
        setDataLength(1);
    }// readData

	@Override
	public String toString() {
		return "WriteSingleChannelResponse: " + getStatusValue();
	}

}// class WriteSingleChannelResponse
