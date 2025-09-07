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

import ro.ciprianpascu.sbus.SbusException;
import ro.ciprianpascu.sbus.msg.SbusRequest;
import ro.ciprianpascu.sbus.msg.SbusResponse;

/**
 * Interface defining a SbusTransaction.
 *
 * A transaction is defined by the sequence of
 * sending a request message and receiving a
 * related response message.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu
 *
 * @version %I% (%G%)
 */
public interface SbusTransaction {

    /**
     * Sets the {@link SbusRequest} for this
     * {@link SbusTransaction}.
     *
     * The related {@link SbusResponse} is acquired
     * from the passed in {@link SbusRequest} instance.<br>
     *
     *
     * @param req a {@link SbusRequest}.
     */
    public void setRequest(SbusRequest req);

    /**
     * Returns the {@link SbusRequest} instance
     * associated with this {@link SbusTransaction}.
     *
     *
     * @return the associated {@link SbusRequest} instance.
     */
    public SbusRequest getRequest();

    /**
     * Returns the {@link SbusResponse} instance
     * associated with this {@link SbusTransaction}.
     *
     *
     * @return the associated {@link SbusRequest} instance.
     */
    public SbusResponse getResponse();

    /**
     * Returns the actual transaction identifier of
     * this {@link SbusTransaction}.
     * The identifier is a 2-byte (short) non negative
     * String value subnetId + _ + unitId + _ + functionCode<br>
     *
     *
     * @return the actual transaction identifier as
     *         {@link int}.
     */
    public String getTransactionID();

    /**
     * Set the amount of retries for opening
     * the connection for executing the transaction.
     *
     *
     * @param retries the amount of retries as {@link int}.
     */
    public void setRetries(int retries);

    /**
     * Returns the amount of retries for opening
     * the connection for executing the transaction.
     *
     *
     * @return the amount of retries as {@link int}.
     */
    public int getRetries();

    /**
     * Sets the time to wait in milliseconds between retries
     *
     * @param retryDelayMillis the time to wait in milliseconds between retries
     */
    public void setRetryDelayMillis(int retryDelayMillis);

    /**
     * Returns the time to wait in milliseconds between retries
     *
     * @return time to wait in milliseconds between retries as {@link long}
     */
    public int getRetryDelayMillis();

    /**
     * Sets the flag that controls whether the
     * validity of a transaction will be checked.
     *
     *
     * @param b true if checking validity, false otherwise.
     */
    public void setCheckingValidity(boolean b);

    /**
     * Tests whether the validity of a transaction
     * will be checked.
     *
     *
     * @return true if checking validity, false otherwise.
     */
    public boolean isCheckingValidity();

    /**
     * Executes this {@link SbusTransaction}.
     * Locks the {@link SbusTransport} for sending
     * the {@link SbusRequest} and reading the
     * related {@link SbusResponse}.
     * If reconnecting is activated the connection will
     * be opened for the transaction and closed afterwards.
     *
     *
     * @throws SbusException if an I/O error occurs,
     *             or the response is a sbus protocol exception.
     */
    public void execute() throws SbusException;

}// interface SbusTransaction
