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
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.ciprianpascu.sbus.Sbus;
import ro.ciprianpascu.sbus.SbusIOException;
import ro.ciprianpascu.sbus.io.SbusTransport;
import ro.ciprianpascu.sbus.io.SbusUDPTransport;
import ro.ciprianpascu.sbus.io.SbusUDPTransportFactory;
import ro.ciprianpascu.sbus.util.LinkedQueue;
import ro.ciprianpascu.sbus.util.SbusUtil;

/**
 * Class implementing a UDP slave terminal for the SBus protocol.
 * This terminal handles UDP communication for a slave device,
 * managing both sending and receiving of messages.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public class UDPSlaveTerminal implements UDPTerminal {

    /**
     * Implementation of SbusUDPTransportFactory for creating UDP transports.
     */
    public static class SbusUDPTransportFactoryImpl implements SbusUDPTransportFactory {
        @Override
        public SbusTransport create(UDPTerminal terminal) {
            return new SbusUDPTransport(terminal);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(UDPSlaveTerminal.class);

    /** Default wait time in milliseconds when deactivating the terminal */
    public static final int DEFAULT_DEACTIVATION_WAIT_MILLIS = 100;

    /** The UDP channel for communication */
    private DatagramChannel m_Channel;

    /** Timeout for operations in milliseconds */
    private int m_Timeout = Sbus.DEFAULT_TIMEOUT;

    /** Flag indicating if the terminal is active */
    private boolean m_Active;

    /** Local address for the terminal */
    protected InetAddress m_LocalAddress;

    /** Local port for the terminal */
    private int m_LocalPort = Sbus.DEFAULT_PORT;

    /** Remote address for communication */
    protected InetAddress m_RemoteAddress;

    /** Remote port for communication */
    private int m_RemotePort = Sbus.DEFAULT_PORT;

    /** Transport layer for SBus protocol */
    protected SbusTransport m_SbusTransport;

    /** Queue for outgoing messages */
    private LinkedQueue m_SendQueue;

    /** Queue for incoming messages */
    protected LinkedQueue m_ReceiveQueue;

    /** Handler for sending packets */
    private PacketSender m_PacketSender;

    /** Handler for receiving packets */
    private PacketReceiver m_PacketReceiver;

    /** Thread for receiving messages */
    private Thread m_Receiver;

    /** Thread for sending messages */
    private Thread m_Sender;

    /** Flag indicating if the terminal is in listener mode */
    private boolean m_listenerMode;

    /** Table for tracking requests */
    protected Hashtable m_Requests;

    /** Signature for SMARTCLOUD protocol */
    byte[] smartCloud = new byte[] { 'S', 'M', 'A', 'R', 'T', 'C', 'L', 'O', 'U', 'D', (byte) 0xAA, (byte) 0xAA };

    /** Factory for creating transport instances */
    private SbusUDPTransportFactory m_TransportFactory;

    /** Time to wait for threads to close during deactivation */
    private int m_DeactivationWaitMillis = 100;

    /**
     * Creates a new UDPSlaveTerminal with default settings and response enabled.
     */
    public UDPSlaveTerminal() {
        this(null, true);
    }

    /**
     * Creates a new UDPSlaveTerminal with specified response behavior.
     *
     * @param withResponse true to enable responses, false for fire-and-forget
     */
    public UDPSlaveTerminal(boolean withResponse) {
        this(null, withResponse);
    }

    /**
     * Creates a new UDPSlaveTerminal with specified local address and response behavior.
     *
     * @param localaddress the local address to bind to
     * @param withResponse true to enable responses, false for fire-and-forget
     */
    public UDPSlaveTerminal(InetAddress localaddress, boolean withResponse) {
        this(localaddress, new SbusUDPTransportFactoryImpl(), DEFAULT_DEACTIVATION_WAIT_MILLIS, withResponse);
    }

    /**
     * Creates a new UDPSlaveTerminal with full configuration options.
     *
     * @param localaddress the local address to bind to
     * @param transportFactory factory for creating transport instances
     * @param deactivationWaitMillis time to wait during deactivation
     * @param withResponse true to enable responses, false for fire-and-forget
     */
    public UDPSlaveTerminal(InetAddress localaddress, SbusUDPTransportFactory transportFactory,
            int deactivationWaitMillis, boolean withResponse) {
        m_LocalAddress = localaddress;
        m_TransportFactory = transportFactory;
        m_DeactivationWaitMillis = deactivationWaitMillis;
        m_SendQueue = new LinkedQueue();
        m_ReceiveQueue = new LinkedQueue();
        m_Requests = new Hashtable(342);
        m_listenerMode = withResponse;
    }

    @Override
    public InetAddress getLocalAddress() {
        return m_LocalAddress;
    }

    @Override
    public int getLocalPort() {
        return m_LocalPort;
    }

    /**
     * Sets the local port for this terminal.
     *
     * @param port the port number to set
     */
    public void setLocalPort(int port) {
        m_LocalPort = port;
    }

    /**
     * Sets the remote port for this terminal.
     * The default is 6000.
     *
     * @param port the port number to set
     */
    public void setRemotePort(int port) {
        m_RemotePort = port;
    }

    /**
     * Sets the remote address for this terminal.
     *
     * @param adr the address to set
     */
    public void setRemoteAddress(InetAddress adr) {
        m_RemoteAddress = adr;
    }

    @Override
    public boolean isActive() {
        return m_Active;
    }

    @Override
    public synchronized void activate() throws Exception {
        if (!isActive()) {
            logger.debug("UDPSlaveTerminal::activate()");
            if (m_Channel == null) {
                m_Channel = DatagramChannel.open(StandardProtocolFamily.INET);
                m_Channel.configureBlocking(true);
                m_Channel.bind(new InetSocketAddress(m_LocalPort));
                // m_LocalAddress = new InetSocketAddress(m_LocalPort).getAddress();
            }
            if (logger.isDebugEnabled()) {
                logger.debug("UDPSlaveTerminal::haveSocket():{}", m_Channel.toString());
                logger.debug("UDPSlaveTerminal::addr=:{}:port={}", m_LocalAddress.toString(), m_LocalPort);
            }

            m_Channel.setOption(StandardSocketOptions.SO_BROADCAST, true);
            m_Channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            m_PacketReceiver = new PacketReceiver();
            m_Receiver = new Thread(m_PacketReceiver);
            m_Receiver.setName("PacketReceiver");
            m_Receiver.start();
            logger.debug("UDPSlaveTerminal::receiver started()");
            m_PacketSender = new PacketSender();
            m_Sender = new Thread(m_PacketSender);
            m_Sender.setName("PacketSender");
            m_Sender.start();
            logger.debug("UDPSlaveTerminal::sender started()");
            m_SbusTransport = m_TransportFactory.create(this);
            logger.debug("UDPSlaveTerminal::transport created");
            m_Active = true;
        }
        logger.info("UDPSlaveTerminal::activated");
    }

    @Override
    public void deactivate() {
        try {
            if (m_Active) {
                m_PacketReceiver.stop();
                m_Receiver.join(m_DeactivationWaitMillis);
                m_Receiver.interrupt();
                m_PacketSender.stop();
                m_Sender.join(m_DeactivationWaitMillis);
                m_Sender.interrupt();
                m_Channel.close();
                m_SbusTransport = null;
                m_Active = false;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public SbusTransport getSbusTransport() {
        return m_SbusTransport;
    }

    /**
     * Checks if there is a response available in the receive queue.
     *
     * @return true if a response is available, false otherwise
     */
    protected boolean hasResponse() {
        return !m_ReceiveQueue.isEmpty();
    }

    /**
     * Gets the timeout value for operations.
     *
     * @return timeout in milliseconds
     */
    public int getTimeout() {
        return m_Timeout;
    }

    /**
     * Sets the timeout value for operations.
     *
     * @param timeout timeout in milliseconds
     */
    public void setTimeout(int timeout) {
        m_Timeout = timeout;
    }

    @Override
    public void sendMessage(byte[] msg) throws Exception {
        byte[] localIp = m_LocalAddress.getAddress();
        byte[] fullMessage = new byte[msg.length + 16];

        System.arraycopy(localIp, 0, fullMessage, 0, localIp.length);
        System.arraycopy(smartCloud, 0, fullMessage, 4, smartCloud.length);
        System.arraycopy(msg, 0, fullMessage, 16, msg.length);
        System.out.println("Sent     " + SbusUtil.toHex(fullMessage));
        m_SendQueue.put(fullMessage);
    }

    @Override
    public byte[] receiveMessage() throws Exception {
        byte[] message = (byte[]) (m_listenerMode ? m_ReceiveQueue.take() : m_ReceiveQueue.poll(m_Timeout));
        if (message == null) {
            throw new SbusIOException("No message response arrived in due time", true);
        }
        System.out.println("Received " + SbusUtil.toHex(message));
        byte[] signature = new byte[Math.min(message.length - 4, smartCloud.length)];
        System.arraycopy(message, 4, signature, 0, signature.length);
        int equal = Arrays.compare(signature, smartCloud);
        if (equal != 0) {
            throw new SbusIOException("Message not for me", true);
        }
        return Arrays.copyOfRange(message, signature.length + 4, message.length);
    }

    @Override
    public boolean hasMessage() {
        return !m_ReceiveQueue.isEmpty();
    }

    /**
     * Inner class handling the sending of packets.
     */
    class PacketSender implements Runnable {
        private boolean m_Continue;

        public PacketSender() {
            m_Continue = true;
        }

        @Override
        public void run() {
            do {
                try {
                    byte[] message = (byte[]) m_SendQueue.take();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    buffer.put(message);
                    buffer.flip();
                    int bytesSent = 0;
                    if (m_listenerMode) {
                        InetSocketAddress sourceAddress = (InetSocketAddress) ((Object[]) m_Requests
                                .remove(SbusUtil.registersToInt(message)))[0];
                        bytesSent = m_Channel.send(buffer, sourceAddress);
                    } else {
                        bytesSent = m_Channel.send(buffer, new InetSocketAddress(m_RemoteAddress, m_LocalPort));
                    }
                    logger.trace("Sent package from queue with length " + bytesSent);
                } catch (Exception ex) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Exception", ex);
                    }
                }
            } while (m_Continue || !m_SendQueue.isEmpty());
        }

        public void stop() {
            m_Continue = false;
        }
    }

    /**
     * Inner class handling the receiving of packets.
     */
    class PacketReceiver implements Runnable {
        private boolean m_Continue;

        public PacketReceiver() {
            m_Continue = true;
        }

        @Override
        public void run() {
            do {
                try {
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    InetSocketAddress sourceAddress = (InetSocketAddress) m_Channel.receive(buffer);
                    if (isSelfAddress(sourceAddress)) {
                        continue;
                    }
                    buffer.flip();
                    byte[] fullMessage = new byte[buffer.remaining()];
                    buffer.get(fullMessage);
                    Integer tid = new Integer(SbusUtil.registersToInt(fullMessage));
                    if (m_listenerMode) {
                        m_Requests.put(tid, new Object[] { sourceAddress, fullMessage });
                    }
                    m_ReceiveQueue.put(fullMessage);
                    logger.trace("Received package placed in queue");
                } catch (Exception ex) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Exception", ex);
                    }
                }
            } while (m_Continue);
        }

        // This method must figure out whether the sender is this process
        private boolean isSelfAddress(InetSocketAddress senderAddr) throws IOException {

            // If they're exactly the same socket address, skip it
            return senderAddr == null || m_LocalAddress.equals(senderAddr.getAddress());
        }

        public void stop() {
            m_Continue = false;
        }
    }
}
