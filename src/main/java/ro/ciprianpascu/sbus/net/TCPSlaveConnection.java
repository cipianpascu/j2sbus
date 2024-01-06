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

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.ciprianpascu.sbus.Modbus;
import ro.ciprianpascu.sbus.io.ModbusSocketBasedTransportFactory;
import ro.ciprianpascu.sbus.io.ModbusTCPTransport;
import ro.ciprianpascu.sbus.io.ModbusTransport;

/**
 * Class that implements a TCPSlaveConnection.
 *
 * @author Dieter Wimberger
 * @version %I% (%G%)
 */
public class TCPSlaveConnection {

    public static class ModbusTCPTransportFactory implements ModbusSocketBasedTransportFactory {

        @Override
        public ModbusTransport create(Socket socket) {
            return new ModbusTCPTransport(socket);
        }

    }

    private static final Logger logger = LoggerFactory.getLogger(TCPSlaveConnection.class);

    // instance attributes
    private Socket m_Socket;
    private int m_Timeout = Modbus.DEFAULT_TIMEOUT;
    private boolean m_Connected;
    private ModbusTransport m_ModbusTransport;

    private ModbusSocketBasedTransportFactory m_TransportFactory;

    /**
     * Constructs a {@link TCPSlaveConnection} instance
     * using a given socket instance.
     *
     * @param socket the socket instance to be used for communication.
     */
    public TCPSlaveConnection(Socket socket, ModbusSocketBasedTransportFactory transportFactory) {
        this.m_TransportFactory = transportFactory;
        try {
            setSocket(socket);
        } catch (IOException ex) {
            final String errMsg = "Socket invalid";
            logger.warn(errMsg);
            // @commentstart@
            throw new IllegalStateException(errMsg);
            // @commentend@
        }
    }// constructor

    public TCPSlaveConnection(Socket socket) {
        this(socket, new ModbusTCPTransportFactory());
    }

    /**
     * Closes this {@link TCPSlaveConnection}.
     */
    public void close() {
        if (m_Connected) {
            try {
                m_ModbusTransport.close();
                m_Socket.close();
            } catch (IOException ex) {
                if (Modbus.debug) {
                    ex.printStackTrace();
                }
            }
            m_Connected = false;
        }
    }// close

    /**
     * Returns the {@link ModbusTransport} associated with this
     * {@link TCPMasterConnection}.
     *
     * @return the connection's {@link ModbusTransport}.
     */
    public ModbusTransport getModbusTransport() {
        return m_ModbusTransport;
    }// getIO

    /**
     * Prepares the associated {@link ModbusTransport} of this
     * {@link TCPMasterConnection} for use.
     *
     * @param socket the socket to be used for communication.
     * @throws IOException if an I/O related error occurs.
     */
    private void setSocket(Socket socket) throws IOException {
        m_Socket = socket;
        if (m_ModbusTransport == null) {
            m_ModbusTransport = this.m_TransportFactory.create(m_Socket);
        } else {
            throw new IllegalStateException("socket cannot be re-set");
        }
        m_Connected = true;
    }// prepareIO

    /**
     * Returns the timeout for this {@link TCPMasterConnection}.
     *
     * @return the timeout as {@link int}.
     */
    public int getTimeout() {
        return m_Timeout;
    }// getReceiveTimeout

    /**
     * Sets the timeout for this {@link TCPSlaveConnection}.
     *
     * @param timeout the timeout as {@link int}.
     */
    public void setTimeout(int timeout) {
        m_Timeout = timeout;
        try {
            m_Socket.setSoTimeout(m_Timeout);
        } catch (IOException ex) {
            // handle?
        }
    }// setReceiveTimeout

    /**
     * Returns the destination port of this
     * {@link TCPMasterConnection}.
     *
     * @return the port number as {@link int}.
     */
    public int getPort() {
        return m_Socket.getLocalPort();
    }// getPort

    /**
     * Returns the destination {@link InetAddress} of this
     * {@link TCPMasterConnection}.
     *
     * @return the destination address as {@link InetAddress}.
     */
    public InetAddress getAddress() {
        return m_Socket.getLocalAddress();
    }// getAddress

    /**
     * Tests if this {@link TCPMasterConnection} is connected.
     *
     * @return true if connected, false otherwise.
     */
    public boolean isConnected() {
        return m_Connected;
    }// isConnected

}// class TCPSlaveConnection
