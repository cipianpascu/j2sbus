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
package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.util.ModbusUtil;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Abstract class implementing a <code>ModbusMessage</code>. This class provides
 * specialised implementations with the functionality they have in common.
 *
 * @author Dieter Wimberger
 * @author Steve O'Hara (4NG)
 * @version 2.0 (March 2016)
 */
public abstract class ModbusMessageImpl implements ModbusMessage {

    // instance attributes
    private int transactionID = Modbus.DEFAULT_TRANSACTION_ID;
    private int protocolID = Modbus.DEFAULT_PROTOCOL_ID;
    private int dataLength;
    private int subnetID = Modbus.DEFAULT_SUBNET_ID;
    private int unitID = Modbus.DEFAULT_UNIT_ID;
    private int functionCode;
    private boolean headless = false; // flag for header-less (serial)

    @Override
    public boolean isHeadless() {
        return headless;
    }

    @Override
    public void setHeadless() {
        headless = true;
    }

    @Override
    public int getTransactionID() {
        return transactionID & 0x0000FFFF;
    }

    /**
     * Sets the transaction identifier of this <code>ModbusMessage</code>.
     *
     *
     * The identifier must be a 2-byte (short) non negative integer value valid
     * in the range of 0-65535.<br>
     *
     * @param tid the transaction identifier as <code>int</code>.
     */
    public void setTransactionID(int tid) {
        transactionID = tid & 0x0000FFFF;
    }

    @Override
    public int getProtocolID() {
        return protocolID;
    }

    /**
     * Sets the protocol identifier of this <code>ModbusMessage</code>.
     *
     * The identifier should be a 2-byte (short) non negative integer value
     * valid in the range of 0-65535.<br>
     *
     *
     * @param pid the protocol identifier as <code>int</code>.
     */
    public void setProtocolID(int pid) {
        protocolID = pid;
    }

    @Override
    public int getDataLength() {
        return dataLength;
    }

    /**
     * Sets the length of the data appended after the protocol header.
     *
     *
     * Note that this library, a bit in contrast to the specification, counts
     * the unit identifier and the function code in the header, because it is
     * part of each and every message. Thus this method will add three (3) to the
     * passed in integer value.
     *
     *
     * This method does not include the length of a final CRC/LRC for those
     * protocols which requirement.
     *
     * @param length the data length as <code>int</code>.
     */
    public void setDataLength(int length) {
        if (length < 0 || length + 3 > 255) {
            throw new IllegalArgumentException("Invalid length: " + length);
        }

        dataLength = length + 3;
    }

    @Override
    public int getUnitID() {
        return unitID;
    }

    /**
     * Sets the unit identifier of this <code>ModbusMessage</code>.<br>
     * The identifier should be a 1-byte non negative integer value valid in the
     * range of 0-255.
     *
     * @param num the unit identifier number to be set.
     */
    public void setUnitID(int num) {
        unitID = num;
    }

    public int getSubnetID() {
		return subnetID;
	}

    /**
     * Sets the subnet identifier of this <code>ModbusMessage</code>.<br>
     * The identifier should be a 1-byte non negative integer value valid in the
     * range of 0-255.
     *
     * @param subnetID the unit identifier number to be set.
     */
	public void setSubnetID(int subnetID) {
		this.subnetID = subnetID;
	}

	@Override
    public int getFunctionCode() {
        return functionCode;
    }

    /**
     * Sets the function code of this <code>ModbusMessage</code>.<br>
     * The function code should be a 1-byte non negative integer value valid in
     * the range of 0-127.<br>
     * Function codes are ordered in conformance classes their values are
     * specified in <code>com.ghgande.j2mod.modbus.Modbus</code>.
     *
     * @param code the code of the function to be set.
     *
     * @see com.ghgande.j2mod.modbus.Modbus
     */
    protected void setFunctionCode(int code) {
        functionCode = code;
    }

    @Override
    public String getHexMessage() {
        return ModbusUtil.toHex(this);
    }

    /**
     * Sets the headless flag of this message.
     *
     * @param b true if headless, false otherwise.
     */
    public void setHeadless(boolean b) {
        headless = b;
    }

    @Override
    public int getOutputLength() {
        int l = 3 + getDataLength();
        if (!isHeadless()) {
            l = l + 6;
        }
        return l;
    }

    @Override
    public void writeTo(DataOutput dout) throws IOException {

        if (!isHeadless()) {
            dout.writeShort(getTransactionID());
            dout.writeShort(getProtocolID());
            dout.writeShort(getDataLength());
        }
        dout.write(getSubnetID());
        dout.writeByte(getUnitID());
        dout.writeByte(getFunctionCode());

        writeData(dout);
    }

    @Override
    public void readFrom(DataInput din) throws IOException {
        if (!isHeadless()) {
            setTransactionID(din.readUnsignedShort());
            setProtocolID(din.readUnsignedShort());
            dataLength = din.readUnsignedShort();
        }
        setSubnetID(din.readUnsignedByte());
        setUnitID(din.readUnsignedByte());
        setFunctionCode(din.readUnsignedByte());
        readData(din);
    }

    /**
     * Writes the subclass specific data to the given DataOutput.
     *
     * @param dout the DataOutput to be written to.
     *
     * @throws IOException if an I/O related error occurs.
     */
    public abstract void writeData(DataOutput dout) throws IOException;

    /**
     * Reads the subclass specific data from the given DataInput instance.
     *
     * @param din the DataInput to read from.
     *
     * @throws IOException if an I/O related error occurs.
     */
    public abstract void readData(DataInput din) throws IOException;

}
