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

package ro.ciprianpascu.sbus.io;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.ciprianpascu.sbus.Sbus;
import ro.ciprianpascu.sbus.SbusIOException;
import ro.ciprianpascu.sbus.msg.ExceptionResponse;
import ro.ciprianpascu.sbus.msg.SbusMessage;
import ro.ciprianpascu.sbus.msg.SbusRequest;
import ro.ciprianpascu.sbus.msg.SbusResponse;
import ro.ciprianpascu.sbus.net.SbusMessageListener;
import ro.ciprianpascu.sbus.net.UDPSlaveTerminal;
import ro.ciprianpascu.sbus.net.UDPTerminal;
import ro.ciprianpascu.sbus.util.SbusUtil;

/**
 * Class that implements the Sbus UDP transport
 * flavor with notification-driven cache population.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu
 *
 * @version 1.0 (29/04/2002)
 */
public class SbusUDPTransport implements SbusTransport, UDPSlaveTerminal.MessageArrivalCallback {

    private static final Logger logger = LoggerFactory.getLogger(SbusUDPTransport.class);

    // instance attributes
    private UDPTerminal m_Terminal;
    private BytesOutputStream m_ByteOut;
    private BytesInputStream m_ByteIn;
    private ExpiringCache<SbusResponse> messages;

    // Listener coordination attributes
    private final Set<String> pendingTransactions = ConcurrentHashMap.newKeySet();
    private final List<SbusMessageListener> messageListeners = new CopyOnWriteArrayList<>();

    /**
     * Constructs a new {@link SbusTransport} instance,
     * for a given {@link UDPTerminal}.
     *
     *
     * @param terminal the {@link UDPTerminal} used for message transport.
     */
    public SbusUDPTransport(UDPTerminal terminal) {
        m_Terminal = terminal;
        m_ByteOut = new BytesOutputStream(Sbus.MAX_MESSAGE_LENGTH);
        m_ByteIn = new BytesInputStream(Sbus.MAX_MESSAGE_LENGTH);
        messages = new ExpiringCache<SbusResponse>();

        // Set up notification-driven cache population
        if (terminal instanceof UDPSlaveTerminal) {
            ((UDPSlaveTerminal) terminal).setMessageArrivalCallback(this);
        }
    }// constructor

    @Override
    public void close() throws IOException {
        // Clear all coordination data on close
        pendingTransactions.clear();
        messageListeners.clear();

        // Remove callback
        if (m_Terminal instanceof UDPSlaveTerminal) {
            ((UDPSlaveTerminal) m_Terminal).setMessageArrivalCallback(null);
        }
    }// close

    /**
     * Callback method called when a message arrives in the terminal's receive queue.
     * This enables notification-driven cache population for non-blocking operation.
     * Processes all available messages in the receive queue without blocking.
     * This is called from the message arrival callback.
     */
    @Override
    public void onMessageArrived() {
        if (!(m_Terminal instanceof UDPSlaveTerminal)) {
            return;
        }

        UDPSlaveTerminal slaveTerminal = (UDPSlaveTerminal) m_Terminal;

        try {
            byte[] messageBytes;
            while ((messageBytes = slaveTerminal.receiveMessageNonBlocking()) != null) {
                processMessage(messageBytes);
            }
        } catch (Exception ex) {
            logger.warn("Error processing available messages: " + ex.getMessage());
        }
    }

    /**
     * Processes a single message and routes it appropriately to cache and listeners.
     */
    private void processMessage(byte[] messageBytes) throws Exception {
        SbusResponse res = null;
        synchronized (m_ByteIn) {
            m_ByteIn.reset(messageBytes);

            // check CRC
            byte[] data = new byte[m_ByteIn.available()];
            int dlength = m_ByteIn.read(data);
            if (!SbusUtil.checkCRC(data, dlength - 2)) {
                logger.warn("CRC Error in received frame: " + dlength + " bytes: "
                        + SbusUtil.toHex(m_ByteIn.getBuffer(), 0, dlength));
                return; // Skip invalid messages
            }
            m_ByteIn.reset();

            int length = m_ByteIn.readUnsignedByte();
            int subnetID = m_ByteIn.readUnsignedByte();
            int unitID = m_ByteIn.readUnsignedByte();
            int deviceType = m_ByteIn.readUnsignedShort();
            int functionCode = m_ByteIn.readUnsignedShort();
            m_ByteIn.reset();
            res = SbusResponse.createSbusResponse(functionCode);
            res.readFrom(m_ByteIn);

            // Cache the response in ExpiringCache
            String responseTransactionId = extractTransactionId(res);
            // Route to appropriate handler
            if (pendingTransactions.contains(responseTransactionId)) {
                logger.debug("Routed response to pending (request/response) transaction via expiringCache: "
                        + responseTransactionId);
                messages.put(responseTransactionId, res);
            } else if (!(res instanceof ExceptionResponse)) {
                // No pending transactions, this is definitely unsolicited
                logger.debug("Routing unsolicited message to listeners: " + responseTransactionId);
                notifyListeners(res);
            }
        }
    }

