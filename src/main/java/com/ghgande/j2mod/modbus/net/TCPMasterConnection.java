/*
 * Copyright 2002-2016 jamod & j2mod development teams
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
 */
package com.ghgande.j2mod.modbus.net;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.io.AbstractModbusTransport;
import com.ghgande.j2mod.modbus.io.ModbusRTUTCPTransport;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransport;
import com.ghgande.j2mod.modbus.util.ModbusUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Objects;

/**
 * Class that implements a TCPMasterConnection.
 *
 * @author Dieter Wimberger
 * @author Julie Haugh
 * @author Steve O'Hara (4NG)
 * @version 2.0 (March 2016)
 */
public class TCPMasterConnection {

    private static final Logger logger = LoggerFactory.getLogger(TCPMasterConnection.class);

    // instance attributes
    private Socket socket;
    private int timeout = Modbus.DEFAULT_TIMEOUT;
    private boolean connected;

    private InetAddress address;
    private NetworkInterface networkInterface = null;
    private int port = Modbus.DEFAULT_PORT;

    private ModbusTCPTransport transport;

    private boolean useRtuOverTcp = false;

    /**
     * useUrgentData - sent a byte of urgent data when testing the TCP
     * connection.
     */
    private boolean useUrgentData = false;

    /**
     * Constructs a <code>TCPMasterConnection</code> instance with a given
     * destination address.
     *
     * @param adr the destination <code>InetAddress</code>.
     */
    public TCPMasterConnection(InetAddress adr) {
        address = adr;
    }

    /**
     * Prepares the associated <code>ModbusTransport</code> of this
     * <code>TCPMasterConnection</code> for use.
     *
     * @param useRtuOverTcp True if the RTU protocol should be used over TCP
     *
     * @throws IOException if an I/O related error occurs.
     */
    private void prepareTransport(boolean useRtuOverTcp) throws IOException {

        // If we don't have a transport, or the transport type has changed
        if (transport == null || (this.useRtuOverTcp != useRtuOverTcp)) {

            // Save the flag to tell us which transport type to use
            this.useRtuOverTcp = useRtuOverTcp;

            // Select the correct transport
            if (useRtuOverTcp) {
                logger.trace("prepareTransport() -> using RTU over TCP transport.");
                transport = new ModbusRTUTCPTransport(socket);
                transport.setMaster(this);
            }
            else {
                logger.trace("prepareTransport() -> using standard TCP transport.");
                transport = new ModbusTCPTransport(socket);
                transport.setMaster(this);
            }
        }
        else {
            logger.trace("prepareTransport() -> using custom transport: {}", transport.getClass().getSimpleName());
            transport.setSocket(socket);
        }
        transport.setTimeout(timeout);
    }

    /**
     * Opens this <code>TCPMasterConnection</code>.
     *
     * @throws Exception if there is a network failure.
     */
    public void connect() throws Exception {
        connect(useRtuOverTcp);
    }

    /**
     * Opens this <code>TCPMasterConnection</code>.
     *
     * @param useRtuOverTcp True if the RTU protocol should be used over TCP
     *
     * @throws Exception if there is a network failure.
     */
    public void connect(boolean useRtuOverTcp) throws Exception {
        if (!isConnected()) {
            logger.debug("connect()");

            // Create a socket without auto-connecting

            socket = new Socket();
            socket.setReuseAddress(true);
            socket.setSoLinger(true, 1);
            socket.setKeepAlive(true);
            setTimeout(timeout);

            // If a Network Interface has been specified, then attempt to force the socket
            // to be bound to that card

            if (networkInterface != null) {
                socket.bind(new InetSocketAddress(networkInterface.getInetAddresses().nextElement(), 0));
            }

            // Connect - only wait for the timeout number of milliseconds

            socket.connect(new InetSocketAddress(address, port), timeout);

            // Prepare the transport

            prepareTransport(useRtuOverTcp);
            connected = true;
        }
    }

    /**
     * Tests if this <code>TCPMasterConnection</code> is connected.
     *
     * @return <code>true</code> if connected, <code>false</code> otherwise.
     */
    public synchronized boolean isConnected() {
        if (connected && socket != null) {
            if (!socket.isConnected() || socket.isClosed() || socket.isInputShutdown() || socket.isOutputShutdown()) {
                try {
                    socket.close();
                }
                catch (IOException e) {
                    logger.error("Socket exception", e);
                }
                finally {
                    connected = false;
                }
            }
            else {
                /*
                 * When useUrgentData is set, a byte of urgent data
                 * will be sent to the server to test the connection. If
                 * the connection is actually broken, an IException will
                 * occur and the connection will be closed.
                 *
                 * Note: RFC 6093 has decreed that we stop using urgent
                 * data.
                 */
                if (useUrgentData) {
                    try {
                        socket.sendUrgentData(0);
                        ModbusUtil.sleep(5);
                    }
                    catch (IOException e) {
                        connected = false;
                        try {
                            socket.close();
                        }
                        catch (IOException e1) {
                            // Do nothing.
                        }
                    }
                }
            }
        }
        return connected;
    }

