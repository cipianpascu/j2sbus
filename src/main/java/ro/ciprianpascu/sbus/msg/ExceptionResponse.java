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
 * Provides the a{@link SbusResponse}
 * implementation that represents a Sbus exception.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu

 * @version %I% (%G%)
 */
public class ExceptionResponse extends SbusResponse {

    // instance attributes
    private int m_ExceptionCode = -1;

    /**
     * Constructs a new {@link ExceptionResponse} instance.
     */
    public ExceptionResponse() {
        // exception code, unitid and function code not counted.
        setDataLength(1);
    }// constructor

    /**
     * Constructs a new {@link ExceptionResponse} instance with
     * a given function code. Adds the exception offset automatically.
     *
     * @param fc the function code as {@link int}.
     */
    public ExceptionResponse(int fc) {
        // unitid and function code not counted.
        setDataLength(1);
        setFunctionCode(fc + Sbus.EXCEPTION_OFFSET);
    }// constructor

    /**
     * Constructs a new {@link ExceptionResponse} instance with
     * a given function code and an exception code. The function
     * code will be automatically increased with the exception offset.
     *
     *
     * @param fc the function code as {@link int}.
     * @param exc the exception code as {@link int}.
     */
    public ExceptionResponse(int fc, int exc) {
        // exception code, unitid and function code not counted.
        setDataLength(1);
        setFunctionCode(fc + Sbus.EXCEPTION_OFFSET);
        m_ExceptionCode = exc;
    }// constructor

    /**
     * Returns the Sbus exception code of this
     * {@link ExceptionResponse}.
* 
     *
     * @return the exception code as {@link int}.
     */
    public int getExceptionCode() {
        return m_ExceptionCode;
    }// getExceptionCode

    @Override
    public void writeData(DataOutput dout) throws IOException {
        dout.writeByte(getExceptionCode());
    }// writeData

    @Override
    public void readData(DataInput din) throws IOException {
        m_ExceptionCode = din.readUnsignedByte();
    }// readData


}// ExceptionResponse
