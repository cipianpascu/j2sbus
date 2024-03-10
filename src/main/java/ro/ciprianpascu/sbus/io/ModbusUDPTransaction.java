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

import ro.ciprianpascu.sbus.Modbus;
import ro.ciprianpascu.sbus.ModbusException;
import ro.ciprianpascu.sbus.ModbusIOException;
import ro.ciprianpascu.sbus.ModbusSlaveException;
import ro.ciprianpascu.sbus.msg.ExceptionResponse;
import ro.ciprianpascu.sbus.msg.ModbusRequest;
import ro.ciprianpascu.sbus.msg.ModbusResponse;
import ro.ciprianpascu.sbus.net.UDPMasterConnection;
import ro.ciprianpascu.sbus.net.UDPTerminal;
import ro.ciprianpascu.sbus.util.AtomicCounter;
import ro.ciprianpascu.sbus.util.Mutex;

/**
 * Class implementing the {@link ModbusTransaction}
 * interface for the UDP transport mechanism.
 *
 * @author Dieter Wimberger
 * @version %I% (%G%)
 */
public class ModbusUDPTransaction implements ModbusTransaction {

    // class attributes
    private static AtomicCounter c_TransactionID = new AtomicCounter(Modbus.DEFAULT_TRANSACTION_ID);

    // instance attributes and associations
    private UDPTerminal m_Terminal;
    private ModbusTransport m_IO;
    private ModbusRequest m_Request;
    private ModbusResponse m_Response;
    private boolean m_ValidityCheck = Modbus.DEFAULT_VALIDITYCHECK;
    private int m_Retries = Modbus.DEFAULT_RETRIES;
    private int m_RetryCounter = 0;

    private Mutex m_TransactionLock = new Mutex();

    private long m_RetryDelayMillis;

    /**
     * Constructs a new {@link ModbusUDPTransaction}
     * instance.
     */
    public ModbusUDPTransaction() {
    }// constructor

    /**
     * Constructs a new {@link ModbusUDPTransaction}
     * instance with a given {@link ModbusRequest} to
     * be send when the transaction is executed.
     * 
     *
     * @param request a {@link ModbusRequest} instance.
     */
    public ModbusUDPTransaction(ModbusRequest request) {
        setRequest(request);
    }// constructor

    /**
     * Constructs a new {@link ModbusUDPTransaction}
     * instance with a given {@link UDPTerminal} to
     * be used for transactions.
     * 
     *
     * @param terminal a {@link UDPTerminal} instance.
     */
    public ModbusUDPTransaction(UDPTerminal terminal) {
        setTerminal(terminal);
    }// constructor

    /**
     * Constructs a new {@link ModbusUDPTransaction}
     * instance with a given {@link UDPMasterConnection}
     * to be used for transactions.
     * 
     *
     * @param con a {@link UDPMasterConnection} instance.
     */
    public ModbusUDPTransaction(UDPMasterConnection con) {
        setTerminal(con.getTerminal());
    }// constructor

    /**
     * Sets the terminal on which this {@link ModbusTransaction}
     * should be executed.
* 
     *
     * @param terminal a {@link UDPTerminal}.
     */
    public void setTerminal(UDPTerminal terminal) {
        m_Terminal = terminal;
        if (terminal.isActive()) {
            m_IO = terminal.getModbusTransport();
        }
    }// setConnection

    @Override
    public void setRequest(ModbusRequest req) {
        m_Request = req;
        // m_Response = req.getResponse();
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
                    m_IO = m_Terminal.getModbusTransport();
                } catch (Exception ex) {
                    throw new ModbusIOException("Activation failed.");

                }
            }

            // 3. Retry transaction m_Retries times, in case of
            // I/O Exception problems.
            m_RetryCounter = 0;

            while (m_RetryCounter <= m_Retries) {
                if (m_RetryCounter != 0) {
                    Thread.sleep(m_RetryDelayMillis);
                }
                try {
                    // 3. write request, and read response,
                    // while holding the lock on the IO object
                    synchronized (m_IO) {
                        // write request message
                        m_IO.writeMessage(m_Request);
                        // read response message
                        m_Response = m_IO.readResponse();
                        break;
                    }
                } catch (ModbusIOException ex) {
                    m_RetryCounter++;
                    continue;
                }
            }

            // 4. deal with "application level" exceptions
            if (m_Response instanceof ExceptionResponse) {
                throw new ModbusSlaveException(((ExceptionResponse) m_Response).getExceptionCode());
            }

            if (isCheckingValidity()) {
                checkValidity();
            }
        } catch (InterruptedException ex) {
            throw new ModbusIOException("Thread acquiring lock was interrupted.");
        } finally {
            m_TransactionLock.release();
        }

    }// execute

    /**
     * Asserts if this {@link ModbusTCPTransaction} is
     * executable.
     *
     * @throws ModbusException if this transaction cannot be
     *             asserted as executable.
     */
    private void assertExecutable() throws ModbusException {
        if (m_Request == null || m_Terminal == null) {
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

}// class ModbusUDPTransaction
