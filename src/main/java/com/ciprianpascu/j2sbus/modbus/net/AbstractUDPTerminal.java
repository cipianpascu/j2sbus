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

import java.net.DatagramSocket;
import java.net.InetAddress;

import com.ciprianpascu.j2sbus.modbus.Modbus;
import com.ciprianpascu.j2sbus.modbus.io.ModbusUDPTransport;

/**
 * Interface defining a <code>UDPTerminal</code>.
 *
 * @author Dieter Wimberger
 * @author Steve O'Hara (4NG)
 * @version 2.0 (March 2016)
 */
public abstract class AbstractUDPTerminal {

    protected InetAddress address;
    protected ModbusUDPTransport transport;
    protected boolean active;
    protected int port = Modbus.DEFAULT_PORT;
    protected int timeout = Modbus.DEFAULT_TIMEOUT;
    protected DatagramSocket socket;

    /**
     * Gets the local adapter address
     *
     * @return Adapter address
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * Returns the local port the terminal is listening on
     *
     * @return Port number
     */
    public synchronized int getPort() {
        return port;
    }

    /**
     * Sets the local port the terminal is running on
     *
     * @param port Local port
     */
    protected synchronized void setPort(int port) {
        this.port = port;
    }

    /**
     * Tests if this <code>UDPSlaveTerminal</code> is active.
     *
     * @return <code>true</code> if active, <code>false</code> otherwise.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the timeout in milliseconds for this <code>UDPSlaveTerminal</code>.
     *
     * @param timeout the timeout as <code>int</code>.
     */
    public synchronized void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Get the transport
     * @return Transport
     */
    public ModbusUDPTransport getTransport() {
        return transport;
    }

    /**
     * Activate this <code>UDPTerminal</code>.
     *
     * @throws java.lang.Exception if there is a network failure.
     */
    public abstract void activate() throws Exception;

    /**
     * Deactivates this <code>UDPTerminal</code>.
     */
    public abstract void deactivate();

    /**
     * Sends the given message.
     *
     * @param msg the message as <code>byte[]</code>.
     *
     * @throws Exception if sending the message fails.
     */
    public abstract void sendMessage(byte[] msg) throws Exception;

    /**
     * Receives and returns a message.
     *
     * @return the message as a newly allocated <code>byte[]</code>.
     *
     * @throws Exception if receiving a message fails.
     */
    public abstract byte[] receiveMessage() throws Exception;

}