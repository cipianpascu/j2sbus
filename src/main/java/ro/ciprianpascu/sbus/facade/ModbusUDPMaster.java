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
 * Modbus/UDP Master facade.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu

 * @version %I% (%G%)
 */
public class ModbusUDPMaster {

    private UDPMasterConnection m_Connection;
    private ModbusUDPTransaction m_Transaction;
    private ReadStatusChannelsRequest m_ReadStatusChannelRequest;
    private WriteSingleChannelRequest m_WriteSingleChannelRequest;

    /**
     * Constructs a new master facade instance for communication
     * with a given slave.
     *
     */
    public ModbusUDPMaster() {
        m_Connection = new UDPMasterConnection();
        m_ReadStatusChannelRequest = new ReadStatusChannelsRequest();
        m_WriteSingleChannelRequest = new WriteSingleChannelRequest();
    }// constructor

    /**
     * Constructs a new master facade instance for communication
     * with a given slave.
     *
     * @param port the port the slave is listening to.
     */
    public ModbusUDPMaster( int port) {
        m_Connection.setPort(port);
    }// constructor

    /**
     * Connects this {@link ModbusUDPMaster} with the slave.
     *
     * @throws Exception if the connection cannot be established.
     */
    public void connect() throws Exception {
        if (m_Connection != null && !m_Connection.isConnected()) {
            m_Connection.connect();
            m_Transaction = new ModbusUDPTransaction(m_Connection);
        }
    }// connect

    /**
     * Disconnects this {@link ModbusUDPMaster} from the slave.
     */
    public void disconnect() {
        if (m_Connection != null && m_Connection.isConnected()) {
            m_Connection.close();
            m_Transaction = null;
        }
    }// disconnect


    /**
     * Reads a given number of input registers from the slave.
     * 
     * Note that the number of input registers returned (i.e. array length)
     * will be according to the number received in the slave response.
     *
     * @return a {@link InputRegister[]} with the received input registers.
     * @throws ModbusException if an I/O error, a slave exception or
     *             a transaction error occurs.
     */
    public synchronized InputRegister[] readInputRegisters() throws ModbusException {
        m_Transaction.setRequest(m_ReadStatusChannelRequest);
        m_Transaction.execute();
        return ((ReadStatusChannelsResponse) m_Transaction.getResponse()).getRegisters();
    }// readInputRegisters



    /**
     * Writes a single channel to the slave.
     *
     * @param ref the offset of the register to be written.
     * @param register a {@link Register} holding the value of the register
     *            to be written.
     * @throws ModbusException if an I/O error, a slave exception or
     *             a transaction error occurs.
     */
    public synchronized void writeSingleRegister(int ref, Register value) throws ModbusException {
        m_WriteSingleChannelRequest.setChannelNo(ref);
        Register[] registers = new Register[2];
		registers[0] = value;
		registers[1] = new WordRegister((short)0);
		m_WriteSingleChannelRequest.setRegisters(registers);
		m_Transaction.setRequest(m_WriteSingleChannelRequest);
		m_Transaction.execute();
	}// writeSingleRegister


}// class ModbusUDPMaster
