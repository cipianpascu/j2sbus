/*
 * Copyright 2002-2016 jamod & j2mod development teams
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
 */
package com.ciprianpascu.j2sbus.modbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

import com.ciprianpascu.j2sbus.modbus.Modbus;
import com.ciprianpascu.j2sbus.modbus.io.NonWordDataHandler;
import com.ciprianpascu.j2sbus.modbus.net.AbstractModbusListener;
import com.ciprianpascu.j2sbus.modbus.procimg.IllegalAddressException;
import com.ciprianpascu.j2sbus.modbus.procimg.ProcessImage;
import com.ciprianpascu.j2sbus.modbus.procimg.Register;
import com.ciprianpascu.j2sbus.modbus.procimg.SimpleRegister;

/**
 * Class implementing a <code>WriteMultipleRegistersRequest</code>. The
 * implementation directly correlates with the class 0 function <i>write
 * multiple registers (FC 16)</i>. It encapsulates the corresponding request
 * message.
 *
 * @author Dieter Wimberger
 * @author jfhaugh
 * @author Steve O'Hara (4NG)
 * @version 2.0 (March 2016)
 */
public class WriteMultipleRegistersRequest extends ModbusRequest {
    private int reference;
    private Register[] registers;
    private NonWordDataHandler nonWordDataHandler = null;

    /**
     * Constructs a new <code>WriteMultipleRegistersRequest</code> instance with a
     * given starting reference and values to be written.
     *
     *
     * @param first     -- the address of the first register to write to.
     * @param registers -- the registers to be written.
     */
    public WriteMultipleRegistersRequest(int first, Register[] registers) {
        setFunctionCode(Modbus.WRITE_MULTIPLE_REGISTERS);

        setReference(first);
        setRegisters(registers);
        setDataLength(5 + registers.length * 2);
    }

    /**
     * Constructs a new <code>WriteMultipleRegistersRequest</code> instance.
     */
    public WriteMultipleRegistersRequest() {
        setFunctionCode(Modbus.WRITE_MULTIPLE_REGISTERS);
        setDataLength(5);
    }

    @Override
    public ModbusResponse getResponse() {
        return updateResponseWithHeader(new WriteMultipleRegistersResponse());
    }

    /**
     * createResponse - Returns the <code>WriteMultipleRegistersResponse</code> that
     * represents the answer to this <code>WriteMultipleRegistersRequest</code>.
     *
     * The implementation should take care about assembling the reply to this
     * <code>WriteMultipleRegistersRequest</code>.
     *
     * This method is used to create responses from the process image associated
     * with the listener. It is commonly used to implement Modbus
     * slave instances.
     *
     * @return the corresponding ModbusResponse.
     *
     *
     * createResponse() must be able to handle the case where the word
     * data that is in the response is actually non-word data. That is,
     * where the slave device has data which are not actually
     * <code>short</code> values in the range of registers being processed.
     */
    @Override
    public ModbusResponse createResponse(AbstractModbusListener listener) {
        WriteMultipleRegistersResponse response;

        if (nonWordDataHandler == null) {
            Register[] regs;
            // 1. get process image
            ProcessImage procimg = listener.getProcessImage(getUnitID());
            // 2. get registers
            try {
                regs = procimg.getRegisterRange(getReference(), getWordCount());
                // 3. set Register values
                for (int i = 0; i < regs.length; i++) {
                    regs[i].setValue(this.getRegister(i).getValue());
                }
            }
            catch (IllegalAddressException iaex) {
                return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION);
            }
            response = (WriteMultipleRegistersResponse)getResponse();
            response.setReference(getReference());
            response.setWordCount(getWordCount());
        }
        else {
            int result = nonWordDataHandler.commitUpdate();
            if (result > 0) {
                return createExceptionResponse(result);
            }

            response = (WriteMultipleRegistersResponse)getResponse();
            response.setReference(getReference());
            response.setWordCount(nonWordDataHandler.getWordCount());
        }

