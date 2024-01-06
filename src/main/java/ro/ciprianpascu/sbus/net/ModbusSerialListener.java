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
import ro.ciprianpascu.sbus.util.SerialParameters;

/**
 * Class that implements a ModbusTCPListener.<br>
 * If listening, it accepts incoming requests
 * passing them on to be handled.
 *
 * @author Dieter Wimberger
 * @version %I% (%G%)
 */
public class ModbusSerialListener {

	/**
	 * {@link SerialConnectionFactory} implementation
	 */
    public static class SerialConnectionFactoryImpl implements SerialConnectionFactory {

        @Override
        public SerialConnection create(SerialParameters parameters) {
            return new SerialConnection(parameters);
        }

    }

    private static final Logger logger = LoggerFactory.getLogger(ModbusSerialListener.class);
    // Members
    private boolean m_Listening; // Flag for toggling listening/!listening
    private SerialConnection m_SerialCon;
    private static int c_RequestCounter = 0; // counter for amount of requests
    private SerialConnectionFactory m_ConnectionFactory;

    /**
     * Constructs a new {@link ModbusSerialListener} instance.
     *
     * @param params a {@link SerialParameters} instance.
     * @param connectionFactory a {@link SerialConnectionFactory} instance.
     */
    public ModbusSerialListener(SerialParameters params, SerialConnectionFactory connectionFactory) {
        this.m_ConnectionFactory = connectionFactory;
        m_SerialCon = m_ConnectionFactory.create(params);
        logger.trace("Created connection");
        listen();
    }// constructor

    /**
     * Constructor
     * @param params {@link SerialParameters} parameters
     */
    public ModbusSerialListener(SerialParameters params) {
        this(params, new SerialConnectionFactoryImpl());
    }

    /**
     * Listen to incoming messages.
     */
    private void listen() {
        try {
            m_Listening = true;
            m_SerialCon.open();
            logger.trace("Opened Serial connection.");

            ModbusTransport transport = m_SerialCon.getModbusTransport();
            do {
                if (m_Listening) {
                    try {
                        // 1. read the request
                        ModbusRequest request = transport.readRequest();
                        ModbusResponse response = null;

                        // test if Process image exists
                        if (ModbusCoupler.getReference().getProcessImage() == null) {
                            response = request.createExceptionResponse(Modbus.ILLEGAL_FUNCTION_EXCEPTION);
                        } else {
                            response = request.createResponse();
                        }

                        logger.debug("Request:{}", request.getHexMessage());
                        logger.debug("Response:{}", response.getHexMessage());

                        transport.writeMessage(response);

                        count();
                    } catch (ModbusIOException ex) {
                        ex.printStackTrace();
                        continue;
                    }
                }
                // ensure nice multithreading behaviour on specific platforms

            } while (true);

        } catch (Exception e) {
            // FIXME: this is a major failure, how do we handle this
            e.printStackTrace();
        }
    }// listen

    /**
     * Sets the listening flag of this {@link ModbusTCPListener}.
     *
     * @param b true if listening (and accepting incoming connections),
     *            false otherwise.
     */
    public void setListening(boolean b) {
        m_Listening = b;
    }// setListening

    /**
     * Tests if this {@link ModbusTCPListener} is listening
     * and accepting incoming connections.
     *
     * @return true if listening (and accepting incoming connections),
     *         false otherwise.
     */
    public boolean isListening() {
        return m_Listening;
    }// isListening

    private void count() {
        c_RequestCounter++;
        if (c_RequestCounter == REQUESTS_TOGC) {
            System.gc();
            c_RequestCounter = 0;
        }
    }// count

    private static final int REQUESTS_TOGC = 15;

}// class ModbusTCPListener