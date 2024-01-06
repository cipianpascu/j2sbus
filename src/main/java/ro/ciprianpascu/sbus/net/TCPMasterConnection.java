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
import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.ciprianpascu.sbus.Modbus;
import ro.ciprianpascu.sbus.io.ModbusTCPRTUTransport;
import ro.ciprianpascu.sbus.io.ModbusTCPTransport;
import ro.ciprianpascu.sbus.io.ModbusTransport;

/**
 * Class that implements a TCPMasterConnection.
 *
 * @author Dieter Wimberger - Initial contribution
 * @author Andrew Fiddian-Green - Added 'rtuEncoded' support
 */
public class TCPMasterConnection implements ModbusSlaveConnection {
    private static final Logger logger = LoggerFactory.getLogger(TCPMasterConnection.class);

    // instance attributes
    private Socket m_Socket;
    private int m_Timeout = Modbus.DEFAULT_TIMEOUT;
    private boolean m_Connected;

    private InetAddress m_Address;
    private int m_Port = Modbus.DEFAULT_PORT;

    // private int m_Retries = Modbus.DEFAULT_RETRIES;
    private ModbusTCPTransport m_ModbusTransport;

    private int m_ConnectTimeoutMillis;

    private boolean rtuEncoded;

    /**
     * Constructs a {@link TCPMasterConnection} instance
     * with a given destination address.
     *
     * @param adr the destination {@link InetAddress}.
     */
    public TCPMasterConnection(InetAddress adr) {
        m_Address = adr;
    }// constructor

    public TCPMasterConnection(InetAddress adr, int port) {
        this(adr);
        setPort(port);
    }

    public TCPMasterConnection(InetAddress adr, int port, int connectTimeoutMillis, boolean rtuEncoded) {
        this(adr, port);
        setConnectTimeoutMillis(connectTimeoutMillis);
        this.rtuEncoded = rtuEncoded;
    }

    /**
     * Opens this {@link TCPMasterConnection}.
     *
     * @throws Exception if there is a network failure.
     */
    @Override
    public synchronized boolean connect() throws Exception {
        if (!isConnected()) {
            logger.debug("connect()");
            m_Socket = new Socket();
            m_Socket.connect(new InetSocketAddress(m_Address, m_Port), this.m_ConnectTimeoutMillis);
            setTimeout(m_Timeout);
            m_Socket.setReuseAddress(true);
            m_Socket.setSoLinger(true, 1);
            m_Socket.setKeepAlive(true);
            prepareTransport();
            m_Connected = true;
        }
        return m_Connected;
    }// connect

    /**
     * Closes this {@link TCPMasterConnection}.
     */
    public void close() {
        if (m_Connected) {
            try {
                m_ModbusTransport.close();
            } catch (IOException ex) {
                logger.warn("close()", ex);
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
    }// getModbusTransport

    /**
     * Prepares the associated {@link ModbusTransport} of this
     * {@link TCPMasterConnection} for use.
     *
     * @throws IOException if an I/O related error occurs.
     */
    private void prepareTransport() throws IOException {
        if (m_ModbusTransport == null) {
            m_ModbusTransport = rtuEncoded ? new ModbusTCPRTUTransport(m_Socket) : new ModbusTCPTransport(m_Socket);
        } else {
            m_ModbusTransport.setSocket(m_Socket);
        }
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
     * Sets the timeout for this {@link TCPMasterConnection}.
     *
     * @param timeout the timeout as {@link int}.
     */
    public void setTimeout(int timeout) {
        m_Timeout = timeout;
        if (m_Socket != null) {
            try {
                m_Socket.setSoTimeout(m_Timeout);
            } catch (IOException ex) {
                logger.warn("Could not set socket timeout on connection {} {}: {}", getAddress(), getPort(),
                        ex.getMessage());
            }
        }
    }// setReceiveTimeout

    /**
     * Returns the destination port of this
     * {@link TCPMasterConnection}.
     *
     * @return the port number as {@link int}.
     */
    public int getPort() {
        return m_Port;
    }// getPort

    /**
     * Sets the destination port of this
     * {@link TCPMasterConnection}.
     * The default is defined as {@link Modbus#DEFAULT_PORT}.
     *
     * @param port the port number as {@link int}.
     */
    public void setPort(int port) {
        m_Port = port;
    }// setPort

    /**
     * Returns the destination {@link InetAddress} of this
     * {@link TCPMasterConnection}.
     *
     * @return the destination address as {@link InetAddress}.
     */
    public InetAddress getAddress() {
        return m_Address;
    }// getAddress

    /**
     * Sets the destination {@link InetAddress} of this
     * {@link TCPMasterConnection}.
     *
     * @param adr the destination address as {@link InetAddress}.
     */
    public void setAddress(InetAddress adr) {
        m_Address = adr;
    }// setAddress

    /**
     * Tests if this {@link TCPMasterConnection} is connected.
     *
     * @return true if connected, false otherwise.
     */
    @Override
    public boolean isConnected() {
        // From j2mod originally. Sockets that are not fully open are closed.
        if (m_Connected && m_Socket != null) {
            // Call close() if the connection is not fully "open"
            if (!m_Socket.isConnected() || m_Socket.isClosed() || m_Socket.isInputShutdown()
                    || m_Socket.isOutputShutdown()) {
                close();
            }
        }
        return m_Connected;
    }// isConnected

    @Override
    public void resetConnection() {
        close();
    }

    @Override
    public String toString() {
        return "TCPMasterConnection [m_Socket=" + m_Socket + ", m_Timeout=" + m_Timeout + ", m_Connected=" + m_Connected
                + ", m_Address=" + m_Address + ", m_Port=" + m_Port + ", m_ModbusTransport=" + m_ModbusTransport
                + ", m_ConnectTimeoutMillis=" + m_ConnectTimeoutMillis + ", rtuEncoded=" + rtuEncoded + "]";
    }

    public int getConnectTimeoutMillis() {
        return m_ConnectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(int m_ConnectTimeoutMillis) {
        this.m_ConnectTimeoutMillis = m_ConnectTimeoutMillis;
    }

}// class TCPMasterConnection
