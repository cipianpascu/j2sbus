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

import ro.ciprianpascu.sbus.io.ModbusTransport;

/**
 * Interface defining a {@link UDPTerminal}.
 *
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public interface UDPTerminal {

    /**
     * Returns the local address of this {@link UDPTerminal}.
     *
     * @return an {@link InetAddress} instance.
     */
    public InetAddress getLocalAddress();

    /**
     * Returns the local port of this {@link UDPTerminal}.
     *
     * @return the local port as {@link int}.
     */
    public int getLocalPort();

    /**
     * Tests if this {@link UDPTerminal} is active.
     *
     * @return true if active, false otherwise.
     */
    public boolean isActive();

    /**
     * Activate this {@link UDPTerminal}.
     *
     * @throws java.lang.Exception if there is a network failure.
     */
    public void activate() throws Exception;

    /**
     * Deactivates this {@link UDPTerminal}.
     */
    public void deactivate();

    /**
     * Returns the {@link ModbusTransport} associated with this
     * {@link UDPTerminal}.
     *
     * @return a {@link ModbusTransport} instance.
     */
    public ModbusTransport getModbusTransport();

    /**
     * Sends the given message.
     *
     * @param msg the message as {@link byte[]}.
     * @throws Exception if sending the message fails.
     */
    public void sendMessage(byte[] msg) throws Exception;

    /**
     * Receives and returns a message.
     *
     * @return the message as a newly allocated {@link byte[]}.
     * @throws Exception if receiving a message fails.
     */
    public byte[] receiveMessage() throws Exception;

}// interface UDPTerminal
