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

import java.net.InetAddress;
import java.net.UnknownHostException;

import ro.ciprianpascu.sbus.ModbusException;
import ro.ciprianpascu.sbus.io.ModbusUDPTransaction;
import ro.ciprianpascu.sbus.msg.ReadStatusChannelsRequest;
import ro.ciprianpascu.sbus.msg.ReadStatusChannelsResponse;
import ro.ciprianpascu.sbus.msg.ReadMultipleRegistersRequest;
import ro.ciprianpascu.sbus.msg.ReadMultipleRegistersResponse;
import ro.ciprianpascu.sbus.msg.WriteMultipleRegistersRequest;
import ro.ciprianpascu.sbus.msg.WriteSingleChannelRequest;
import ro.ciprianpascu.sbus.net.UDPMasterConnection;
import ro.ciprianpascu.sbus.procimg.InputRegister;
import ro.ciprianpascu.sbus.procimg.Register;

/**
 * Modbus/UDP Master facade.
 *
 * @author Dieter Wimberger
 * @version %I% (%G%)
 */
public class ModbusUDPMaster {

    private UDPMasterConnection m_Connection;
    private InetAddress m_SlaveAddress;
    private ModbusUDPTransaction m_Transaction;
    private ReadStatusChannelsRequest m_ReadStatusChannelRequest;
    private ReadMultipleRegistersRequest m_ReadMultipleRegistersRequest;
    private WriteSingleChannelRequest m_WriteSingleChannelRequest;
    private WriteMultipleRegistersRequest m_WriteMultipleRegistersRequest;

    /**
     * Constructs a new master facade instance for communication
     * with a given slave.
     *
     * @param addr an internet address as resolvable IP name or IP number,
     *            specifying the slave to communicate with.
     */
    public ModbusUDPMaster(String addr) {
        try {
            m_SlaveAddress = InetAddress.getByName(addr);
            m_Connection = new UDPMasterConnection(m_SlaveAddress);
            m_ReadStatusChannelRequest = new ReadStatusChannelsRequest();
            m_ReadMultipleRegistersRequest = new ReadMultipleRegistersRequest();
            m_WriteSingleChannelRequest = new WriteSingleChannelRequest();
            m_WriteMultipleRegistersRequest = new WriteMultipleRegistersRequest();

        } catch (UnknownHostException e) {
            throw new RuntimeException(e.getMessage());
        }
    }// constructor

    /**
     * Constructs a new master facade instance for communication
     * with a given slave.
     *
     * @param addr an internet address as resolvable IP name or IP number,
     *            specifying the slave to communicate with.
     * @param port the port the slave is listening to.
     */
    public ModbusUDPMaster(String addr, int port) {
        this(addr);
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
     * Disconnects this {@link ModbusTCPMaster} from the slave.
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
     * @param ref the offset of the input register to start reading from.
     * @param count the number of input registers to be read.
     * @return a {@link InputRegister[]} with the received input registers.
     * @throws ModbusException if an I/O error, a slave exception or
     *             a transaction error occurs.
     */
    public synchronized InputRegister[] readInputRegisters(int ref, int count) throws ModbusException {
        m_ReadStatusChannelRequest.setReference(ref);
        m_ReadStatusChannelRequest.setWordCount(count);
        m_Transaction.setRequest(m_ReadStatusChannelRequest);
        m_Transaction.execute();
        return ((ReadStatusChannelsResponse) m_Transaction.getResponse()).getRegisters();
    }// readInputRegisters

    /**
     * Reads a given number of registers from the slave.
     * 
     * Note that the number of registers returned (i.e. array length)
     * will be according to the number received in the slave response.
     *
     * @param ref the offset of the register to start reading from.
     * @param count the number of registers to be read.
     * @return a {@link Register[]} holding the received registers.
     * @throws ModbusException if an I/O error, a slave exception or
     *             a transaction error occurs.
     */
    public synchronized Register[] readMultipleRegisters(int ref, int count) throws ModbusException {
        m_ReadMultipleRegistersRequest.setReference(ref);
        m_ReadMultipleRegistersRequest.setWordCount(count);
        m_Transaction.setRequest(m_ReadMultipleRegistersRequest);
        m_Transaction.execute();
        return ((ReadMultipleRegistersResponse) m_Transaction.getResponse()).getRegisters();
    }// readMultipleRegisters

    /**
     * Writes a single register to the slave.
     *
     * @param ref the offset of the register to be written.
     * @param register a {@link Register} holding the value of the register
     *            to be written.
     * @throws ModbusException if an I/O error, a slave exception or
     *             a transaction error occurs.
     */
    public synchronized void writeSingleRegister(int ref, Register register) throws ModbusException {
        m_WriteSingleChannelRequest.setReference(ref);
        m_WriteSingleChannelRequest.setRegister(register);
        m_Transaction.setRequest(m_WriteSingleChannelRequest);
        m_Transaction.execute();
    }// writeSingleRegister

    /**
     * Writes a number of registers to the slave.
     *
     * @param ref the offset of the register to start writing to.
     * @param registers a {@link Register[]} holding the values of
     *            the registers to be written.
     * @throws ModbusException if an I/O error, a slave exception or
     *             a transaction error occurs.
     */
    public synchronized void writeMultipleRegisters(int ref, Register[] registers) throws ModbusException {
        m_WriteMultipleRegistersRequest.setReference(ref);
        m_WriteMultipleRegistersRequest.setRegisters(registers);
        m_Transaction.setRequest(m_WriteMultipleRegistersRequest);
        m_Transaction.execute();
    }// writeMultipleRegisters

}// class ModbusUDPMaster
