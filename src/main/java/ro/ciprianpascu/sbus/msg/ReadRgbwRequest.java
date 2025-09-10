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
import ro.ciprianpascu.sbus.procimg.IllegalAddressException;
import ro.ciprianpascu.sbus.procimg.InputRegister;
import ro.ciprianpascu.sbus.procimg.ProcessImageImplementation;

/**
 * Class implementing a {@link ReadRgbwRequest}.
 * The implementation directly correlates with the class 0
 * function <i>read multiple registers (FC 4)</i>. It
 * encapsulates the corresponding request message.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu
 *
 * @version %I% (%G%)
 */
public final class ReadRgbwRequest extends SbusRequest {

    // instance attributes
    private int m_LoopNumber;

    /**
     * Constructs a new {@link ReadRgbwRequest}
     * instance.
     */
    public ReadRgbwRequest() {
        super();
        setFunctionCode(Sbus.READ_CUSTOM_COLORS_REQUEST);
        setLoopNumber(1);
        setDataLength(1);
    }// constructor

    @Override
    public SbusResponse createResponse(ProcessImageImplementation procimg) {
        // For a local/server emulation: 0xDD31 success + loop + 4 bytes
        InputRegister[] four = procimg.getInputRegisterRange(0, 3);
        ReadRgbwResponse resp = new ReadRgbwResponse(getLoopNumber(), true, four);
        resp.setSourceSubnetID(getSourceSubnetID());
        resp.setSourceUnitID(getSourceUnitID());
        resp.setSourceDeviceType(getSourceDeviceType());
        resp.setSubnetID(getSubnetID());
        resp.setUnitID(getUnitID());
        resp.setFunctionCode(getFunctionCode() + 1); // 0xDD31
        return resp;
    }// createResponse

    /**
     * Sets the limit
     * from with this {@link ReadRgbwRequest}.
     *
     *
     * @param type the loop number type 0 Low, 1 High
     */
    public void setLoopNumber(int type) {
        m_LoopNumber = type;
        // setChanged(true);
    }// setReference

    /**
     * Returns the loop number from this
     * {@link ReadRgbwRequest}.
     *
     *
     * @return the loop number 0 Low, 1 High
     */
    public int getLoopNumber() {
        return m_LoopNumber;
    }// getReference

    @Override
    public void writeData(DataOutput dout) throws IOException {
        dout.writeByte(m_LoopNumber);
    }// writeData

    @Override
    public void readData(DataInput din) throws IOException {
        m_LoopNumber = din.readByte();
    }// readData

}// class ReadStatusChannelsRequest
