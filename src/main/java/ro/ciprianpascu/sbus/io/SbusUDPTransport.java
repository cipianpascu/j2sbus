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
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.ciprianpascu.sbus.Sbus;
import ro.ciprianpascu.sbus.SbusIOException;
import ro.ciprianpascu.sbus.msg.SbusMessage;
import ro.ciprianpascu.sbus.msg.SbusRequest;
import ro.ciprianpascu.sbus.msg.SbusResponse;
import ro.ciprianpascu.sbus.net.UDPTerminal;
import ro.ciprianpascu.sbus.util.SbusUtil;

/**
 * Class that implements the Sbus UDP transport
 * flavor.
 * 
 * @author Dieter Wimberger
 * @author Ciprian Pascu

 * @version 1.0 (29/04/2002)
 */
public class SbusUDPTransport implements SbusTransport {
	
	private static final Logger logger = LoggerFactory.getLogger(SbusUDPTransport.class);

    // instance attributes
    private UDPTerminal m_Terminal;
    private BytesOutputStream m_ByteOut;
    private BytesInputStream m_ByteIn;
    private ExpiringCache<SbusResponse> messages;

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
    }// constructor

    @Override
    public void close() throws IOException {
        // ?
    }// close

    @Override
    public void writeMessage(SbusMessage msg) throws SbusIOException {
        try {
        	cacheResponses();
        	SbusResponse cachedMessage = messages.get("" + msg.getSubnetID() + "_" + msg.getUnitID() + "_" + msg.getFunctionCode());
        	if(cachedMessage != null) // already have recent information in the cache
        		return;
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
				if(dlength <= 0) {
					logger.debug("No data received. Message not targeted for me.");
					return null;
				}
                if (!SbusUtil.checkCRC(data, dlength-2)) { 
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

        try {
        	cacheResponses();
            SbusResponse res =  messages.get(transactionId);
        	if(res != null)
        		return res;
            synchronized (m_ByteIn) {
                m_ByteIn.reset(m_Terminal.receiveMessage());
                
                // check CRC
                byte[] data = new byte[m_ByteIn.available()];
				int dlength = m_ByteIn.read(data);
                if (!SbusUtil.checkCRC(data, dlength-2)) {
                    throw new IOException("CRC Error in received frame: " + dlength + " bytes: "
                            + SbusUtil.toHex(m_ByteIn.getBuffer(), 0, dlength));
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
            }
            return res;
        } catch (InterruptedIOException ioex) {
            throw new SbusIOException("Socket timed out. " + ioex.getMessage());
        } catch (SbusIOException mioex) {
            throw mioex;
        } catch (Exception ex) {
            // ex.printStackTrace();
            throw new SbusIOException("I/O exception - failed to read. " + ex.getMessage());
        }
    }// readResponse

	private void cacheResponses() throws SbusIOException {
		try {
			while (m_Terminal.hasMessage()) {
				SbusResponse res = null;
				synchronized (m_ByteIn) {
					m_ByteIn.reset(m_Terminal.receiveMessage());

					// check CRC
					byte[] data = new byte[m_ByteIn.available()];
					int dlength = m_ByteIn.read(data);
					if (!SbusUtil.checkCRC(data, dlength - 2)) {
						logger.warn("CRC Error in received frame: " + dlength + " bytes: "
								+ SbusUtil.toHex(m_ByteIn.getBuffer(), 0, dlength));
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
					messages.put(subnetID + "_" + unitID + "_" + functionCode, res);
				}
			}
		} catch (InterruptedIOException ioex) {
			throw new SbusIOException("Socket timed out. " + ioex.getMessage());
		} catch (Exception ex) {
			// ex.printStackTrace();
			throw new SbusIOException("I/O exception - failed to read. " + ex.getMessage());
		}
	}
   
    /**
     * This class was taken from java.io internal package
     */
    static class ExpiringCache<T> {
        private long millisUntilExpiration;
        private Map<String,Entry> map;
        // Clear out old entries every few queries
        private int queryCount;
        private int queryOverflow = 300;
        private int MAX_ENTRIES = 200;

        class Entry {
            private long   timestamp;
            private T val;

            Entry(long timestamp, T val) {
                this.timestamp = timestamp;
                this.val = val;
            }

            long   timestamp()                  { return timestamp;           }
            void   setTimestamp(long timestamp) { this.timestamp = timestamp; }

            T val()                        { return val;                 }
            void   setVal(T val)           { this.val = val;             }
        }

        ExpiringCache() {
            this(30000);
        }

        @SuppressWarnings("serial")
        ExpiringCache(long millisUntilExpiration) {
            this.millisUntilExpiration = millisUntilExpiration;
            map = new LinkedHashMap<>() {
                protected boolean removeEldestEntry(Map.Entry<String,Entry> eldest) {
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
            for (String key: keySet) {
                keys[i++] = key;
            }
            for (int j = 0; j < keys.length; j++) {
                entryFor(keys[j]);
            }
            queryCount = 0;
        }
    }

}// class SbusUDPTransport
