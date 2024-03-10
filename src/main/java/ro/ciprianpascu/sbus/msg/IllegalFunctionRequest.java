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

/**
 * Provides an implementation of a {@link ModbusRequest}
 * which is created for illegal or non implemented
 * function codes.
* 
 * This is just a helper class to keep the implementation
 * patterns the same for all cases.
 *
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public class IllegalFunctionRequest extends ModbusRequest {

    /**
     * Constructs a new {@link IllegalFunctionRequest} instance for
     * a given function code.
     *
     * @param fc the function code as {@link int}.
     */
    public IllegalFunctionRequest(int fc) {
        setFunctionCode(fc);
    }// constructor

    @Override
    public ModbusResponse createResponse() {
        return this.createExceptionResponse(Modbus.ILLEGAL_FUNCTION_EXCEPTION);
    }// createResponse

    @Override
    public void writeData(DataOutput dout) throws IOException {
        throw new RuntimeException();
    }// writeData

    @Override
    public void readData(DataInput din) throws IOException {
        // skip all following bytes
        int length = getDataLength();
        for (int i = 0; i < length; i++) {
            din.readByte();
        }
    }// readData

}// IllegalFunctionRequest
