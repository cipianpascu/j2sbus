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

import ro.ciprianpascu.sbus.Modbus;
import ro.ciprianpascu.sbus.ModbusIOException;
import ro.ciprianpascu.sbus.msg.IllegalFunctionRequest;
import ro.ciprianpascu.sbus.msg.ModbusMessage;
import ro.ciprianpascu.sbus.msg.ModbusRequest;
import ro.ciprianpascu.sbus.msg.ModbusResponse;
import ro.ciprianpascu.sbus.net.UDPTerminal;
import ro.ciprianpascu.sbus.util.ModbusUtil;

/**
 * Class that implements the Modbus UDP transport
 * flavor.
 * 
 * @author Dieter Wimberger
 * @author Ciprian Pascu

 * @version 1.0 (29/04/2002)
 */
public class ModbusUDPTransport implements ModbusTransport {
	
	private static final Logger logger = LoggerFactory.getLogger(ModbusUDPTransport.class);

    // instance attributes
    private UDPTerminal m_Terminal;
    private BytesOutputStream m_ByteOut;
    private BytesInputStream m_ByteIn;
    private ExpiringCache<ModbusResponse> messages;

    /**
     * Constructs a new {@link ModbusTransport} instance,
     * for a given {@link UDPTerminal}.
* 
     *
     * @param terminal the {@link UDPTerminal} used for message transport.
     */
    public ModbusUDPTransport(UDPTerminal terminal) {
        m_Terminal = terminal;
        m_ByteOut = new BytesOutputStream(Modbus.MAX_MESSAGE_LENGTH);
        m_ByteIn = new BytesInputStream(Modbus.MAX_MESSAGE_LENGTH);
        messages = new ExpiringCache<ModbusResponse>();
    }// constructor

    @Override
    public void close() throws IOException {
        // ?
    }// close

    @Override
    public void writeMessage(ModbusMessage msg) throws ModbusIOException {
        try {
        	cacheResponses();
        	ModbusResponse cachedMessage = messages.get("" + msg.getSubnetID() + "_" + msg.getUnitID() + "_" + msg.getFunctionCode());
        	if(cachedMessage != null) // already have recent information in the cache
        		return;
            synchronized (m_ByteOut) {
                m_ByteOut.reset();
                msg.writeTo(m_ByteOut);
                byte[] crc = ModbusUtil.calculateCRC(m_ByteOut.getBuffer(), m_ByteOut.size());
                m_ByteOut.writeByte(crc[0]);
                m_ByteOut.writeByte(crc[1]);
                m_Terminal.sendMessage(m_ByteOut.toByteArray());
            }
        } catch (Exception ex) {
            throw new ModbusIOException("I/O exception - failed to write.");
        }
    }// write

    @Override
    public ModbusRequest readRequest() throws ModbusIOException {
        try {
            ModbusRequest req = null;
            synchronized (m_ByteIn) {
                m_ByteIn.reset(m_Terminal.receiveMessage());
                
                // check CRC
                byte[] data = new byte[m_ByteIn.available()];
				int dlength = m_ByteIn.read(data);
				if(dlength <= 0) {
					logger.debug("No data received. Message not targeted for me.");
					return null;
				}
                if (!ModbusUtil.checkCRC(data, dlength-2)) { 
                    throw new IOException("CRC Error in received frame: " + dlength + " bytes: "
                            + ModbusUtil.toHex(m_ByteIn.getBuffer(), 0, dlength));
                }
                m_ByteIn.reset();
                
                // continue with request
                int length = m_ByteIn.readUnsignedByte();
                int subnetID = m_ByteIn.readUnsignedByte();
				int unitID = m_ByteIn.readUnsignedByte();
				int deviceType = m_ByteIn.readUnsignedShort();
                int functionCode = m_ByteIn.readUnsignedShort();
                m_ByteIn.reset();
                req = ModbusRequest.createModbusRequest(functionCode);
                req.readFrom(m_ByteIn);
            }
            return req;
        } catch (InterruptedIOException ioex) {
            throw new ModbusIOException("Socket timed out. " + ioex.getMessage());
        } catch (Exception ex) {
            throw new ModbusIOException("I/O exception - failed to read. " + ex.getMessage());
        }
    }// readRequest

    @Override
    public ModbusResponse readResponse(String transactionId) throws ModbusIOException {

        try {
        	cacheResponses();
            ModbusResponse res =  messages.get(transactionId);
        	if(res != null)
        		return res;
            synchronized (m_ByteIn) {
                m_ByteIn.reset(m_Terminal.receiveMessage());
                
                // check CRC
                byte[] data = new byte[m_ByteIn.available()];
				int dlength = m_ByteIn.read(data);
                if (!ModbusUtil.checkCRC(data, dlength-2)) {
                    throw new IOException("CRC Error in received frame: " + dlength + " bytes: "
                            + ModbusUtil.toHex(m_ByteIn.getBuffer(), 0, dlength));
                }
                m_ByteIn.reset();
                
                int length = m_ByteIn.readUnsignedByte();
                int subnetID = m_ByteIn.readUnsignedByte();
				int unitID = m_ByteIn.readUnsignedByte();
				int deviceType = m_ByteIn.readUnsignedShort();
                int functionCode = m_ByteIn.readUnsignedShort();
                m_ByteIn.reset();
                res = ModbusResponse.createModbusResponse(functionCode);
                res.readFrom(m_ByteIn);
            }
            return res;
        } catch (InterruptedIOException ioex) {
            throw new ModbusIOException("Socket timed out. " + ioex.getMessage());
        } catch (Exception ex) {
            // ex.printStackTrace();
            throw new ModbusIOException("I/O exception - failed to read. " + ex.getMessage());
        }
    }// readResponse

	private void cacheResponses() throws ModbusIOException {
		try {
			while (!m_Terminal.hasMessage()) {
				ModbusResponse res = null;
				synchronized (m_ByteIn) {
					m_ByteIn.reset(m_Terminal.receiveMessage());

					// check CRC
					byte[] data = new byte[m_ByteIn.available()];
					int dlength = m_ByteIn.read(data);
					if (!ModbusUtil.checkCRC(data, dlength - 2)) {
						logger.warn("CRC Error in received frame: " + dlength + " bytes: "
								+ ModbusUtil.toHex(m_ByteIn.getBuffer(), 0, dlength));
					}
					m_ByteIn.reset();

					int length = m_ByteIn.readUnsignedByte();
					int subnetID = m_ByteIn.readUnsignedByte();
					int unitID = m_ByteIn.readUnsignedByte();
					int deviceType = m_ByteIn.readUnsignedShort();
					int functionCode = m_ByteIn.readUnsignedShort();
					m_ByteIn.reset();
					res = ModbusResponse.createModbusResponse(functionCode);
					res.readFrom(m_ByteIn);
					messages.put(subnetID + "_" + unitID + "_" + functionCode, res);
				}
			}
		} catch (InterruptedIOException ioex) {
			throw new ModbusIOException("Socket timed out. " + ioex.getMessage());
		} catch (Exception ex) {
			// ex.printStackTrace();
			throw new ModbusIOException("I/O exception - failed to read. " + ex.getMessage());
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

}// class ModbusUDPTransport
