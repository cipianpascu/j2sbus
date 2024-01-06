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

import ro.ciprianpascu.sbus.Modbus;
import ro.ciprianpascu.sbus.ModbusException;
import ro.ciprianpascu.sbus.ModbusIOException;
import ro.ciprianpascu.sbus.ModbusSlaveException;
import ro.ciprianpascu.sbus.msg.ExceptionResponse;
import ro.ciprianpascu.sbus.msg.ModbusRequest;
import ro.ciprianpascu.sbus.msg.ModbusResponse;
import ro.ciprianpascu.sbus.net.TCPMasterConnection;
import ro.ciprianpascu.sbus.util.AtomicCounter;
import ro.ciprianpascu.sbus.util.Mutex;

/**
 * Class implementing the {@link ModbusTransaction}
 * interface.
 *
 * @author Dieter Wimberger
 * @version %I% (%G%)
 */
public class ModbusTCPTransaction implements ModbusTransaction {

    private static final Logger logger = LoggerFactory.getLogger(ModbusTCPTransaction.class);

    // class attributes
    private static AtomicCounter c_TransactionID = new AtomicCounter(Modbus.DEFAULT_TRANSACTION_ID);

    // instance attributes and associations
    private TCPMasterConnection m_Connection;
    private ModbusTransport m_IO;
    private ModbusRequest m_Request;
    private ModbusResponse m_Response;
    private boolean m_ValidityCheck = Modbus.DEFAULT_VALIDITYCHECK;
    private boolean m_Reconnecting = Modbus.DEFAULT_RECONNECTING;
    private int m_Retries = Modbus.DEFAULT_RETRIES;

    private Mutex m_TransactionLock = new Mutex();

    private long m_RetryDelayMillis;

    /**
     * Constructs a new {@link ModbusTCPTransaction}
     * instance.
     */
    public ModbusTCPTransaction() {
    }// constructor

    /**
     * Constructs a new {@link ModbusTCPTransaction}
     * instance with a given {@link ModbusRequest} to
     * be send when the transaction is executed.
     * 
     *
     * @param request a {@link ModbusRequest} instance.
     */
    public ModbusTCPTransaction(ModbusRequest request) {
        setRequest(request);
    }// constructor

    /**
     * Constructs a new {@link ModbusTCPTransaction}
     * instance with a given {@link TCPMasterConnection} to
     * be used for transactions.
     * 
     *
     * @param con a {@link TCPMasterConnection} instance.
     */
    public ModbusTCPTransaction(TCPMasterConnection con) {
        setConnection(con);
    }// constructor

    /**
     * Sets the connection on which this {@link ModbusTransaction}
     * should be executed.
     * 
     * An implementation should be able to
     * handle open and closed connections.<br>
     * 
     *
     * @param con a {@link TCPMasterConnection}.
     */
    public void setConnection(TCPMasterConnection con) {
        m_Connection = con;
        m_IO = con.getModbusTransport();
    }// setConnection

    @Override
    public void setRequest(ModbusRequest req) {
        m_Request = req;
    }// setRequest

    @Override
    public ModbusRequest getRequest() {
        return m_Request;
    }// getRequest

    @Override
    public ModbusResponse getResponse() {
        return m_Response;
    }// getResponse

    @Override
    public int getTransactionID() {
        return c_TransactionID.get();
    }// getTransactionID

    @Override
    public void setCheckingValidity(boolean b) {
        m_ValidityCheck = b;
    }// setCheckingValidity

    @Override
    public boolean isCheckingValidity() {
        return m_ValidityCheck;
    }// isCheckingValidity

    /**
     * Sets the flag that controls whether a
     * connection is openend and closed for
     * each execution or not.
     * 
     *
     * @param b true if reconnecting, false otherwise.
     */
    public void setReconnecting(boolean b) {
        m_Reconnecting = b;
    }// setReconnecting

    /**
     * Tests if the connection will be openend
     * and closed for <b>each</b> execution.
     * 
     *
     * @return true if reconnecting, false otherwise.
     */
    public boolean isReconnecting() {
        return m_Reconnecting;
    }// isReconnecting

