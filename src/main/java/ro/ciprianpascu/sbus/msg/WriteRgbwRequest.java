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
import ro.ciprianpascu.sbus.io.NonWordDataHandler;
import ro.ciprianpascu.sbus.procimg.ByteRegister;
import ro.ciprianpascu.sbus.procimg.IllegalAddressException;
import ro.ciprianpascu.sbus.procimg.ProcessImageImplementation;
import ro.ciprianpascu.sbus.procimg.Register;
import ro.ciprianpascu.sbus.procimg.WordRegister;

/**
 * Class implementing a WriteRgbwRequest for the SBus protocol.
 * This request writes RGBW (Red, Green, Blue, White) values and a temporisation value
 * to a device. It uses 4 byte registers for the RGBW values and 1 word register
 * for the temporisation.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public final class WriteRgbwRequest extends SbusRequest {

    // instance attributes
    private Register[] m_Registers;
    private NonWordDataHandler m_NonWordDataHandler = null;

    /**
     * Constructs a new WriteRgbwRequest instance with default values.
     */
    public WriteRgbwRequest() {
        super();
        setFunctionCode(Sbus.WRITE_RGBW_REQUEST);
        // 4 bytes for RGBW values + 2 bytes for temporisation
        setDataLength(6);
    }

    /**
     * Constructs a new WriteRgbwRequest instance with given register values.
     * The registers should contain RGBW values and temporisation in order:
     * reg[0] = red
     * reg[1] = green
     * reg[2] = blue
     * reg[3] = white
     * reg[4] = temporisation
     *
     * @param reg array of registers containing the RGBW and temporisation values
     */
    public WriteRgbwRequest(Register[] reg) {
        super();
        setFunctionCode(Sbus.WRITE_RGBW_REQUEST);
        m_Registers = reg;
        setDataLength(6);
    }


    @Override
    public SbusResponse createResponse(ProcessImageImplementation procimg) {
        WriteRgbwResponse response = null;

        if (m_NonWordDataHandler == null) {
            try {
                // Get registers for RGBW values (4 bytes) and temporisation (1 word)
                Register[] regs = procimg.getRegisterRange(0, 5);
                // Update register values
                for (int i = 0; i < regs.length; i++) {
                    regs[i].setValue(this.getRegister(i).toBytes());
                }
            } catch (IllegalAddressException iaex) {
                return createExceptionResponse(Sbus.ILLEGAL_ADDRESS_EXCEPTION);
            }
        } else {
            int result = m_NonWordDataHandler.commitUpdate();
            if (result > 0) {
                return createExceptionResponse(result);
            }
        }
        
        response = new WriteRgbwResponse(true);
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
     * Sets the registers containing RGBW and temporisation values.
     *
     * @param registers array of registers containing RGBW and temporisation values
     */
    public void setRegisters(Register[] registers) {
        m_Registers = registers;
        setDataLength(6);
    }

    /**
     * Returns the registers containing RGBW and temporisation values.
     *
     * @return array of registers
     */
    public Register[] getRegisters() {
        return m_Registers;
    }

    /**
     * Returns the register at the specified index.
     * Index mapping:
     * 0 = red
     * 1 = green
     * 2 = blue
     * 3 = white
     * 4 = temporisation
     *
     * @param index the index of the register to retrieve
     * @return the register at the specified index
     * @throws IndexOutOfBoundsException if index is out of range
     */
    public Register getRegister(int index) throws IndexOutOfBoundsException {
        if (index >= m_Registers.length) {
            throw new IndexOutOfBoundsException();
        }
        return m_Registers[index];
    }

    /**
     * Sets a handler for non-word data processing.
     *
     * @param dhandler the NonWordDataHandler to use
     */
    public void setNonWordDataHandler(NonWordDataHandler dhandler) {
        m_NonWordDataHandler = dhandler;
        setDataLength(6);
    }

    /**
     * Returns the current non-word data handler.
     *
     * @return the current NonWordDataHandler
     */
    public NonWordDataHandler getNonWordDataHandler() {
        return m_NonWordDataHandler;
    }
    
    @Override
    public void writeData(DataOutput dout) throws IOException {
        dout.writeByte(m_Registers[0].toShort()); // red
        dout.writeByte(m_Registers[1].toShort()); // green
        dout.writeByte(m_Registers[2].toShort()); // blue
        dout.writeByte(m_Registers[3].toShort()); // white
        dout.writeShort(m_Registers[4].toUnsignedShort()); // temporisation
    }

    @Override
    public void readData(DataInput din) throws IOException {
        m_Registers = new Register[5]; // 4 for RGBW + 1 for temporisation
        m_Registers[0] = new ByteRegister(din.readByte()); // red
        m_Registers[1] = new ByteRegister(din.readByte()); // green
        m_Registers[2] = new ByteRegister(din.readByte()); // blue
        m_Registers[3] = new ByteRegister(din.readByte()); // white
        m_Registers[4] = new WordRegister(din.readShort()); // temporisation
        setDataLength(6);
    }
}
