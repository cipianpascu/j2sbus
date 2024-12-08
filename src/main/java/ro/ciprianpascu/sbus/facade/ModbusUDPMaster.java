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

package ro.ciprianpascu.sbus.facade;

import ro.ciprianpascu.sbus.ModbusException;
import ro.ciprianpascu.sbus.io.ModbusUDPTransaction;
import ro.ciprianpascu.sbus.msg.ReadStatusChannelsRequest;
import ro.ciprianpascu.sbus.msg.ReadStatusChannelsResponse;
import ro.ciprianpascu.sbus.msg.WriteSingleChannelRequest;
import ro.ciprianpascu.sbus.net.UDPMasterConnection;
import ro.ciprianpascu.sbus.procimg.InputRegister;
import ro.ciprianpascu.sbus.procimg.Register;
import ro.ciprianpascu.sbus.procimg.WordRegister;

/**
 * SBus UDP Master facade.
 * This class provides a high-level interface for communicating with SBus slaves
 * over UDP. It handles connection management and provides simplified methods
 * for reading and writing registers.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public class ModbusUDPMaster {

    /** The UDP connection to the slave */
    private UDPMasterConnection m_Connection;
    
    /** The transaction handler for sending/receiving messages */
    private ModbusUDPTransaction m_Transaction;
    
    /** Request for reading status channels */
    private ReadStatusChannelsRequest m_ReadStatusChannelRequest;
    
    /** Request for writing to a single channel */
    private WriteSingleChannelRequest m_WriteSingleChannelRequest;

    /**
     * Constructs a new master facade instance with default connection settings.
     */
    public ModbusUDPMaster() {
        m_Connection = new UDPMasterConnection();
        m_ReadStatusChannelRequest = new ReadStatusChannelsRequest();
        m_WriteSingleChannelRequest = new WriteSingleChannelRequest();
    }

    /**
     * Constructs a new master facade instance with a specific port.
     *
     * @param port the port number the slave is listening on
     */
    public ModbusUDPMaster(int port) {
        m_Connection = new UDPMasterConnection();
        m_Connection.setPort(port);
        m_ReadStatusChannelRequest = new ReadStatusChannelsRequest();
        m_WriteSingleChannelRequest = new WriteSingleChannelRequest();
    }

    /**
     * Connects this master to the slave.
     * This must be called before any read or write operations.
     *
     * @throws Exception if the connection cannot be established
     */
    public void connect() throws Exception {
        if (m_Connection != null && !m_Connection.isConnected()) {
            m_Connection.connect();
            m_Transaction = new ModbusUDPTransaction(m_Connection);
        }
    }

    /**
     * Disconnects this master from the slave.
     * This should be called when communication is complete.
     */
    public void disconnect() {
        if (m_Connection != null && m_Connection.isConnected()) {
            m_Connection.close();
            m_Transaction = null;
        }
    }

    /**
     * Reads the status of all input registers from the slave.
     * The number of registers returned will be according to the slave's response.
     *
     * @return array of input registers containing the current status values
     * @throws ModbusException if an I/O error, slave exception, or transaction error occurs
     */
    public synchronized InputRegister[] readInputRegisters() throws ModbusException {
        m_Transaction.setRequest(m_ReadStatusChannelRequest);
        m_Transaction.execute();
        return ((ReadStatusChannelsResponse) m_Transaction.getResponse()).getRegisters();
    }

    /**
     * Writes a value to a single channel on the slave.
     * The value is written to the specified channel number, with a default
     * timer value of 0.
     *
     * @param channelNo the channel number to write to
     * @param value the register containing the value to write
     * @throws ModbusException if an I/O error, slave exception, or transaction error occurs
     */
    public synchronized void writeSingleRegister(int channelNo, Register value) throws ModbusException {
        m_WriteSingleChannelRequest.setChannelNo(channelNo);
        Register[] registers = new Register[2];
        registers[0] = value;
        registers[1] = new WordRegister((short)0);
        m_WriteSingleChannelRequest.setRegisters(registers);
        m_Transaction.setRequest(m_WriteSingleChannelRequest);
        m_Transaction.execute();
    }
}
