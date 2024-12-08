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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import ro.ciprianpascu.sbus.Modbus;

/**
 * Abstract class implementing a {@link ModbusResponse}.
 * This class provides specialised implementations with
 * the functionality they have in common.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu

 * @version %I% (%G%)
 */
public abstract class ModbusResponse extends ModbusMessageImpl {

    /**
     * Utility method to set the raw data of the message.
     * Should not be used except under rare circumstances.
* 
     *
     * @param msg the {@link byte[]} resembling the raw modbus
     *            response message.
     */
    protected void setMessage(byte[] msg) {
        try {
            readData(new DataInputStream(new ByteArrayInputStream(msg)));
        } catch (IOException ex) {

        }
    }// setMessage

    /**
     * Factory method creating the required specialized {@link ModbusResponse}
     * instance.
     *
     * @param functionCode the function code of the response as {@link int}.
     * @return a ModbusResponse instance specific for the given function code.
     */
    public static ModbusResponse createModbusResponse(int functionCode) {
        ModbusResponse response = null;

        switch (functionCode) {
            case Modbus.READ_STATUS_CHANNELS_REQUEST+1:
                response = new ReadStatusChannelsResponse();
                break;
            case Modbus.READ_TEMPERATURE_REQUEST+1:
            	response = new ReadTemperatureResponse();
                break;
            default:
                response = new ExceptionResponse();
                break;
        }
        return response;
    }// createModbusResponse

}// class ModbusResponse
