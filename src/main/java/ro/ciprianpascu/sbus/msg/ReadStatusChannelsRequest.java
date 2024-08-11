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
import ro.ciprianpascu.sbus.procimg.InputRegister;
import ro.ciprianpascu.sbus.procimg.ProcessImageImplementation;

/**
 * Class implementing a {@link ReadStatusChannelsRequest}.
 * The implementation directly correlates with the class 0
 * function <i>read multiple registers (FC 4)</i>. It
 * encapsulates the corresponding request message.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu

 * @version %I% (%G%)
 */
public final class ReadStatusChannelsRequest extends ModbusRequest {

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
    public ModbusResponse createResponse(ProcessImageImplementation procimg) {
        ReadStatusChannelsResponse response = null;
        InputRegister[] inpregs = null;

        // 1. get input registers range. WordCount depends on the device type (num channels + 1)
        try {
            inpregs = procimg.getInputRegisterRange(0, procimg.getRegisterCount()-1);
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


    @Override
    public void writeData(DataOutput dout) throws IOException {
    }// writeData

    @Override
    public void readData(DataInput din) throws IOException {
    }// readData

}// class ReadStatusChannelsRequest