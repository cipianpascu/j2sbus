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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.ciprianpascu.sbus.Modbus;
import ro.ciprianpascu.sbus.io.ModbusTransport;
import ro.ciprianpascu.sbus.io.ModbusUDPTransport;

/**
 * Class implementing a {@link UDPMasterTerminal}.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu

 * @version %I% (%G%)
 */
class UDPMasterTerminal implements UDPTerminal {
    private static final Logger logger = LoggerFactory.getLogger(UDPMasterTerminal.class);

    private DatagramSocket m_Socket;
    private int m_Timeout = Modbus.DEFAULT_TIMEOUT;
    private boolean m_Active;
    protected InetAddress m_LocalAddress;
    protected InetAddress m_RemoteAddress;
    private int m_RemotePort = Modbus.DEFAULT_PORT;
    private int m_LocalPort = Modbus.DEFAULT_PORT;
    protected ModbusUDPTransport m_ModbusTransport;

    public UDPMasterTerminal() {
    }// constructor

    protected UDPMasterTerminal(InetAddress addr) {
        m_RemoteAddress = addr;
    }// constructor

    @Override
    public InetAddress getLocalAddress() {
        return m_LocalAddress;
    }// getLocalAddress

    public void setLocalAddress(InetAddress addr) {
        m_LocalAddress = addr;
    }// setLocalAddress

    @Override
    public int getLocalPort() {
        return m_LocalPort;
    }// getLocalPort

    protected void setLocalPort(int port) {
        m_LocalPort = port;
    }// setLocalPort

    /**
     * Returns the destination port of this
     * {@link UDPSlaveTerminal}.
     *
     * @return the port number as {@link int}.
     */
    public int getRemotePort() {
        return m_RemotePort;
    }// getDestinationPort

    /**
     * Sets the destination port of this
     * {@link UDPSlaveTerminal}.
     * The default is defined as {@link Modbus.DEFAULT_PORT}.
     *
     * @param port the port number as {@link int}.
     */
    public void setRemotePort(int port) {
        m_RemotePort = port;
    }// setPort

    /**
     * Returns the destination {@link InetAddress} of this
     * {@link UDPSlaveTerminal}.
     *
     * @return the destination address as {@link InetAddress}.
     */
    public InetAddress getRemoteAddress() {
        return m_RemoteAddress;
    }// getAddress

    /**
     * Sets the destination {@link InetAddress} of this
     * {@link UDPSlaveTerminal}.
     *
     * @param adr the destination address as {@link InetAddress}.
     */
    public void setRemoteAddress(InetAddress adr) {
        m_RemoteAddress = adr;
    }// setAddress

    /**
     * Tests if this {@link UDPSlaveTerminal} is active.
     *
     * @return true if active, false otherwise.
     */
    @Override
    public boolean isActive() {
        return m_Active;
    }// isActive

    /**
     * Activate this {@link UDPTerminal}.
     *
     * @throws Exception if there is a network failure.
     */
    @Override
    public synchronized void activate() throws Exception {
        if (!isActive()) {
            logger.debug("UDPMasterTerminal::activate()::laddr=:{}:lport={}", m_LocalAddress.toString(), m_LocalPort);

            if (m_Socket == null) {
                if (m_LocalAddress != null && m_LocalPort != -1) {
                    m_Socket = new DatagramSocket(m_LocalPort, m_LocalAddress);
                } else {
                    m_Socket = new DatagramSocket();
                    m_LocalPort = m_Socket.getLocalPort();
                    m_LocalAddress = m_Socket.getLocalAddress();
                }
            }
            logger.debug("UDPMasterTerminal::haveSocket():{}", m_Socket.toString());
            logger.debug("UDPMasterTerminal::laddr=:{}:lport={}", m_LocalAddress.toString(), m_LocalPort);
            logger.debug("UDPMasterTerminal::raddr=:{}:rport={}", m_RemoteAddress.toString(), m_RemotePort);

            m_Socket.setReceiveBufferSize(1024);
            m_Socket.setSendBufferSize(1024);
            //m_Socket.setBroadcast(true);

            m_ModbusTransport = new ModbusUDPTransport(this);
            m_Active = true;
        }
        logger.info("UDPMasterTerminal::activated");
    }// activate

    /**
     * Deactivates this {@link UDPSlaveTerminal}.
     */
    @Override
    public void deactivate() {
        try {
            logger.debug("UDPMasterTerminal::deactivate()");
            // close socket
            m_Socket.close();
            m_ModbusTransport = null;
            m_Active = false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }// deactivate

    /**
     * Returns the {@link ModbusTransport} associated with this
     * {@link TCPMasterConnection}.
     *
     * @return the connection's {@link ModbusTransport}.
     */
    @Override
    public ModbusUDPTransport getModbusTransport() {
        return m_ModbusTransport;
    }// getModbusTransport

    /**
     * Returns the timeout for this {@link UDPMasterTerminal}.
     *
     * @return the timeout as {@link int}.
     */
    public int getTimeout() {
        return m_Timeout;
    }// getReceiveTimeout

    /**
     * Sets the timeout for this {@link UDPMasterTerminal}.
     *
     * @param timeout the timeout as {@link int}.
     */
    public void setTimeout(int timeout) {
        m_Timeout = timeout;
    }// setReceiveTimeout

    @Override
    public void sendMessage(byte[] msg) throws Exception {
    	
    	byte[] localIp = m_LocalAddress.getAddress();
    	byte[] fullMessage = new byte[msg.length + 16];
    	fullMessage[0] = localIp[0];
    	fullMessage[1] = localIp[1];
    	fullMessage[2] = localIp[2];
    	fullMessage[3] = localIp[3];

    	fullMessage[4]=0x53; //S
    	fullMessage[5]=0x4D; //M
    	fullMessage[6]=0x41; //A
    	fullMessage[7]=0x52; //R
    	fullMessage[8]=0x54; //T
    	fullMessage[9]=0x43; //C
    	fullMessage[10]=0x4C; //L
    	fullMessage[11]=0x4F; //O
    	fullMessage[12]=0x55; //U
    	fullMessage[13]=0x44; //D
    	fullMessage[14]=(byte) 0xAA; //
    	fullMessage[15]=(byte) 0xAA; //
    	System.arraycopy(msg,0,fullMessage,16,msg.length);
    	
        DatagramPacket req = new DatagramPacket(fullMessage, fullMessage.length, m_RemoteAddress, m_RemotePort);
        synchronized (m_Socket) {
            m_Socket.send(req);
        }
    }// sendPackage

    @Override
    public byte[] receiveMessage() throws Exception {

        // 1. Prepare buffer and receive package
        byte[] buffer = new byte[256];// max size
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        synchronized (m_Socket) {
            m_Socket.setSoTimeout(m_Timeout);
            m_Socket.receive(packet);
        }
        return buffer;
    }// receiveMessage

    public void receiveMessage(byte[] buffer) throws Exception {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        m_Socket.setSoTimeout(m_Timeout);
        m_Socket.receive(packet);
    }// receiveMessage
    
}// class UDPMasterTerminal
