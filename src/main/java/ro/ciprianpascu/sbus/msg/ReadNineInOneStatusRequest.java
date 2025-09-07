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
 * Class implementing a {@link ReadNineInOneStatusRequest}.
 * The implementation reads status from 9-in-1 sensor device including
 * dry contacts, motion status, and LUX value. It encapsulates the 
 * corresponding request message for OpCode 0xDB00.
 *
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public final class ReadNineInOneStatusRequest extends SbusRequest {

    /**
     * Constructs a new {@link ReadNineInOneStatusRequest}
     * instance.
     */
    public ReadNineInOneStatusRequest() {
        super();
        setFunctionCode(Sbus.READ_NINE_IN_ONE_STATUS_REQUEST);
        setDataLength(0);
    }// constructor


    @Override
    public SbusResponse createResponse(ProcessImageImplementation procimg) {
        ReadNineInOneStatusResponse response = null;
        InputRegister[] inpregs = null;

        // Get 8 bytes of sensor status data
        try {
            inpregs = procimg.getInputRegisterRange(0, 7); // 8 bytes (0-7)
        } catch (IllegalAddressException iaex) {
            return createExceptionResponse(Sbus.ILLEGAL_ADDRESS_EXCEPTION);
        }
        response = new ReadNineInOneStatusResponse(inpregs);
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
        // No additional data to write for this request
    }// writeData

    @Override
    public void readData(DataInput din) throws IOException {
        // No additional data to read for this request
    }// readData

}// class ReadNineInOneStatusRequest
