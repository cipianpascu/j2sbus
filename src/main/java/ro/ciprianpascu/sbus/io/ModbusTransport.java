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

package ro.ciprianpascu.sbus.io;

import java.io.IOException;

import ro.ciprianpascu.sbus.ModbusIOException;
import ro.ciprianpascu.sbus.msg.ModbusMessage;
import ro.ciprianpascu.sbus.msg.ModbusRequest;
import ro.ciprianpascu.sbus.msg.ModbusResponse;

/**
 * Interface defining the I/O mechanisms for
 * {@link ModbusMessage} instances.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu

 * @version %I% (%G%)
 */
public interface ModbusTransport {

    /**
     * Closes the raw input and output streams of
     * this {@link ModbusTransport}.
     * 
     *
     * @throws IOException if a stream
     *             cannot be closed properly.
     */
    public void close() throws IOException;

    /**
     * Writes a {@link ModbusMessage} to the
     * output stream of this {@link ModbusTransport}.
     * 
     *
     * @param msg a {@link ModbusMessage}.
     * @throws ModbusIOException data cannot be
     *             written properly to the raw output stream of
     *             this {@link ModbusTransport}.
     */
    public void writeMessage(ModbusMessage msg) throws ModbusIOException;

    /**
     * Reads a {@link ModbusRequest} from the
     * input stream of this {@link ModbusTransport}.
     * 
     * @return req the {@link ModbusRequest} read from the underlying stream.
     *
     * @throws ModbusIOException data cannot be
     *             read properly from the raw input stream of
     *             this {@link ModbusTransport}.
     */
    public ModbusRequest readRequest() throws ModbusIOException;

    /**
     * Reads a {@link ModbusResponse} from the
     * input stream of this {@link ModbusTransport} .
     * @param transactionId Transaction identifier for the cached response
     * @return res the {@link ModbusResponse} read from the underlying stream.
     *
     * @throws ModbusIOException data cannot be
     *             read properly from the raw input stream of
     *             this {@link ModbusTransport}.
     */
    public ModbusResponse readResponse(String transactionId) throws ModbusIOException;

}// class ModbusTransport