    /**
     * Adds a message listener for unsolicited messages.
     *
     * @param listener the listener to add
     */
    @Override
    public void addMessageListener(SbusMessageListener listener) {
        if (listener != null) {
            messageListeners.add(listener);
        }
    }

    /**
     * Removes a message listener.
     *
     * @param listener the listener to remove
     */
    @Override
    public void removeMessageListener(SbusMessageListener listener) {
        messageListeners.remove(listener);
    }

    /**
     * Extracts the transaction ID from a response message.
     *
     * @param response the response message
     * @return the transaction ID
     */
    private String extractTransactionId(SbusResponse response) {
        // Use the request function code for transaction ID
        // Wire analysis: request=0xE3E7, response=0xE3E8, so response = request + 1
        int requestFunctionCode = response.getFunctionCode() - 1;
        // For responses, use sourceSubnetID and sourceUnitID to match the original request
        return response.getSourceSubnetID() + "_" + response.getSourceUnitID() + "_" + requestFunctionCode;
    }

    /**
     * Notifies all registered listeners about an unsolicited message.
     *
     * @param response the response to notify about
     */
    private void notifyListeners(SbusResponse response) {
        for (SbusMessageListener listener : messageListeners) {
            try {
                listener.onMessageReceived(response);
            } catch (Exception e) {
                logger.warn("Error in message listener", e);
                try {
                    listener.onError(e, null);
                } catch (Exception listenerError) {
                    logger.error("Error in listener error handler", listenerError);
                }
            }
        }
    }

    @Override
    public void writeMessage(SbusMessage msg) throws SbusIOException {
        try {
            SbusResponse cachedMessage = messages
                    .get("" + msg.getSubnetID() + "_" + msg.getUnitID() + "_" + msg.getFunctionCode());
            if (cachedMessage != null) { // already have recent information in the cache
                return;
            }
            synchronized (m_ByteOut) {
                m_ByteOut.reset();
                msg.writeTo(m_ByteOut);
                byte[] crc = SbusUtil.calculateCRC(m_ByteOut.getBuffer(), m_ByteOut.size());
                m_ByteOut.writeByte(crc[0]);
                m_ByteOut.writeByte(crc[1]);
                m_Terminal.sendMessage(m_ByteOut.toByteArray());
            }
        } catch (Exception ex) {
            throw new SbusIOException("I/O exception - failed to write.");
        }
    }// write

    @Override
    public SbusRequest readRequest() throws SbusIOException {
        try {
            SbusRequest req = null;
            synchronized (m_ByteIn) {
                m_ByteIn.reset(m_Terminal.receiveMessage());

                // check CRC
                byte[] data = new byte[m_ByteIn.available()];
                int dlength = m_ByteIn.read(data);
                if (dlength <= 0) {
                    logger.debug("No data received. Message not targeted for me.");
                    return null;
                }
                if (!SbusUtil.checkCRC(data, dlength - 2)) {
                    throw new IOException("CRC Error in received frame: " + dlength + " bytes: "
                            + SbusUtil.toHex(m_ByteIn.getBuffer(), 0, dlength));
                }
                m_ByteIn.reset();

                // continue with request
                int length = m_ByteIn.readUnsignedByte();
                int subnetID = m_ByteIn.readUnsignedByte();
                int unitID = m_ByteIn.readUnsignedByte();
                int deviceType = m_ByteIn.readUnsignedShort();
                int functionCode = m_ByteIn.readUnsignedShort();
                m_ByteIn.reset();
                req = SbusRequest.createSbusRequest(functionCode);
                req.readFrom(m_ByteIn);
            }
            return req;
        } catch (InterruptedIOException ioex) {
            throw new SbusIOException("Socket timed out. " + ioex.getMessage());
        } catch (Exception ex) {
            throw new SbusIOException("I/O exception - failed to read. " + ex.getMessage());
        }
    }// readRequest

