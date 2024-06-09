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
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Hashtable;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.ciprianpascu.sbus.Modbus;
import ro.ciprianpascu.sbus.io.ModbusTransport;
import ro.ciprianpascu.sbus.io.ModbusUDPTransport;
import ro.ciprianpascu.sbus.io.ModbusUDPTransportFactory;
import ro.ciprianpascu.sbus.util.LinkedQueue;
import ro.ciprianpascu.sbus.util.ModbusUtil;

/**
 * Class implementing a {@link UDPSlaveTerminal}.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu

 * @version %I% (%G%)
 */
public class UDPSlaveTerminal implements UDPTerminal {
    public static class ModbusUDPTransportFactoryImpl implements ModbusUDPTransportFactory {

        @Override
        public ModbusTransport create(UDPTerminal terminal) {
            return new ModbusUDPTransport(terminal);
        }

    }

    private static final Logger logger = LoggerFactory.getLogger(UDPSlaveTerminal.class);
    public static final int DEFAULT_DEACTIVATION_WAIT_MILLIS = 100;

    // instance attributes
    private DatagramSocket m_Socket;
    private int m_Timeout = Modbus.DEFAULT_TIMEOUT;
    private boolean m_Active;
    protected InetAddress m_LocalAddress;
    private int m_LocalPort = Modbus.DEFAULT_PORT;
    protected InetAddress m_RemoteAddress;
    private int m_RemotePort = Modbus.DEFAULT_PORT;
    protected ModbusTransport m_ModbusTransport;

    private LinkedQueue m_SendQueue;
    protected LinkedQueue m_ReceiveQueue;
    private PacketSender m_PacketSender;
    private PacketReceiver m_PacketReceiver;
    private Thread m_Receiver;
    private Thread m_Sender;

    private boolean m_listenerMode;
    protected Hashtable m_Requests;
    
	byte[] smartCloud = new byte[] {'S', 'M', 'A', 'R', 'T', 'C', 'L', 'O', 'U', 'D', (byte)0xAA, (byte)0xAA};

    private ModbusUDPTransportFactory m_TransportFactory;
    /**
     * Time to wait for Threads to close when deactivate is called.
     * Note that often no time is enough since threads might be waiting for data.
     */
    private int m_DeactivationWaitMillis = 100;

    public UDPSlaveTerminal() {
        this(null, true);
    }// constructor

    public UDPSlaveTerminal(boolean withResponse) {
        this(null, withResponse);
    }// constructor

    public UDPSlaveTerminal(InetAddress localaddress, boolean withResponse) {
        this(localaddress, new ModbusUDPTransportFactoryImpl(), DEFAULT_DEACTIVATION_WAIT_MILLIS, withResponse);
    }

    public UDPSlaveTerminal(InetAddress localaddress, ModbusUDPTransportFactory transportFactory,
            int deactivationWaitMillis, boolean withResponse) {
        m_LocalAddress = localaddress;
        m_TransportFactory = transportFactory;
        m_DeactivationWaitMillis = deactivationWaitMillis;
        m_SendQueue = new LinkedQueue();
        m_ReceiveQueue = new LinkedQueue();
        // m_Requests = new Hashtable(342, 0.75F);
        m_Requests = new Hashtable(342);
		m_listenerMode = withResponse;

    }// constructor

    @Override
    public InetAddress getLocalAddress() {
        return m_LocalAddress;
    }// getLocalAddress

    @Override
    public int getLocalPort() {
        return m_LocalPort;
    }// getLocalPort

