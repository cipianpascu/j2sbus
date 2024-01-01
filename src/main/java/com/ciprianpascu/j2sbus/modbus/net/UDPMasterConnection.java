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
package com.ciprianpascu.j2sbus.modbus.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ciprianpascu.j2sbus.modbus.Modbus;
import com.ciprianpascu.j2sbus.modbus.io.AbstractModbusTransport;

import java.net.InetAddress;

/**
 * Class that implements a UDPMasterConnection.
 *
 * @author Dieter Wimberger
 * @author Steve O'Hara (4NG)
 * @version 2.0 (March 2016)
 */
public class UDPMasterConnection {

    private static final Logger logger = LoggerFactory.getLogger(UDPMasterConnection.class);

    //instance attributes
    private UDPMasterTerminal terminal;
    private int timeout = Modbus.DEFAULT_TIMEOUT;
    private boolean connected;

    private InetAddress address;
    private int port = Modbus.DEFAULT_PORT;

    /**
     * Constructs a <code>UDPMasterConnection</code> instance
     * with a given destination address.
     *
     * @param adr the destination <code>InetAddress</code>.
     */
    public UDPMasterConnection(InetAddress adr) {
        address = adr;
    }

    /**
     * Opens this <code>UDPMasterConnection</code>.
     *
     * @throws Exception if there is a network failure.
     */
    public void connect() throws Exception {
        if (!connected) {
            terminal = new UDPMasterTerminal(address);
            terminal.setPort(port);
            terminal.setTimeout(timeout);
            terminal.activate();
            connected = true;
        }
    }

    /**
     * Closes this <code>UDPMasterConnection</code>.
     */
    public void close() {
        if (connected) {
            try {
                terminal.deactivate();
            }
            catch (Exception ex) {
                logger.debug("Exception occurred while closing UDPMasterConnection", ex);
            }
            connected = false;
        }
    }

    /**
     * Returns the <code>ModbusTransport</code> associated with this
     * <code>UDPMasterConnection</code>.
     *
     * @return the connection's <code>ModbusTransport</code>.
     */
    public AbstractModbusTransport getModbusTransport() {
        return terminal == null ? null : terminal.getTransport();
    }

    /**
     * Returns the terminal used for handling the package traffic.
     *
     * @return a <code>UDPTerminal</code> instance.
     */
    public AbstractUDPTerminal getTerminal() {
        return terminal;
    }

    /**
     * Returns the timeout for this <code>UDPMasterConnection</code>.
     *
     * @return the timeout as <code>int</code>.
     */
    public synchronized int getTimeout() {
        return timeout;
    }

    /**
     * Sets the timeout for this <code>UDPMasterConnection</code>.
     *
     * @param timeout the timeout as <code>int</code>.
     */
    public synchronized void setTimeout(int timeout) {
        this.timeout = timeout;
        if (terminal != null) {
            terminal.setTimeout(timeout);
        }
    }

    /**
     * Returns the destination port of this
     * <code>UDPMasterConnection</code>.
     *
     * @return the port number as <code>int</code>.
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the destination port of this
     * <code>UDPMasterConnection</code>.
     * The default is defined as <code>Modbus.DEFAULT_PORT</code>.
     *
     * @param port the port number as <code>int</code>.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Returns the destination <code>InetAddress</code> of this
     * <code>UDPMasterConnection</code>.
     *
     * @return the destination address as <code>InetAddress</code>.
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * Sets the destination <code>InetAddress</code> of this
     * <code>UDPMasterConnection</code>.
     *
     * @param adr the destination address as <code>InetAddress</code>.
     */
    public void setAddress(InetAddress adr) {
        address = adr;
    }

    /**
     * Tests if this <code>UDPMasterConnection</code> is connected.
     *
     * @return <code>true</code> if connected, <code>false</code> otherwise.
     */
    public boolean isConnected() {
        return connected;
    }

}