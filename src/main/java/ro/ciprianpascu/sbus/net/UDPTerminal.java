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

import ro.ciprianpascu.sbus.io.SbusTransport;

/**
 * Interface defining a UDP Terminal for the SBus protocol.
 * This terminal handles UDP communication, providing methods for
 * sending and receiving messages over the network.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public interface UDPTerminal {

    /**
     * Returns the local address of this terminal.
     *
     * @return the local InetAddress of this terminal
     */
    public InetAddress getLocalAddress();

    /**
     * Returns the local port of this terminal.
     *
     * @return the local port number
     */
    public int getLocalPort();

    /**
     * Tests if this terminal is active.
     *
     * @return true if the terminal is active, false otherwise
     */
    public boolean isActive();

    /**
     * Activates this terminal, preparing it for communication.
     *
     * @throws Exception if there is a network failure during activation
     */
    public void activate() throws Exception;

    /**
     * Deactivates this terminal, closing any open connections.
     */
    public void deactivate();

    /**
     * Returns the transport layer associated with this terminal.
     *
     * @return the SbusTransport instance used by this terminal
     */
    public SbusTransport getSbusTransport();

    /**
     * Sends a message through this terminal.
     *
     * @param msg the message as a byte array
     * @throws Exception if sending the message fails
     */
    public void sendMessage(byte[] msg) throws Exception;

    /**
     * Receives a message through this terminal.
     * This method will block until a message is received or a timeout occurs.
     *
     * @return the received message as a newly allocated byte array
     * @throws Exception if receiving the message fails
     */
    public byte[] receiveMessage() throws Exception;
    
    /**
     * Checks if there is a message available to be received.
     * This is a non-blocking operation that can be used to check
     * for available messages before calling receiveMessage().
     *
     * @return true if a message is available, false otherwise
     */
    public boolean hasMessage();
}
