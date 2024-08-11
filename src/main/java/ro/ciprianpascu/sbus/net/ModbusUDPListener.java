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
 * Class that implements a ModbusUDPListener.<br>
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
    }// ModbusUDPListener

    /**
     * Create a new {@link ModbusUDPListener} instance
     * listening to the given interface address.
     *
     * @param ifc an {@link InetAddress} instance.
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
    }// ModbusUDPListener

    public ModbusUDPListener(InetAddress ifc, UDPSlaveTerminalFactory terminalFactory) {
        m_Interface = ifc;
        this.m_TerminalFactory = terminalFactory;
    }

    /**
     * Returns the number of the port this {@link ModbusUDPListener}
     * is listening to.
     *
     * @return the number of the IP port as {@link int}.
     */
    public int getPort() {
        return m_Port;
    }// getPort
    
    /**
	 * Sets the process image.With it, the {@link ModbusUDPListener} will act as a device driver.
	 * @param processImage
	 */
    public void setProcessImage(ProcessImageImplementation processImage) {
		m_ProcessImage = processImage;
	}

    /**
     * Sets the number of the port this {@link ModbusUDPListener}
     * is listening to.
     *
     * @param port the number of the IP port as {@link int}.
     */
    public void setPort(int port) {
        m_Port = ((port > 0) ? port : Modbus.DEFAULT_PORT);
    }// setPort

    /**
     * Starts this {@link ModbusUDPListener}.
     */
    public void start() {
        // start listening
        try {
            m_Terminal = m_TerminalFactory.create(m_Interface, m_Port);
            m_Terminal.setLocalPort(m_Port);
            m_Terminal.activate();

            m_Handler = new ModbusUDPHandler(m_Terminal.getModbusTransport());
            m_HandlerThread = new Thread(m_Handler);
            m_HandlerThread.start();

        } catch (Exception e) {
            // FIXME: this is a major failure, how do we handle this
        }
        m_Listening = true;
    }// start

    /**
     * Stops this {@link ModbusUDPListener}.
     */
    public void stop() {
        // stop listening
        m_Terminal.deactivate();
        m_Handler.stop();
        m_Listening = false;
    }// stop

    /**
     * Tests if this {@link ModbusUDPListener} is listening
     * and accepting incoming connections.
     *
     * @return true if listening (and accepting incoming connections),
     *         false otherwise.
     */
    public boolean isListening() {
        return m_Listening;
    }// isListening

    class ModbusUDPHandler implements Runnable {

        private ModbusTransport m_Transport;
        private boolean m_Continue = true;

        public ModbusUDPHandler(ModbusTransport transport) {
            m_Transport = transport;
        }// constructor

        @Override
		public void run() {
			do {
				try {
					// 1. read the request
					ModbusRequest request = m_Transport.readRequest();
					if(request == null) {
						continue;
					}
					logger.trace("Request: {}", request.getHexMessage());
					ModbusResponse response = null;

					// test if Process image exists
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
						// other troubles, output for debug
						ex.printStackTrace();
					}
				} 
			} while (m_Continue);
		}// run

        public void stop() {
            m_Continue = false;
        }// stop

    }// inner class ModbusUDPHandler

    public int getLocalPort() {
        if (m_Terminal == null) {
            return -1;
        }
        return m_Terminal.getLocalPort();
    }

}// class ModbusUDPListener