    @Override
    public int getRetries() {
        return m_Retries;
    }// getRetries

    @Override
    public void setRetries(int num) {
        m_Retries = num;
    }// setRetries

    @Override
    public void execute() throws ModbusIOException, ModbusSlaveException, ModbusException {

        // 1. check that the transaction can be executed
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
            if (!m_Connection.isConnected()) {
                try {
                    m_Connection.connect();
                    m_IO = m_Connection.getModbusTransport();
                } catch (Exception ex) {
                    throw new ModbusIOException("Connecting failed.");
                }
            }

            // 4. Retry transaction m_Retries times, in case of
            // I/O Exception problems.
            int tries = 0;

            do {
                try {
                    // toggle and set the id
                    m_Request.setTransactionID(c_TransactionID.increment());
                    // 3. write request, and read response
                    logger.trace("Sending request with transaction ID {}: {}", m_Request.getTransactionID(), m_Request);
                    m_IO.writeMessage(m_Request);
                    // read response message
                    m_Response = m_IO.readResponse();
                    logger.trace("Received response with transaction ID {}", m_Response.getTransactionID());
                    break;
                } catch (ModbusIOException ex) {
                    tries++;
                    logger.debug(
                            "execute try {}/{} error: {}. Request: {} (unit id {} & transaction {}). Address: {}:{}",
                            tries, m_Retries + 1, ex.getMessage(), m_Request, m_Request.getUnitID(),
                            m_Request.getTransactionID(), m_Connection.getAddress(), m_Connection.getPort());
                    if (tries >= m_Retries) {
                        logger.debug(
                                "execute reached max tries {}, throwing last error: {}. Request: {} (unit id {} & transaction {}). Address: {}:{}",
                                m_Retries + 1, ex.getMessage(), m_Request, m_Request.getUnitID(),
                                m_Request.getTransactionID(), m_Connection.getAddress(), m_Connection.getPort());
                        throw ex;

                    }
                    Thread.sleep(m_RetryDelayMillis);
                }
            } while (true);

            if (tries > 0) {
                logger.debug(
                        "execute eventually succeeded with {} re-tries. Request: {} (unit id {} & transaction {}). Address: {}:{}",
                        tries, m_Request, m_Request.getUnitID(), m_Request.getTransactionID(),
                        m_Connection.getAddress(), m_Connection.getPort());
            }

            // 5. deal with "application level" exceptions
            if (m_Response instanceof ExceptionResponse) {
                throw new ModbusSlaveException(((ExceptionResponse) m_Response).getExceptionCode());
            }

            // 6. Check transaction validity
            if (isCheckingValidity()) {
                checkValidity();
            }

        } catch (InterruptedException ex) {
            throw new ModbusIOException("Thread acquiring lock was interrupted.");
        } finally {
            // Finally: close connection if reconnecting
            if (isReconnecting() && m_Connection != null) {
                m_Connection.close();
            }

            m_TransactionLock.release();
        }
    }// execute

    /**
     * Asserts if this {@link ModbusTCPTransaction} is
     * executable.
     *
     * @throws ModbusException if the transaction cannot be asserted
     *             as executable.
     */
    private void assertExecutable() throws ModbusException {
        if (m_Request == null || m_Connection == null) {
            throw new ModbusException("Assertion failed, transaction not executable");
        }
    }// assertExecuteable

    /**
     * Checks the validity of the transaction, by
     * checking if the values of the response correspond
     * to the values of the request.
     * Use an override to provide some checks, this method will only return.
     *
     * @throws ModbusException if this transaction has not been valid.
     */
    protected void checkValidity() throws ModbusException {
    }// checkValidity

    @Override
    public long getRetryDelayMillis() {
        return m_RetryDelayMillis;
    }

    @Override
    public void setRetryDelayMillis(long retryDelayMillis) {
        this.m_RetryDelayMillis = retryDelayMillis;
    }

}// class ModbusTCPTransaction
