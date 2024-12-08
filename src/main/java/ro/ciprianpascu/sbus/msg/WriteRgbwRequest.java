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

package ro.ciprianpascu.sbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import ro.ciprianpascu.sbus.Modbus;
import ro.ciprianpascu.sbus.io.NonWordDataHandler;
import ro.ciprianpascu.sbus.procimg.ByteRegister;
import ro.ciprianpascu.sbus.procimg.IllegalAddressException;
import ro.ciprianpascu.sbus.procimg.ProcessImageImplementation;
import ro.ciprianpascu.sbus.procimg.Register;
import ro.ciprianpascu.sbus.procimg.WordRegister;

/**
 * Class implementing a {@link WriteRgbwRequest}.
 * The implementation directly correlates with the class 0
 * function <i>write single register (FC 6)</i>. It
 * encapsulates the corresponding request message.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu

 * @version %I% (%G%)
 */
public final class WriteRgbwRequest extends ModbusRequest {

    // instance attributes
    private Register[] m_Registers;
    private NonWordDataHandler m_NonWordDataHandler = null;

    /**
     * Constructs a new {@link WriteRgbwRequest}
     * instance.
     */
    public WriteRgbwRequest() {
        super();
        setFunctionCode(Modbus.WRITE_RGBW_REQUEST);
        // 4 bytes (unit id and function code is excluded)
        setDataLength(6);
    }// constructor

    /**
     * Constructs a new {@link WriteRgbwRequest}
     * instance with a given reference and value to be written.
     * 
     *
     * @param reg the registers containing the data to be written.
     */
    public WriteRgbwRequest( Register[] reg) {
        super();
        setFunctionCode(Modbus.WRITE_RGBW_REQUEST);
        m_Registers = reg;
        // 4 bytes (unit id and function code is excluded)
        setDataLength(6);
    }// constructor

    @Override
    public ModbusResponse createResponse(ProcessImageImplementation procimg) {
    	WriteRgbwResponse response = null;

        // 1. get register
        if (m_NonWordDataHandler == null) {
            Register[] regs = null;
            // 1. get registers
            try {
                // TODO: realize a setRegisterRange()?
                regs = procimg.getRegisterRange(0, 5); // 4 byte registers + 1 word register
                // 2. set Register values
                for (int i = 0; i < regs.length; i++) {
                    regs[i].setValue(this.getRegister(i).toBytes());
                }
            } catch (IllegalAddressException iaex) {
                return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION);
            }
        } else {
            int result = m_NonWordDataHandler.commitUpdate();
            if (result > 0) {
                return createExceptionResponse(result);
            }
        }
        response = new WriteRgbwResponse(true);
        // transfer header data
        response.setSourceSubnetID(this.getSourceSubnetID());
		response.setSourceUnitID(this.getSourceUnitID());
		response.setSourceDeviceType(this.getSourceDeviceType());
        response.setSubnetID(this.getSubnetID());
        response.setUnitID(this.getUnitID());
        response.setFunctionCode(this.getFunctionCode());
        return response;
    }// createResponse


    /**
     * Sets the registers to be written with this
     * {@link WriteMultipleRegistersRequest}.
     * 
     *
     * @param registers the registers to be written
     *            as {@link Register[]}.
     */
    public void setRegisters(Register[] registers) {
        m_Registers = registers;
        setDataLength(6); // update message length in header
    }// setRegisters

    /**
     * Returns the registers to be written with this
     * {@link WriteMultipleRegistersRequest}.
     * 
     *
     * @return the registers to be written as {@link Register[]}.
     */
    public Register[] getRegisters() {
        return m_Registers;
    }// getRegisters

    /**
     * Returns the {@link Register} at
     * the given position (relative to the reference
     * used in the request).
     * 
     *
     * @param index the relative index of the {@link Register}.
     *
     * @return the register as {@link Register}.
     *
     * @throws IndexOutOfBoundsException if
     *             the index is out of bounds.
     */
    public Register getRegister(int index) throws IndexOutOfBoundsException {

        if (index >= m_Registers.length) {
            throw new IndexOutOfBoundsException();
        } else {
            return m_Registers[index];
        }
    }// getRegister

    /**
     * Sets a non word data handler.
     *
     * @param dhandler a {@link NonWordDataHandler} instance.
     */
    public void setNonWordDataHandler(NonWordDataHandler dhandler) {
        m_NonWordDataHandler = dhandler;
        setDataLength(6);
    }// setNonWordDataHandler

    /**
     * Returns the actual non word data handler.
     *
     * @return the actual {@link NonWordDataHandler}.
     */
    public NonWordDataHandler getNonWordDataHandler() {
        return m_NonWordDataHandler;
    }// getNonWordDataHandler
    
    @Override
    public void writeData(DataOutput dout) throws IOException {
        dout.writeByte(m_Registers[0].toShort()); //red
		dout.writeByte(m_Registers[1].toShort()); //green
		dout.writeByte(m_Registers[2].toShort()); //blue
		dout.writeByte(m_Registers[3].toShort()); //white
		dout.writeShort(m_Registers[4].toUnsignedShort()); //temporisation
    }// writeData

    @Override
    public void readData(DataInput din) throws IOException {
    	m_Registers = new Register[4];
		m_Registers[0] = new ByteRegister(din.readByte());
		m_Registers[1] = new ByteRegister(din.readByte());
		m_Registers[2] = new ByteRegister(din.readByte());
		m_Registers[3] = new ByteRegister(din.readByte());
		m_Registers[4] = new WordRegister(din.readShort());
		setDataLength(6);
    }// readData

}// class WriteSingleChannelRequest
