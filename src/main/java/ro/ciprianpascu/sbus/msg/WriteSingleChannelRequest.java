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

import ro.ciprianpascu.sbus.Sbus;
import ro.ciprianpascu.sbus.procimg.ByteRegister;
import ro.ciprianpascu.sbus.procimg.IllegalAddressException;
import ro.ciprianpascu.sbus.procimg.InputRegister;
import ro.ciprianpascu.sbus.procimg.ProcessImageImplementation;
import ro.ciprianpascu.sbus.procimg.Register;
import ro.ciprianpascu.sbus.procimg.WordRegister;

/**
 * Class implementing a WriteSingleChannelRequest for the SBus protocol.
 * This request writes a value and timer to a single channel. It uses
 * one byte register for the value and one word register for the timer.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public final class WriteSingleChannelRequest extends SbusRequest {

    // instance attributes
    private int m_channelNo;
    private Register[] m_Registers;

    /**
     * Constructs a new WriteSingleChannelRequest instance with default values.
     *
     * @param withTimer true to include timer data in the request, false for value only
     */
    public WriteSingleChannelRequest(boolean withTimer) {
        super();
        setFunctionCode(Sbus.WRITE_SINGLE_CHANNEL_REQUEST);
        // 4 bytes (unit id and function code is excluded)
        if (withTimer) {
            setDataLength(4);
        } else {
            setDataLength(2);
        }
    }

    /**
     * Constructs a new WriteSingleChannelRequest instance with given channel number
     * and register values.
     *
     * @param channelNo the channel number to write to
     * @param regs array of registers containing [value, timer] data
     */
    public WriteSingleChannelRequest(int channelNo, Register[] regs) {
        super();
        setFunctionCode(Sbus.WRITE_SINGLE_CHANNEL_REQUEST);
        m_channelNo = channelNo;
        m_Registers = regs;
        if (regs.length == 1) {
            // 2 bytes for channel and value (unit id and function code is excluded)
            setDataLength(2);
        }
        if (regs.length == 2) {
            // 4 bytes for channel, value and timer (unit id and function code is excluded)
            setDataLength(4);
        }
    }

    @Override
    public SbusResponse createResponse(ProcessImageImplementation procimg) {
        WriteSingleChannelResponse response = null;
        boolean updateSuccessful = false;

        try {
            Register regValue = procimg.getRegister(m_channelNo + 1);
            regValue.setValue(m_Registers[0].toBytes());
            if (getDataLength() == (11 + 4)) {
                Register regTimer = procimg.getRegister(m_channelNo * 2 + 1);
                regTimer.setValue(m_Registers[1].toBytes());
            }
            updateSuccessful = true;
        } catch (IllegalAddressException iaex) {
            return createExceptionResponse(Sbus.ILLEGAL_ADDRESS_EXCEPTION);
        }

        response = new WriteSingleChannelResponse(this.getChannelNo(), updateSuccessful);
        // Transfer header data
        response.setSourceSubnetID(this.getSourceSubnetID());
        response.setSourceUnitID(this.getSourceUnitID());
        response.setSourceDeviceType(this.getSourceDeviceType());
        response.setSubnetID(this.getSubnetID());
        response.setUnitID(this.getUnitID());
        response.setFunctionCode(this.getFunctionCode());
        return response;
    }

    /**
     * Sets the channel number to write to.
     *
     * @param channelNo the channel number
     */
    public void setChannelNo(int channelNo) {
        m_channelNo = channelNo;
    }

    /**
     * Returns the channel number being written to.
     *
     * @return the channel number
     */
    public int getChannelNo() {
        return m_channelNo;
    }

    /**
     * Returns the register at the specified index.
     * Index 0 = value register
     * Index 1 = timer register
     *
     * @param index the index of the register to retrieve (0 or 1)
     * @return the register at the specified index
     * @throws IndexOutOfBoundsException if index is not 0 or 1
     */
    public InputRegister getRegister(int index) throws IndexOutOfBoundsException {
        if (index > m_Registers.length) {
            throw new IndexOutOfBoundsException();
        }
        return m_Registers[index];
    }

    /**
     * Returns the value of the register at the specified index as an unsigned short.
     * Index 0 = value register
     * Index 1 = timer register
     *
     * @param index the index of the register value to retrieve (0 or 1)
     * @return the register value as an unsigned short
     * @throws IndexOutOfBoundsException if index is not 0 or 1
     */
    public int getRegisterValue(int index) throws IndexOutOfBoundsException {
        if (index > m_Registers.length) {
            throw new IndexOutOfBoundsException();
        }
        return m_Registers[index].toUnsignedShort();
    }

    /**
     * Returns the array of registers containing the value and timer data.
     *
     * @return array containing [value, timer] registers
     */
    public InputRegister[] getRegisters() {
        return m_Registers;
    }

    /**
     * Sets the registers containing the value and timer data.
     * The array must contain exactly 2 registers:
     * registers[0] = value register
     * registers[1] = timer register
     *
     * @param registers array containing [value, timer] registers
     */
    public void setRegisters(Register[] registers) {
        m_Registers = registers;
        if (registers.length == 1) {
            // 2 bytes for channel and value (unit id and function code is excluded)
            setDataLength(2);
        }
        if (registers.length == 2) {
            // 4 bytes for channel, value and timer (unit id and function code is excluded)
            setDataLength(4);
        }
    }

    @Override
    public void writeData(DataOutput dout) throws IOException {
        dout.writeByte(m_channelNo);
        dout.write(m_Registers[0].toBytes());
        if (m_Registers.length == 2) {
            dout.write(m_Registers[1].toBytes());
        }
    }

    @Override
    public void readData(DataInput din) throws IOException {
        m_channelNo = din.readByte();
        if (getDataLength() == (11 + 4)) {
            m_Registers = new Register[2];
            m_Registers[0] = new ByteRegister(din.readByte());
            m_Registers[1] = new WordRegister(din.readShort());
            setDataLength(4);
        } else {
            m_Registers = new Register[1];
            m_Registers[0] = new ByteRegister(din.readByte());
            setDataLength(2);
        }

    }
}
