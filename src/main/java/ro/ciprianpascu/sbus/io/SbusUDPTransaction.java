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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.ciprianpascu.sbus.Sbus;
import ro.ciprianpascu.sbus.SbusException;
import ro.ciprianpascu.sbus.SbusIOException;
import ro.ciprianpascu.sbus.SbusSlaveException;
import ro.ciprianpascu.sbus.msg.ExceptionResponse;
import ro.ciprianpascu.sbus.msg.SbusRequest;
import ro.ciprianpascu.sbus.msg.SbusResponse;
import ro.ciprianpascu.sbus.net.UDPMasterConnection;
import ro.ciprianpascu.sbus.net.UDPTerminal;
import ro.ciprianpascu.sbus.util.Mutex;

/**
 * Class implementing the {@link SbusTransaction}
 * interface for the UDP transport mechanism.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu
 *
 * @version %I% (%G%)
 */
public class SbusUDPTransaction implements SbusTransaction {

    private static final Logger logger = LoggerFactory.getLogger(SbusUDPTransaction.class);

    // instance attributes and associations
    private UDPTerminal m_Terminal;
    private SbusTransport m_IO;
    private SbusRequest m_Request;
    private SbusResponse m_Response;
    private boolean m_ValidityCheck = Sbus.DEFAULT_VALIDITYCHECK;
    private int m_Retries = Sbus.DEFAULT_RETRIES;

    private Mutex m_TransactionLock = new Mutex();

    /**
     * Constructs a new {@link SbusUDPTransaction}
     * instance.
     */
    public SbusUDPTransaction() {
    }// constructor

    /**
     * Constructs a new {@link SbusUDPTransaction}
     * instance with a given {@link SbusRequest} to
     * be send when the transaction is executed.
     *
     *
     * @param request a {@link SbusRequest} instance.
     */
    public SbusUDPTransaction(SbusRequest request) {
        setRequest(request);
    }// constructor

    /**
     * Constructs a new {@link SbusUDPTransaction}
     * instance with a given {@link UDPTerminal} to
     * be used for transactions.
     *
     *
     * @param terminal a {@link UDPTerminal} instance.
     */
    public SbusUDPTransaction(UDPTerminal terminal) {
        setTerminal(terminal);
    }// constructor

    /**
     * Constructs a new {@link SbusUDPTransaction}
     * instance with a given {@link UDPMasterConnection}
     * to be used for transactions.
     *
     *
     * @param con a {@link UDPMasterConnection} instance.
     */
    public SbusUDPTransaction(UDPMasterConnection con) {
        setTerminal(con.getTerminal());
    }// constructor

    /**
     * Sets the terminal on which this {@link SbusTransaction}
     * should be executed.
     *
     *
     * @param terminal a {@link UDPTerminal}.
     */
    public void setTerminal(UDPTerminal terminal) {
        m_Terminal = terminal;
        if (terminal.isActive()) {
            m_IO = terminal.getSbusTransport();
        }
    }// setConnection

    @Override
    public void setRequest(SbusRequest req) {
        m_Request = req;
        // m_Response = req.getResponse();
    }// setRequest

    @Override
    public SbusRequest getRequest() {
        return m_Request;
    }// getRequest

    @Override
    public SbusResponse getResponse() {
        return m_Response;
    }// getResponse

    @Override
    public String getTransactionID() {
        return m_Request.getSubnetID() + "_" + m_Request.getUnitID() + "_" + m_Request.getFunctionCode();
    }// getTransactionID

    /**
     * Gets the transaction ID for the response message.
     *
     * @return a string representing the response transaction ID
     */
    public String getResponseTransactionID() {
        // Use the request function code for transaction ID
        // Wire analysis: request=0xE3E7, response=0xE3E8, so response = request + 1
        int requestFunctionCode = m_Response.getFunctionCode() - 1;
        return m_Response.getSourceSubnetID() + "_" + m_Response.getSourceUnitID() + "_" + requestFunctionCode;
    }// getResponseTransactionID

    @Override
    public void setCheckingValidity(boolean b) {
        m_ValidityCheck = b;
    }// setCheckingValidity

    @Override
    public boolean isCheckingValidity() {
        return m_ValidityCheck;
    }// isCheckingValidity

    @Override
    public int getRetries() {
        return m_Retries;
    }// getRetries

    @Override
    public void setRetries(int num) {
        m_Retries = num;
    }// setRetries

    @Override
    public void execute() throws SbusIOException, SbusSlaveException, SbusException {

        // 1. assert executeability
        assertExecutable();

        try {
            // 2. Lock transaction
            /**
             * Note: The way this explicit synchronization is implemented at the moment,
             * there is no ordering of pending threads. The Mutex will simply call notify()
             * and the JVM will handle the rest.
             */
            m_TransactionLock.acquire();

            // 3. open the connection if not connected
            if (!m_Terminal.isActive()) {
                try {
                    m_Terminal.activate();
                    m_IO = m_Terminal.getSbusTransport();
                } catch (Exception ex) {
                    throw new SbusIOException("Activation failed.");

                }
            }

            // 3. Retry transaction m_Retries times, in case of
            // I/O Exception problems.
            int m_RetryCounter = 0;

            while (m_RetryCounter < m_Retries) {
                try {
                    // 3. write request, and read response,
                    // while holding the lock on the IO object
                    synchronized (m_IO) {
                        // write request message
                        m_IO.writeMessage(m_Request);
                        if (m_Request.isFireAndForget()) {
                            break;
                        }
                        // read response message
                        m_Response = m_IO.readResponse(getTransactionID());
                        if (isCheckingValidity()) {
                            checkValidity();
                        }
                        break;
                    }
                } catch (SbusIOException ex) {
                    logger.debug("SbusIOException: " + ex.getMessage());
                }
                m_RetryCounter++;
            }

            // 4. deal with "application level" exceptions
            if (m_Response instanceof ExceptionResponse) {
                throw new SbusSlaveException(((ExceptionResponse) m_Response).getExceptionCode());
            }

        } catch (InterruptedException ex) {
            throw new SbusIOException("Thread acquiring lock was interrupted.");
        } finally {
            m_TransactionLock.release();
        }

    }// execute

    /**
     * Asserts if this {@link SbusTCPTransaction} is
     * executable.
     *
     * @throws SbusException if this transaction cannot be
     *             asserted as executable.
     */
    private void assertExecutable() throws SbusException {
        if (m_Request == null || m_Terminal == null) {
            throw new SbusException("Assertion failed, transaction not executable");
        }
    }// assertExecuteable

    /**
     * Checks the validity of the transaction, by
     * checking if the values of the response correspond
     * to the values of the request.
     * Use an override to provide some checks, this method will only return.
     *
     * @throws SbusException if this transaction has not been valid.
     */
    protected void checkValidity() throws SbusException {
        if (!getTransactionID().equals(getResponseTransactionID())) {
            throw new SbusIOException("Wrong message. Keep trying");
        }
    }// checkValidity

    @Override
    public int getRetryDelayMillis() {
        return m_Terminal.getTimeout();
    }

    @Override
    public void setRetryDelayMillis(int retryDelayMillis) {
        m_Terminal.setTimeout(retryDelayMillis);
    }

}// class SbusUDPTransaction