    @Override
    public SbusResponse readResponse(String transactionId) throws SbusIOException {
        // Register that we're waiting for this transaction
        pendingTransactions.add(transactionId);

        try {
            // Block on cache with timeout - this will wait for notification-driven population
            SbusResponse res = messages.getWithTimeout(transactionId, m_Terminal.getTimeout());
            if (res != null) {
                logger.debug("Found response after waiting for transaction: " + transactionId);
                return res;
            }

            // Timeout occurred
            throw new SbusIOException("No response received for transaction: " + transactionId);
        } catch (SbusIOException mioex) {
            throw mioex;
        } catch (Exception ex) {
            // ex.printStackTrace();
            throw new SbusIOException("I/O exception - failed to read. " + ex.getMessage());
        } finally {
            // Always clean up pending transaction
            pendingTransactions.remove(transactionId);
        }

    }

    /**
     * This class was taken from java.io internal package
     */
    static class ExpiringCache<T> {
        private long millisUntilExpiration;
        private Map<String, Entry> map;
        // Clear out old entries every few queries
        private int queryCount;
        private int queryOverflow = 300;
        private int MAX_ENTRIES = 200;

        class Entry {
            private long timestamp;
            private T val;

            Entry(long timestamp, T val) {
                this.timestamp = timestamp;
                this.val = val;
            }

            long timestamp() {
                return timestamp;
            }

            void setTimestamp(long timestamp) {
                this.timestamp = timestamp;
            }

            T val() {
                return val;
            }

            void setVal(T val) {
                this.val = val;
            }
        }

        ExpiringCache() {
            this(30000);
        }

        @SuppressWarnings("serial")
        ExpiringCache(long millisUntilExpiration) {
            this.millisUntilExpiration = millisUntilExpiration;
            map = new LinkedHashMap<>() {
                protected boolean removeEldestEntry(Map.Entry<String, Entry> eldest) {
                    return size() > MAX_ENTRIES;
                }
            };
        }

        synchronized T get(String key) {
            if (++queryCount >= queryOverflow) {
                cleanup();
            }
            Entry entry = entryFor(key);
            if (entry != null) {
                return entry.val();
            }
            return null;
        }

        /**
         * Gets a value from the cache, blocking until it becomes available or timeout occurs.
         *
         * @param key the key to look for
         * @param timeoutMs timeout in milliseconds
         * @return the value if found within timeout, null if timeout occurs
         */
        synchronized T getWithTimeout(String key, long timeoutMs) {
            long startTime = System.currentTimeMillis();
            long endTime = startTime + timeoutMs;

            while (System.currentTimeMillis() < endTime) {
                if (++queryCount >= queryOverflow) {
                    cleanup();
                }
                Entry entry = entryFor(key);
                if (entry != null) {
                    return entry.val();
                }

                // Wait a short time before checking again
                try {
                    long remainingTime = endTime - System.currentTimeMillis();
                    if (remainingTime > 0) {
                        wait(Math.min(remainingTime, 100)); // Wait max 100ms at a time
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            return null; // Timeout occurred
        }

        synchronized void put(String key, T val) {
            if (++queryCount >= queryOverflow) {
                cleanup();
            }
            Entry entry = entryFor(key);
            if (entry != null) {
                entry.setTimestamp(System.currentTimeMillis());
                entry.setVal(val);
            } else {
                map.put(key, new Entry(System.currentTimeMillis(), val));
            }
            // Notify any threads waiting in getWithTimeout
            notifyAll();
        }

        synchronized void clear() {
            map.clear();
        }

        private Entry entryFor(String key) {
            Entry entry = map.get(key);
            if (entry != null) {
                long delta = System.currentTimeMillis() - entry.timestamp();
                if (delta < 0 || delta >= millisUntilExpiration) {
                    map.remove(key);
                    entry = null;
                }
            }
            return entry;
        }

        private void cleanup() {
            Set<String> keySet = map.keySet();
            // Avoid ConcurrentModificationExceptions
            String[] keys = new String[keySet.size()];
            int i = 0;
            for (String key : keySet) {
                keys[i++] = key;
            }
            for (int j = 0; j < keys.length; j++) {
                entryFor(keys[j]);
            }
            queryCount = 0;
        }
    }

}// class SbusUDPTransport