    /**
     * Closes this <code>TCPMasterConnection</code>.
     */
    public void close() {
        if (connected) {
            try {
                transport.close();
            }
            catch (IOException ex) {
                logger.debug("close()", ex);
            }
            finally {
                connected = false;
            }
        }
    }

    /**
     * Returns the <code>ModbusTransport</code> associated with this
     * <code>TCPMasterConnection</code>.
     *
     * @return the connection's <code>ModbusTransport</code>.
     */
    public AbstractModbusTransport getModbusTransport() {
        return transport;
    }

    /**
     * Set the <code>ModbusTransport</code> associated with this
     * <code>TCPMasterConnection</code>
     * @param trans associated transport
     */
    public void setModbusTransport(ModbusTCPTransport trans) {
        transport = trans;
    }

    /**
     * Returns the timeout (msec) for this <code>TCPMasterConnection</code>.
     *
     * @return the timeout as <code>int</code>.
     */
    public synchronized int getTimeout() {
        return timeout;
    }

    /**
     * Sets the timeout (msec) for this <code>TCPMasterConnection</code>. This is both the
     * connection timeout and the transaction timeout
     *
     * @param timeout - the timeout in milliseconds as an <code>int</code>.
     */
    public synchronized void setTimeout(int timeout) {
        try {
            this.timeout = timeout;
            if (socket != null) {
                socket.setSoTimeout(timeout);
            }
        }
        catch (IOException ex) {
            logger.warn("Could not set timeout to value {}", timeout, ex);
        }
    }

    /**
     * Returns the destination port of this <code>TCPMasterConnection</code>.
     *
     * @return the port number as <code>int</code>.
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the destination port of this <code>TCPMasterConnection</code>. The
     * default is defined as <code>Modbus.DEFAULT_PORT</code>.
     *
     * @param port the port number as <code>int</code>.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Returns the destination <code>InetAddress</code> of this
     * <code>TCPMasterConnection</code>.
     *
     * @return the destination address as <code>InetAddress</code>.
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * Sets the destination <code>InetAddress</code> of this
     * <code>TCPMasterConnection</code>.
     *
     * @param adr the destination address as <code>InetAddress</code>.
     */
    public void setAddress(InetAddress adr) {
        address = adr;
    }

    /**
     * Gets the local <code>NetworkInterface</code> that this socket is bound
     * to. If null (the default), the socket is bound to the adapter chosen by the system
     * based on routing to the destination address when the connection is made
     *
     * @return the network card as <code>NetworkInterface</code>.
     */
    public NetworkInterface getNetworkInterface() {
        return networkInterface;
    }

    /**
     * Sets the local <code>NetworkInterface</code> to bind to for this
     * <code>TCPMasterConnection</code>.
     *
     * @param networkInterface of the network card as <code>NetworkInterface</code>.
     */
    public void setNetworkInterface(NetworkInterface networkInterface) {
        this.networkInterface = networkInterface;
    }

    /**
     * Gets the current setting of the flag which controls sending
     * urgent data to test a network connection.
     *
     * @return Status
     */
    public boolean getUseUrgentData() {
        return useUrgentData;
    }

    /**
     * Set the flag which controls sending urgent data to test a
     * network connection.
     *
     * @param useUrgentData - Connections are testing using urgent data.
     */
    public void setUseUrgentData(boolean useUrgentData) {
        this.useUrgentData = useUrgentData;
    }

    /**
     * Returns true if this connection is an RTU over TCP type
     * 
     * @return True if RTU over TCP
     */
    public boolean isUseRtuOverTcp() {
        return useRtuOverTcp;
    }

    /**
     * Sets the transport type to use
     * Normally set during the connection but can also be set after a connection has been established
     *
     * @param useRtuOverTcp True if the transport should be interpreted as RTU over tCP
     *
     * @throws Exception If the connection is not valid
     */
    public void setUseRtuOverTcp(boolean useRtuOverTcp) throws Exception {
        this.useRtuOverTcp = useRtuOverTcp;
        if (isConnected()) {
            prepareTransport(useRtuOverTcp);
        }
    }

    /**
     * A {@link TCPMasterConnection} is equal if {@link InetAddress} &amp; port are equal. There is no way how two
     * different {@link TCPMasterConnection}s can use the same {@link Socket} at the same time, therefore this is good
     * enough for an equality test
     *
     * @param obj Entity to be checked for equality
     * @return <code>true</code> if object is equal, <code>false</code> otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || TCPMasterConnection.class != obj.getClass()) {
            return false;
        } else {
            TCPMasterConnection other = (TCPMasterConnection) obj;
            return this == obj || this.address.equals(other.address) && this.port == other.port;
        }
    }

    /**
     * The unique value of the {@link TCPMasterConnection} is calculated from the unique values of the
     * {@link InetAddress} &amp; port
     *
     * @return Unique integer hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(address, port);
    }
}