        return response;
    }

    /**
     * setReference - Returns the reference of the register to start writing to
     * with this <code>WriteMultipleRegistersRequest</code>.
     *
     *
     * @return the reference of the register to start writing to as <code>int</code>
     * .
     */
    public int getReference() {
        return reference;
    }

    /**
     * setReference - Sets the reference of the register to write to with this
     * <code>WriteMultipleRegistersRequest</code>.
     *
     *
     * @param ref the reference of the register to start writing to as an
     *            <code>int</code>.
     */
    public void setReference(int ref) {
        reference = ref;
    }

    /**
     * getRegisters - Returns the registers to be written with this
     * <code>WriteMultipleRegistersRequest</code>.
     *
     *
     * @return the registers to be written as <code>Register[]</code>.
     */
    public synchronized Register[] getRegisters() {
        Register[] dest = new Register[registers.length];
        System.arraycopy(registers, 0, dest, 0, dest.length);
        return dest;
    }

    /**
     * setRegisters - Sets the registers to be written with this
     * <code>WriteMultipleRegistersRequest</code>.
     *
     *
     * @param registers the registers to be written as <code>Register[]</code>.
     */
    public synchronized void setRegisters(Register[] registers) {
        if (registers == null) {
            this.registers = null;
            setDataLength(5);
        } else {
            this.registers = Arrays.copyOf(registers, registers.length);
            setDataLength(5 + registers.length * 2);
        }
    }

    /**
     * getRegister - Returns the <code>Register</code> at the given position.
     *
     * @param index the relative index of the <code>Register</code>.
     *
     * @return the register as <code>Register</code>.
     *
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    public Register getRegister(int index) throws IndexOutOfBoundsException {
        if (index < 0) {
            throw new IndexOutOfBoundsException(index + " < 0");
        }

        if (index >= getWordCount()) {
            throw new IndexOutOfBoundsException(index + " > " + getWordCount());
        }

        return registers[index];
    }

    /**
     * getRegisterValue - Returns the value of the specified register.
     *
     *
     * @param index the index of the desired register.
     *
     * @return the value as an <code>int</code>.
     *
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    public int getRegisterValue(int index) throws IndexOutOfBoundsException {
        return getRegister(index).toUnsignedShort();
    }

    /**
     * getByteCount - Returns the number of bytes representing the values to be
     * written.
     *
     *
     * @return the number of bytes to be written as <code>int</code>.
     */
    public int getByteCount() {
        return getWordCount() * 2;
    }

    /**
     * getWordCount - Returns the number of words to be written.
     *
     * @return the number of words to be written as <code>int</code>.
     */
    public int getWordCount() {
        if (registers == null) {
            return 0;
        }

        return registers.length;
    }

    /**
     * getNonWordDataHandler - Returns the actual non word data handler.
     *
     * @return the actual <code>NonWordDataHandler</code>.
     */
    public NonWordDataHandler getNonWordDataHandler() {
        return nonWordDataHandler;
    }

    /**
     * setNonWordHandler - Sets a non word data handler. A non-word data handler
     * is responsible for converting words from a Modbus packet into the
     * non-word values associated with the actual device's registers.
     *
     * @param dhandler a <code>NonWordDataHandler</code> instance.
     */
    public void setNonWordDataHandler(NonWordDataHandler dhandler) {
        nonWordDataHandler = dhandler;
    }

    @Override
    public void writeData(DataOutput output) throws IOException {
        output.write(getMessage());
    }

    @Override
    public void readData(DataInput input) throws IOException {
        reference = input.readUnsignedShort();
        int registerCount = input.readUnsignedShort();
        int byteCount = input.readUnsignedByte();

        if (nonWordDataHandler == null) {
            byte[] buffer = new byte[byteCount];
            input.readFully(buffer, 0, byteCount);

            int offset = 0;
            registers = new Register[registerCount];

            for (int register = 0; register < registerCount; register++) {
                registers[register] = new SimpleRegister(buffer[offset], buffer[offset + 1]);
                offset += 2;
            }
        }
        else {
            nonWordDataHandler.readData(input, reference, registerCount);
        }
    }

    @Override
    public byte[] getMessage() {
        int len = 5;

        if (registers != null) {
            len += registers.length * 2;
        }

        byte[] result = new byte[len];
        int registerCount = registers != null ? registers.length : 0;

        result[0] = (byte)((reference >> 8) & 0xff);
        result[1] = (byte)(reference & 0xff);
        result[2] = (byte)((registerCount >> 8) & 0xff);
        result[3] = (byte)(registerCount & 0xff);
        result[4] = (byte)(registerCount * 2);

        int offset = 5;

        if (nonWordDataHandler == null) {
            for (int i = 0; i < registerCount; i++) {
                byte[] bytes = registers[i].toBytes();
                result[offset++] = bytes[0];
                result[offset++] = bytes[1];
            }
        }
        else {
            nonWordDataHandler.prepareData(reference, registerCount);
            byte[] bytes = nonWordDataHandler.getData();
            if (bytes != null) {
                int nonWordBytes = bytes.length;
                if (nonWordBytes > registerCount * 2) {
                    nonWordBytes = registerCount * 2;
                }

                System.arraycopy(bytes, 0, result, offset, nonWordBytes);
            }
        }
        return result;
    }
}
