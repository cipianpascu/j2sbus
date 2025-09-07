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

import ro.ciprianpascu.sbus.SbusIOException;
import ro.ciprianpascu.sbus.msg.SbusMessage;
import ro.ciprianpascu.sbus.msg.SbusRequest;
import ro.ciprianpascu.sbus.msg.SbusResponse;
import ro.ciprianpascu.sbus.net.SbusMessageListener;

/**
 * Interface defining the I/O mechanisms for
 * {@link SbusMessage} instances.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu
 *
 * @version %I% (%G%)
 */
public interface SbusTransport {

    /**
     * Closes the raw input and output streams of
     * this {@link SbusTransport}.
     *
     *
     * @throws IOException if a stream
     *             cannot be closed properly.
     */
    public void close() throws IOException;

    /**
     * Adds a message listener for unsolicited messages.
     *
     * @param listener the listener to add
     */
    public void addMessageListener(SbusMessageListener listener);

    /**
     * Removes a message listener.
     *
     * @param listener the listener to remove
     */
    public void removeMessageListener(SbusMessageListener listener);

    /**
     * Writes a {@link SbusMessage} to the
     * output stream of this {@link SbusTransport}.
     *
     *
     * @param msg a {@link SbusMessage}.
     * @throws SbusIOException data cannot be
     *             written properly to the raw output stream of
     *             this {@link SbusTransport}.
     */
    public void writeMessage(SbusMessage msg) throws SbusIOException;

    /**
     * Reads a {@link SbusRequest} from the
     * input stream of this {@link SbusTransport}.
     *
     * @return req the {@link SbusRequest} read from the underlying stream.
     *
     * @throws SbusIOException data cannot be
     *             read properly from the raw input stream of
     *             this {@link SbusTransport}.
     */
    public SbusRequest readRequest() throws SbusIOException;

    /**
     * Reads a {@link SbusResponse} from the
     * input stream of this {@link SbusTransport} .
     *
     * @param transactionId Transaction identifier for the cached response
     * @return res the {@link SbusResponse} read from the underlying stream.
     *
     * @throws SbusIOException data cannot be
     *             read properly from the raw input stream of
     *             this {@link SbusTransport}.
     */
    public SbusResponse readResponse(String transactionId) throws SbusIOException;

}// class SbusTransport
