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

package ro.ciprianpascu.sbus.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.ciprianpascu.sbus.Modbus;
import ro.ciprianpascu.sbus.ModbusCoupler;
import ro.ciprianpascu.sbus.ModbusIOException;
import ro.ciprianpascu.sbus.io.ModbusTransport;
import ro.ciprianpascu.sbus.msg.ModbusRequest;
import ro.ciprianpascu.sbus.msg.ModbusResponse;

/**
 * Class implementing a handler for incoming Modbus/TCP requests.
 *
 * @author Dieter Wimberger
 * @version %I% (%G%)
 */
public class TCPConnectionHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TCPConnectionHandler.class);

    private TCPSlaveConnection m_Connection;
    private ModbusTransport m_Transport;

    /**
     * Constructs a new {@link TCPConnectionHandler} instance.
     *
     * @param con an incoming connection.
     */
    public TCPConnectionHandler(TCPSlaveConnection con) {
        setConnection(con);
    }// constructor

    /**
     * Sets a connection to be handled by this {@link 
     * TCPConnectionHandler}.
     *
     * @param con a {@link TCPSlaveConnection}.
     */
    public void setConnection(TCPSlaveConnection con) {
        m_Connection = con;
        m_Transport = m_Connection.getModbusTransport();
    }// setConnection

    @Override
    public void run() {
        try {
            do {
                // 1. read the request
                ModbusRequest request = m_Transport.readRequest();
                ModbusResponse response = null;

                // test if Process image exists
                if (ModbusCoupler.getReference().getProcessImage() == null) {
                    response = request.createExceptionResponse(Modbus.ILLEGAL_FUNCTION_EXCEPTION);
                } else {
                    response = request.createResponse();
                }
                logger.debug("Request (transaction id {}): {}", request.getTransactionID(), request.getHexMessage());
                logger.debug("Response (transaction id {}): {}", response.getTransactionID(), response.getHexMessage());

                m_Transport.writeMessage(response);
            } while (true);
        } catch (ModbusIOException ex) {
            if (!ex.isEOF()) {
                // other troubles, output for debug
                ex.printStackTrace();
            }
        } finally {
            try {
                m_Connection.close();
            } catch (Exception ex) {
                // ignore
            }

        }
    }// run

}// TCPConnectionHandler
