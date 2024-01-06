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

import ro.ciprianpascu.sbus.Modbus;
import ro.ciprianpascu.sbus.io.ModbusTransport;

/**
 * Class that implements a UDPMasterConnection.
 *
 * @author Dieter Wimberger
 * @version %I% (%G%)
 */
public class UDPMasterConnection implements ModbusSlaveConnection {

    private static int LOCAL_PORT = 50000;

    // instance attributes
    private UDPMasterTerminal m_Terminal;
    private int m_Timeout = Modbus.DEFAULT_TIMEOUT;
    private boolean m_Connected;

    private InetAddress m_Address;
    private int m_Port = Modbus.DEFAULT_PORT;

    /**
     * Constructs a {@link UDPMasterConnection} instance
     * with a given destination address.
     *
     * @param adr the destination {@link InetAddress}.
     */
    public UDPMasterConnection(InetAddress adr) {
        m_Address = adr;
    }// constructor

    public UDPMasterConnection(InetAddress adr, int port) {
        this(adr);
        setPort(port);
    }

    /**
     * Opens this {@link UDPMasterConnection}.
     *
     * @throws Exception if there is a network failure.
     */
    @Override
    public synchronized boolean connect() throws Exception {
        if (!m_Connected) {
            m_Terminal = new UDPMasterTerminal();
            m_Terminal.setLocalAddress(InetAddress.getLocalHost());
            m_Terminal.setLocalPort(LOCAL_PORT);
            m_Terminal.setRemoteAddress(m_Address);
            m_Terminal.setRemotePort(m_Port);
            m_Terminal.setTimeout(m_Timeout);
            m_Terminal.activate();
            m_Connected = true;
        }
        return m_Connected;
    }// connect

    /**
     * Closes this {@link UDPMasterConnection}.
     */
    public void close() {
        if (m_Connected) {
            try {
                m_Terminal.deactivate();
            } catch (Exception ex) {
                if (Modbus.debug) {
                    ex.printStackTrace();
                }
            }
            m_Connected = false;
        }
    }// close

    /**
     * Returns the {@link ModbusTransport} associated with this
     * {@link UDPMasterConnection}.
     *
     * @return the connection's {@link ModbusTransport}.
     */
    public ModbusTransport getModbusTransport() {
        return m_Terminal.getModbusTransport();
    }// getModbusTransport

    /**
     * Returns the terminal used for handling the package traffic.
     *
     * @return a {@link UDPTerminal} instance.
     */
    public UDPTerminal getTerminal() {
        return m_Terminal;
    }// getTerminal

    /**
     * Returns the timeout for this {@link UDPMasterConnection}.
     *
     * @return the timeout as {@link int}.
     */
    public int getTimeout() {
        return m_Timeout;
    }// getReceiveTimeout

    /**
     * Sets the timeout for this {@link UDPMasterConnection}.
     *
     * @param timeout the timeout as {@link int}.
     */
    public void setTimeout(int timeout) {
        m_Timeout = timeout;
        m_Terminal.setTimeout(timeout);
    }// setReceiveTimeout

    /**
     * Returns the destination port of this
     * {@link UDPMasterConnection}.
     *
     * @return the port number as {@link int}.
     */
    public int getPort() {
        return m_Port;
    }// getPort

    /**
     * Sets the destination port of this
     * {@link UDPMasterConnection}.
     * The default is defined as {@link Modbus#DEFAULT_PORT}.
     *
     * @param port the port number as {@link int}.
     */
    public void setPort(int port) {
        m_Port = port;
    }// setPort

    /**
     * Returns the destination {@link InetAddress} of this
     * {@link UDPMasterConnection}.
     *
     * @return the destination address as {@link InetAddress}.
     */
    public InetAddress getAddress() {
        return m_Address;
    }// getAddress

    /**
     * Sets the destination {@link InetAddress} of this
     * {@link UDPMasterConnection}.
     *
     * @param adr the destination address as {@link InetAddress}.
     */
    public void setAddress(InetAddress adr) {
        m_Address = adr;
    }// setAddress

    /**
     * Tests if this {@link UDPMasterConnection} is connected.
     *
     * @return true if connected, false otherwise.
     */
    @Override
    public boolean isConnected() {
        return m_Connected;
    }// isConnected

    @Override
    public void resetConnection() {
        close();
    }

    @Override
    public String toString() {
        return "UDPMasterConnection [m_Terminal=" + m_Terminal + ", m_Timeout=" + m_Timeout + ", m_Connected="
                + m_Connected + ", m_Address=" + m_Address + ", m_Port=" + m_Port + "]";
    }
}// class UDPMasterConnection
