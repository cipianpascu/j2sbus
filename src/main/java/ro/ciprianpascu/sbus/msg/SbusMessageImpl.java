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
import ro.ciprianpascu.sbus.util.SbusUtil;

/**
 * Abstract class implementing a {@link SbusMessage}.
 * This class provides specialised implementations with
 * the functionality they have in common.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public abstract class SbusMessageImpl implements SbusMessage {

    /**
     * Constructs a new SbusMessageImpl instance.
     * Initializes the message with default values for subnet ID, unit ID,
     * source subnet ID, source unit ID, and source device type.
     */
    public SbusMessageImpl() {
        m_SubnetID = Sbus.DEFAULT_SUBNET_ID;
        m_UnitID = Sbus.DEFAULT_UNIT_ID;
        m_SourceSubnetID = Sbus.DEFAULT_SOURCE_SUBNET_ID;
        m_SourceUnitID = Sbus.DEFAULT_SOURCE_UNIT_ID;
        m_SourceDeviceType = Sbus.DEFAULT_SOURCE_DEVICE_TYPE;
    }

    // instance attributes
    private int m_DataLength;
    private int m_SubnetID = Sbus.DEFAULT_SUBNET_ID;
    private int m_UnitID = Sbus.DEFAULT_UNIT_ID;
    private int m_FunctionCode;
    private int m_SourceSubnetID = Sbus.DEFAULT_SOURCE_SUBNET_ID;
    private int m_SourceUnitID = Sbus.DEFAULT_SOURCE_UNIT_ID;
    private int m_SourceDeviceType = Sbus.DEFAULT_SOURCE_DEVICE_TYPE;

    /**
     * Gets the source subnet identifier of this message.
     *
     * @return the source subnet identifier as an integer.
     */
    public int getSourceSubnetID() {
        return m_SourceSubnetID;
    }

    /**
     * Sets the source subnet identifier of this message.
     *
     * @param sourceSubnetID the source subnet identifier to set
     */
    public void setSourceSubnetID(int sourceSubnetID) {
        this.m_SourceSubnetID = sourceSubnetID;
    }

    /**
     * Gets the source unit identifier of this message.
     *
     * @return the source unit identifier as an integer.
     */
    public int getSourceUnitID() {
        return m_SourceUnitID;
    }

    /**
     * Sets the source unit identifier of this message.
     *
     * @param sourceUnitID the source unit identifier to set
     */
    public void setSourceUnitID(int sourceUnitID) {
        this.m_SourceUnitID = sourceUnitID;
    }

    /**
     * Gets the source device type of this message.
     *
     * @return the source device type as an integer.
     */
    public int getSourceDeviceType() {
        return m_SourceDeviceType;
    }

    /**
     * Sets the source device type of this message.
     *
     * @param sourceDeviceType the source device type to set
     */
    public void setSourceDeviceType(int sourceDeviceType) {
        this.m_SourceDeviceType = sourceDeviceType;
    }

    @Override
    public int getDataLength() {
        return m_DataLength;
    }

    /**
     * Sets the length of the data appended after the protocol header.
     * 
     * Note that this library, a bit in contrast to the specification, counts 
     * the subnet identifier, the unit identifier and the function code to 
     * the header, because it is part of each and every message. Thus this 
     * message will append three (3) to the passed in integer value.
     *
     * @param length the data length as {@link int}.
     */
    public void setDataLength(int length) {
        // should be below 255, check!
        m_DataLength = length + 11;
    }

    /**
     * Gets the subnet identifier of this message.
     *
     * @return the subnet identifier as an integer.
     */
    public int getSubnetID() {
        return m_SubnetID;
    }

    /**
     * Sets the subnet identifier of this {@link SbusMessage}.
     * The identifier should be a 1-byte non negative
     * integer value valid in the range of 0-255.
     *
     * @param subnetId the subnet identifier number to be set.
     */
    public void setSubnetID(int subnetId) {
        this.m_SubnetID = subnetId;
    }

    @Override
    public int getUnitID() {
        return m_UnitID;
    }

    /**
     * Sets the unit identifier of this {@link SbusMessage}.
     * The identifier should be a 1-byte non negative
     * integer value valid in the range of 0-255.
     *
     * @param num the unit identifier number to be set.
     */
    public void setUnitID(int num) {
        m_UnitID = num;
    }

    @Override
    public int getFunctionCode() {
        return m_FunctionCode;
    }

    /**
     * Sets the function code of this {@link SbusMessage}.
     * The function code should be a 1-byte non negative
     * integer value valid in the range of 0-127.
     * Function codes are ordered in conformance
     * classes their values are specified in
     * {@link ro.ciprianpascu.sbus.Sbus}.
     *
     * @param code the code of the function to be set.
     * @see ro.ciprianpascu.sbus.Sbus
     */
    protected void setFunctionCode(int code) {
        m_FunctionCode = code;
    }

    /**
     * Writes this message to the given {@link DataOutput}.
     *
     * @param dout a {@link DataOutput} instance.
     * @throws IOException if an I/O related error occurs.
     */
    @Override
    public void writeTo(DataOutput dout) throws IOException {
        dout.writeByte(getDataLength());
        dout.writeByte(getSourceSubnetID());
        dout.writeByte(getSourceUnitID());
        dout.writeShort(getSourceDeviceType());
        dout.writeShort(getFunctionCode());
        dout.writeByte(getSubnetID());
        dout.writeByte(getUnitID());
        writeData(dout);
    }

    /**
     * Writes the subclass specific data to the given DataOutput.
     *
     * @param dout the DataOutput to be written to.
     * @throws IOException if an I/O related error occurs.
     */
    public abstract void writeData(DataOutput dout) throws IOException;

    @Override
    public void readFrom(DataInput din) throws IOException {
        m_DataLength = din.readUnsignedByte();
        setSourceSubnetID(din.readUnsignedByte());
        setSourceUnitID(din.readUnsignedByte());
        setSourceDeviceType(din.readUnsignedShort());
        setFunctionCode(din.readUnsignedShort());
        setSubnetID(din.readUnsignedByte());
        setUnitID(din.readUnsignedByte());
        readData(din);
    }

    /**
     * Reads the subclass specific data from the given DataInput instance.
     *
     * @param din the DataInput to read from.
     * @throws IOException if an I/O related error occurs.
     */
    public abstract void readData(DataInput din) throws IOException;

    /**
     * Returns this message as hexadecimal string.
     *
     * @return the message as hex encoded string.
     */
    @Override
    public String getHexMessage() {
        return SbusUtil.toHex(this);
    }

    /**
     * Returns a string representation of this message in the format:
     * subnetID_unitID_functionCode
     *
     * @return a string representation of the message
     */
    @Override
    public String toString() {
        return this.getSubnetID() + "_" + this.getUnitID() + "_" + this.getFunctionCode();
    }

}
