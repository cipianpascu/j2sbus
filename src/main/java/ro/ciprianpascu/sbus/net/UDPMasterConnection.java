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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.ciprianpascu.sbus.Sbus;
import ro.ciprianpascu.sbus.io.SbusTransport;

/**
 * Class implementing a UDP Master Connection for the SBus protocol.
 * This class handles UDP communication with a slave device, managing
 * connection establishment, message transport, and connection termination.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public class UDPMasterConnection implements SbusSlaveConnection {

    private static final Logger logger = LoggerFactory.getLogger(UDPMasterConnection.class);

    /** Default local port for UDP communication */
    private static int LOCAL_PORT = Sbus.DEFAULT_PORT;

    /** The terminal handling UDP communication */
    private UDPMasterTerminal m_Terminal;

    /** Timeout for communication operations in milliseconds */
    private int m_Timeout = Sbus.DEFAULT_TIMEOUT;

    /** Flag indicating if the connection is active */
    private boolean m_Connected;

    /** Remote address for the slave device */
    protected InetAddress m_RemoteAddress;

    /** Remote port for the slave device */
    private int m_Port = Sbus.DEFAULT_PORT;

    /**
     * Constructs a new UDPMasterConnection with default settings.
     */
    public UDPMasterConnection() {
    }

    /**
     * Constructs a new UDPMasterConnection with a specific port.
     *
     * @param port the port number to use for communication
     */
    public UDPMasterConnection(int port) {
        setPort(port);
    }

    /**
     * Opens this connection to the slave device.
     * This initializes the UDP terminal and establishes communication.
     *
     * @return true if connection was successful, false otherwise
     * @throws Exception if there is a network failure
     */
    @Override
    public synchronized boolean connect() throws Exception {
        if (!m_Connected) {
            InetAddress localAddress = getLocalIP();
            m_Terminal = new UDPMasterTerminal(localAddress);
            m_Terminal.setLocalPort(LOCAL_PORT);
            m_Terminal.setRemoteAddress(
                    m_RemoteAddress == null ? InetAddress.getByAddress(getTargetIP(localAddress.getAddress()))
                            : m_RemoteAddress);
            m_Terminal.setRemotePort(m_Port);
            m_Terminal.activate();
            m_Terminal.setTimeout(m_Timeout);
            m_Connected = true;
        }
        return m_Connected;
    }

    /**
     * Closes this connection and releases resources.
     */
    public void close() {
        if (m_Connected) {
            try {
                m_Terminal.deactivate();
            } catch (Exception ex) {
                if (Sbus.debug) {
                    ex.printStackTrace();
                }
            }
            m_Connected = false;
        }
    }

    /**
     * Returns the transport layer used for communication.
     *
     * @return the connection's SbusTransport instance
     */
    public SbusTransport getSbusTransport() {
        return m_Terminal.getSbusTransport();
    }

    /**
     * Adds a message listener for unsolicited messages.
     * The listener will be notified when messages arrive that are not
     * part of a synchronous request/response transaction.
     *
     * @param listener the listener to add
     */
    public void addMessageListener(SbusMessageListener listener) {
        SbusTransport transport = getSbusTransport();
        transport.addMessageListener(listener);
    }

    /**
     * Removes a message listener.
     *
     * @param listener the listener to remove
     */
    public void removeMessageListener(SbusMessageListener listener) {
        SbusTransport transport = getSbusTransport();
        transport.removeMessageListener(listener);
    }

    /**
     * Returns the terminal used for handling UDP communication.
     *
     * @return the UDPTerminal instance
     */
    public UDPTerminal getTerminal() {
        return m_Terminal;
    }

    /**
     * Returns the timeout for communication operations.
     *
     * @return the timeout in milliseconds
     */
    public int getTimeout() {
        return m_Timeout;
    }

    /**
     * Sets the timeout for communication operations.
     *
     * @param timeout the timeout in milliseconds
     */
    public void setTimeout(int timeout) {
        m_Timeout = timeout;
        m_Terminal.setTimeout(timeout);
    }

    /**
     * Returns the destination port used for communication.
     *
     * @return the port number
     */
    public int getPort() {
        return m_Port;
    }

    /**
     * Sets the destination port for communication.
     * The default is defined as DEFAULT_PORT.
     *
     * @param port the port number to use
     */
    public void setPort(int port) {
        m_Port = port;
    }

    /**
     * Sets the destination address for communication.
     *
     * @param adr the destination address
     */
    public void setRemoteAddress(InetAddress adr) {
        m_RemoteAddress = adr;
    }

    /**
     * Tests if this connection is currently active.
     *
     * @return true if connected, false otherwise
     */
    @Override
    public boolean isConnected() {
        return m_Connected;
    }

    @Override
    public void resetConnection() {
        close();
    }

    @Override
    public String toString() {
        return "UDPMasterConnection [m_Terminal=" + m_Terminal + ", m_Timeout=" + m_Timeout + ", m_Connected="
                + m_Connected + ", m_Port=" + m_Port + "]";
    }

    /**
     * Gets the local IP address for this connection.
     * Searches through network interfaces for a non-loopback IPv4 address.
     *
     * @return the local InetAddress
     * @throws SocketException if there is a network interface error
     * @throws UnknownHostException if no suitable address is found
     */
    private InetAddress getLocalIP() throws SocketException, UnknownHostException {
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
            NetworkInterface intf = en.nextElement();
            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                InetAddress inetAddress = enumIpAddr.nextElement();
                if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                    return inetAddress;
                }
            }
        }
        return InetAddress.getLocalHost();
    }

    /**
     * Calculates the target broadcast IP address based on the local IP.
     * Determines the network class (A, B, C) and sets appropriate broadcast bits.
     *
     * @param arraybyteLocalIP the local IP address as a byte array
     * @return the target broadcast IP address as a byte array
     */
    private byte[] getTargetIP(byte[] arraybyteLocalIP) {
        byte[] arraybyteTargetIP = new byte[4];
        byte byteBit;

        byteBit = (byte) ((arraybyteLocalIP[0] & 0xFF) >> 5);
        if (((byteBit & 0xFF) >= 0) && ((byteBit & 0xFF) <= 3)) { // Class A
            arraybyteTargetIP[0] = arraybyteLocalIP[0];
            arraybyteTargetIP[1] = (byte) 255;
            arraybyteTargetIP[2] = (byte) 255;
            arraybyteTargetIP[3] = (byte) 255;
        } else if (((byteBit & 0xFF) >= 4) && ((byteBit & 0xFF) <= 5)) { // Class B
            arraybyteTargetIP[0] = arraybyteLocalIP[0];
            arraybyteTargetIP[1] = arraybyteLocalIP[1];
            arraybyteTargetIP[2] = (byte) 255;
            arraybyteTargetIP[3] = (byte) 255;
        } else if (((byteBit & 0xFF) >= 6) && ((byteBit & 0xFF) <= 7)) { // Class C
            arraybyteTargetIP[0] = arraybyteLocalIP[0];
            arraybyteTargetIP[1] = arraybyteLocalIP[1];
            arraybyteTargetIP[2] = arraybyteLocalIP[2];
            arraybyteTargetIP[3] = (byte) 255;
        } else { // Default broadcast
            arraybyteTargetIP[0] = (byte) 255;
            arraybyteTargetIP[1] = (byte) 255;
            arraybyteTargetIP[2] = (byte) 255;
            arraybyteTargetIP[3] = (byte) 255;
        }

        return arraybyteTargetIP;
    }
}
