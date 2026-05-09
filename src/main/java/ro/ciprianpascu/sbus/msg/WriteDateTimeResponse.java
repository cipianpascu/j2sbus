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
 * Class implementing a {@link WriteDateTimeResponse}.
 * Carries the acknowledgement for a write date/time operation on a logic module.
 * It encapsulates the corresponding response message for OpCode 0x02C7.
 * The response payload is 1 byte: {@link Sbus#SUCCESS} (0xF8) or {@link Sbus#FAILURE} (0xF5).
 *
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public final class WriteDateTimeResponse extends SbusResponse {

    // instance attributes
    private boolean m_Success;

    /**
     * Constructs a new {@link WriteDateTimeResponse} instance. Reader focus.
     */
    public WriteDateTimeResponse() {
        super();
        setFunctionCode(Sbus.WRITE_DATETIME_REQUEST + 1);
        setDataLength(1);
    }// constructor

    /**
     * Constructs a new {@link WriteDateTimeResponse} instance. Writer focus.
     *
     * @param success true if the write operation succeeded, false otherwise
     */
    public WriteDateTimeResponse(boolean success) {
        super();
        setFunctionCode(Sbus.WRITE_DATETIME_REQUEST + 1);
        m_Success = success;
        setDataLength(1);
    }// constructor

    /**
     * Returns whether the write operation was successful.
     *
     * @return true if successful, false if failed
     */
    public boolean isSuccess() {
        return m_Success;
    }// isSuccess

    @Override
    public void writeData(DataOutput dout) throws IOException {
        dout.writeByte(m_Success ? Sbus.SUCCESS : Sbus.FAILURE);
    }// writeData

    @Override
    public void readData(DataInput din) throws IOException {
        m_Success = (din.readUnsignedByte() == Sbus.SUCCESS);
        setDataLength(1);
    }// readData

}// class WriteDateTimeResponse