    public void setLocalPort(int port) {
        m_LocalPort = port;
    }// setLocalPort

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
            logger.debug("UDPSlaveTerminal::activate()");
            if (m_Socket == null) {
                if (m_LocalAddress != null && m_LocalPort != -1) {
                    m_Socket = new DatagramSocket(m_LocalPort, m_LocalAddress);
                } else if (m_LocalPort != -1) {
                    m_Socket = new DatagramSocket(m_LocalPort);
                    m_LocalAddress = m_Socket.getLocalAddress();
                } else {
                    m_Socket = new DatagramSocket();
                    m_LocalPort = m_Socket.getLocalPort();
                    m_LocalAddress = m_Socket.getLocalAddress();
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("UDPSlaveTerminal::haveSocket():{}", m_Socket.toString());
                logger.debug("UDPSlaveTerminal::addr=:{}:port={}", m_LocalAddress.toString(), m_LocalPort);
            }

            m_Socket.setReceiveBufferSize(1024);
            m_Socket.setSendBufferSize(1024);
            m_Socket.setBroadcast(true);
            m_PacketReceiver = new PacketReceiver();
            m_Receiver = new Thread(m_PacketReceiver);
            m_Receiver.start();
            logger.debug("UDPSlaveTerminal::receiver started()");
            m_PacketSender = new PacketSender();
            m_Sender = new Thread(m_PacketSender);
            m_Sender.start();
            logger.debug("UDPSlaveTerminal::sender started()");
            m_ModbusTransport = m_TransportFactory.create(this);
            logger.debug("UDPSlaveTerminal::transport created");
            m_Active = true;
        }
        logger.info("UDPSlaveTerminal::activated");
    }// activate

    /**
     * Deactivates this {@link UDPSlaveTerminal}.
     */
    @Override
    public void deactivate() {
        try {
            if (m_Active) {
                // 1. stop receiver
                m_PacketReceiver.stop();
                m_Receiver.join(m_DeactivationWaitMillis);
                m_Receiver.interrupt();
                // 2. stop sender gracefully
                m_PacketSender.stop();
                m_Sender.join(m_DeactivationWaitMillis);
                m_Sender.interrupt();
                // 3. close socket
                m_Socket.close();
                m_ModbusTransport = null;
                m_Active = false;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }// deactivate

    /**
     * Returns the {@link ModbusTransport} associated with this
     * {@link TCPMasterConnection}.
     *
     * @return the connection's {@link ModbusTransport}.
     */
    @Override
    public ModbusTransport getModbusTransport() {
        return m_ModbusTransport;
    }// getModbusTransport

    protected boolean hasResponse() {
        return !m_ReceiveQueue.isEmpty();
    }// hasResponse

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
        try {
			m_Socket.setSoTimeout(m_Timeout);
		} catch (SocketException e) {
			logger.error(e.getMessage(), e);
		}
    }// setReceiveTimeout
    /**
     * Returns the socket of this {@link UDPSlaveTerminal}.
     *
     * @return the socket as {@link DatagramSocket}.
     */
    public DatagramSocket getSocket() {
        return m_Socket;
    }// getSocket

    /**
     * Sets the socket of this {@link UDPTerminal}.
     *
     * @param sock the {@link DatagramSocket} for this terminal.
     */
    protected void setSocket(DatagramSocket sock) {
        m_Socket = sock;
    }// setSocket

    @Override
    public void sendMessage(byte[] msg) throws Exception {
    	byte[] localIp = m_LocalAddress.getAddress();
    	byte[] fullMessage = new byte[msg.length + 16];
    	
    	System.arraycopy(localIp,0,fullMessage,0,localIp.length);
    	System.arraycopy(smartCloud,0,fullMessage,4,smartCloud.length);
    	System.arraycopy(msg,0,fullMessage,16,msg.length);
        m_SendQueue.put(fullMessage);
    }// sendPackage

    @Override
    public byte[] receiveMessage() throws Exception {
        byte[] message = (byte[]) (m_listenerMode ? m_ReceiveQueue.take() : m_ReceiveQueue.poll(m_Timeout));
        byte[] signature = new byte[Math.min(message.length-4, smartCloud.length)]; //skip source IP from the message (first 4 bites)
        System.arraycopy(message,4,signature,0,signature.length);
        int equal = Arrays.compare(signature, smartCloud);
        if(equal != 0)
        	return new byte[0];
        return Arrays.copyOfRange(message, signature.length+4, message.length);
    }// receiveMessage

    class PacketSender implements Runnable {

        private boolean m_Continue;

        public PacketSender() {
            m_Continue = true;
        }// constructor

        @Override
        public void run() {
            do {
                try {
                	DatagramPacket res;
                    // 1. pickup the message and corresponding request
                    byte[] message = (byte[]) m_SendQueue.take();
                	if(m_listenerMode) {
	                    DatagramPacket req =  (DatagramPacket) m_Requests
	                            .remove(ModbusUtil.registersToInt(message));
	                    // 2. create new Package with corresponding address and port
	                    res = new DatagramPacket(message, message.length, req.getAddress(), req.getPort());
                	} else {
	                    // 2. create new Package with corresponding address and port
                		res = new DatagramPacket(message, message.length, m_RemoteAddress, m_RemotePort);
                	}
                    m_Socket.send(res);
                    logger.trace("Sent package from queue");
                } catch (Exception ex) {
                	if(logger.isDebugEnabled())
                        logger.debug("Exception", ex);
                }
            } while (m_Continue || !m_SendQueue.isEmpty());
        }// run

        public void stop() {
            m_Continue = false;
        }// stop

    }// PacketSender

    class PacketReceiver implements Runnable {

        private boolean m_Continue;

        public PacketReceiver() {
            m_Continue = true;
        }// constructor

        @Override
        public void run() {
            do {
                try {
                    // 1. Prepare buffer and receive package
                    byte[] buffer = new byte[256];// max size
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    m_Socket.receive(packet);
                    // 2. Extract TID and remember request
                    Integer tid = new Integer(ModbusUtil.registersToInt(buffer));
                    if(m_listenerMode)
                    	m_Requests.put(tid, packet);
                    // 3. place the data buffer in the queue
                    byte[] fullMessage = new byte[packet.getLength()];
                    System.arraycopy(buffer,0,fullMessage,0,packet.getLength());
                    m_ReceiveQueue.put(fullMessage);
                    logger.trace("Received package placed in queue");
                } catch (Exception ex) {
                	if(logger.isDebugEnabled())
                     logger.debug("Exception", ex);
                }
            } while (m_Continue);
        }// run

        public void stop() {
            m_Continue = false;
        }// stop

    }// PacketReceiver

	@Override
	public boolean hasMessage() {
		return !m_ReceiveQueue.isEmpty();
	}

}// class UDPTerminal
