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

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.ciprianpascu.sbus.Modbus;
import ro.ciprianpascu.sbus.ModbusIOException;
import ro.ciprianpascu.sbus.io.ModbusTransport;
import ro.ciprianpascu.sbus.msg.ModbusRequest;
import ro.ciprianpascu.sbus.msg.ModbusResponse;
import ro.ciprianpascu.sbus.procimg.ProcessImageImplementation;

/**
 * Class implementing a UDP Listener for the SBus protocol.
 * This listener handles incoming UDP messages and processes them
 * according to the SBus protocol specification.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public class ModbusUDPListener {
    private static final Logger logger = LoggerFactory.getLogger(ModbusUDPListener.class);

    private UDPSlaveTerminal m_Terminal;
    private ModbusUDPHandler m_Handler;
    private Thread m_HandlerThread;
    private int m_Port = Modbus.DEFAULT_PORT;
    private boolean m_Listening;
    private InetAddress m_Interface;

    private UDPSlaveTerminalFactory m_TerminalFactory;
    private ProcessImageImplementation m_ProcessImage;

    /**
     * Constructs a new ModbusUDPListener instance.
     */
    public ModbusUDPListener() {
        this(null);
    }

    /**
     * Creates a new ModbusUDPListener instance listening to the given interface address.
     *
     * @param ifc an InetAddress instance representing the interface to listen on
     */
    public ModbusUDPListener(InetAddress ifc) {
        this(ifc, new UDPSlaveTerminalFactory() {
            @Override
            public UDPSlaveTerminal create(InetAddress interfac, int port) {
                UDPSlaveTerminal terminal = new UDPSlaveTerminal(interfac, true);
                terminal.setLocalPort(port);
                return terminal;
            }
        });
    }

    /**
     * Creates a new ModbusUDPListener with a custom terminal factory.
     *
     * @param ifc the interface address to listen on
     * @param terminalFactory factory for creating UDP slave terminals
     */
    public ModbusUDPListener(InetAddress ifc, UDPSlaveTerminalFactory terminalFactory) {
        m_Interface = ifc;
        this.m_TerminalFactory = terminalFactory;
    }

    /**
     * Returns the port number this listener is listening to.
     *
     * @return the port number
     */
    public int getPort() {
        return m_Port;
    }
    
    /**
     * Sets the process image for this listener. The process image acts as a device driver,
     * handling the actual data processing for requests.
     *
     * @param processImage the ProcessImageImplementation to handle data processing
     */
    public void setProcessImage(ProcessImageImplementation processImage) {
        m_ProcessImage = processImage;
    }

    /**
     * Sets the port number for this listener.
     * If the port number is less than or equal to 0, the default port will be used.
     *
     * @param port the port number to listen on
     */
    public void setPort(int port) {
        m_Port = ((port > 0) ? port : Modbus.DEFAULT_PORT);
    }

    /**
     * Starts this listener, initializing the UDP terminal and handler thread.
     */
    public void start() {
        try {
            m_Terminal = m_TerminalFactory.create(m_Interface, m_Port);
            m_Terminal.setLocalPort(m_Port);
            m_Terminal.activate();

            m_Handler = new ModbusUDPHandler(m_Terminal.getModbusTransport());
            m_HandlerThread = new Thread(m_Handler);
            m_HandlerThread.start();

        } catch (Exception e) {
            logger.error("Failed to start UDP listener", e);
        }
        m_Listening = true;
    }

    /**
     * Stops this listener, deactivating the terminal and stopping the handler thread.
     */
    public void stop() {
        m_Terminal.deactivate();
        m_Handler.stop();
        m_Listening = false;
    }

    /**
     * Tests if this listener is currently active and accepting connections.
     *
     * @return true if listening, false otherwise
     */
    public boolean isListening() {
        return m_Listening;
    }

    /**
     * Handler class for processing UDP messages.
     */
    class ModbusUDPHandler implements Runnable {

        private ModbusTransport m_Transport;
        private boolean m_Continue = true;

        /**
         * Creates a new handler instance.
         *
         * @param transport the transport layer to use for communication
         */
        public ModbusUDPHandler(ModbusTransport transport) {
            m_Transport = transport;
        }

        @Override
        public void run() {
            do {
                try {
                    ModbusRequest request = m_Transport.readRequest();
                    if(request == null) {
                        continue;
                    }
                    logger.trace("Request: {}", request.getHexMessage());
                    ModbusResponse response = null;

                    if (m_ProcessImage == null) {
                        response = request.createExceptionResponse(Modbus.ILLEGAL_FUNCTION_EXCEPTION);
                    } else {
                        response = request.createResponse(m_ProcessImage);
                    }
                    logger.debug("Request: {}", request.getHexMessage());
                    logger.debug("Response: {}", response.getHexMessage());

                    m_Transport.writeMessage(response);
                } catch (ModbusIOException ex) {
                    if (!ex.isEOF()) {
                        logger.error("Error processing request", ex);
                    }
                } 
            } while (m_Continue);
        }

        /**
         * Stops the handler's processing loop.
         */
        public void stop() {
            m_Continue = false;
        }
    }

    /**
     * Gets the local port number the terminal is bound to.
     *
     * @return the local port number, or -1 if terminal is not initialized
     */
    public int getLocalPort() {
        if (m_Terminal == null) {
            return -1;
        }
        return m_Terminal.getLocalPort();
    }
}
