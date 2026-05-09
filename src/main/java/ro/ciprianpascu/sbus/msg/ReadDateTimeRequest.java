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
 * Class implementing a {@link ReadDateTimeRequest}.
 * Reads the current date and time from a logic module device.
 * It encapsulates the corresponding request message for OpCode 0x02C0.
 * The request carries no additional content (0 bytes payload).
 *
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public final class ReadDateTimeRequest extends SbusRequest {

    /**
     * Constructs a new {@link ReadDateTimeRequest} instance.
     */
    public ReadDateTimeRequest() {
        super();
        setFunctionCode(Sbus.READ_DATETIME_REQUEST);
        setDataLength(0);
    }// constructor

    @Override
    public SbusResponse createResponse(ProcessImageImplementation procimg) {
        ReadDateTimeResponse response = null;
        InputRegister[] inpregs = null;

        try {
            inpregs = procimg.getInputRegisterRange(0, 5); // 6 registers: year offset, month, day, hour, minute, second
        } catch (IllegalAddressException iaex) {
            return createExceptionResponse(Sbus.ILLEGAL_ADDRESS_EXCEPTION);
        }
        response = new ReadDateTimeResponse(
                inpregs[0].toUnsignedShort() + 2000, // year (stored offset + 2000)
                inpregs[1].toUnsignedShort(),         // month
                inpregs[2].toUnsignedShort(),         // day
                inpregs[3].toUnsignedShort(),         // hour
                inpregs[4].toUnsignedShort(),         // minute
                inpregs[5].toUnsignedShort()          // second
        );
        // transfer header data
        response.setSourceSubnetID(this.getSourceSubnetID());
        response.setSourceUnitID(this.getSourceUnitID());
        response.setSourceDeviceType(this.getSourceDeviceType());
        response.setSubnetID(this.getSubnetID());
        response.setUnitID(this.getUnitID());
        response.setFunctionCode(this.getFunctionCode());
        return response;
    }// createResponse

    @Override
    public void writeData(DataOutput dout) throws IOException {
        // No additional content for this request
    }// writeData

    @Override
    public void readData(DataInput din) throws IOException {
        // No additional content for this request
    }// readData

}// class ReadDateTimeRequest
