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
import ro.ciprianpascu.sbus.util.ModbusUtil;

/**
 * Abstract class implementing a {@link ModbusMessage}.
 * This class provides specialised implementations with
 * the functionality they have in common.
 *
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public abstract class ModbusMessageImpl implements ModbusMessage {

    // instance attributes
    private int m_DataLength;
    private int m_SubnetID = Modbus.DEFAULT_SUBNET_ID;
    private int m_UnitID = Modbus.DEFAULT_UNIT_ID;
    private int m_FunctionCode;
    private int m_SourceSubnetID = Modbus.DEFAULT_SOURCE_SUBNET_ID;
    private int m_SourceUnitID = Modbus.DEFAULT_SOURCE_UNIT_ID;
    private int m_SourceDeviceType = Modbus.DEFAULT_SOURCE_DEVICE_TYPE;

    /*** Header ******************************************/



    public int getSourceSubnetID() {
		return m_SourceSubnetID;
	}

	public void setSourceSubnetID(int m_SourceSubnetID) {
		this.m_SourceSubnetID = m_SourceSubnetID;
	}

	public int getSourceUnitID() {
		return m_SourceUnitID;
	}

	public void setSourceUnitID(int m_SourceUnitID) {
		this.m_SourceUnitID = m_SourceUnitID;
	}

	public int getSourceDeviceType() {
		return m_SourceDeviceType;
	}

	public void setSourceDeviceType(int m_SourceDeviceType) {
		this.m_SourceDeviceType = m_SourceDeviceType;
	}

	@Override
    public int getDataLength() {
        return m_DataLength;
    }// getDataLength

    /**
     * Sets the length of the data appended
     * after the protocol header.
* 
     * Note that this library, a bit in contrast to the
     * specification, counts the subnet identifier, the unit identifier and the
     * function code to the header, because it is part
     * of each and every message. Thus this message will
     * append three (3) to the passed in integer value.
* 
     *
     * @param length the data length as {@link int}.
     */
    public void setDataLength(int length) {
        // should be below 255, check!
        m_DataLength = length + 11;
    }// setData

    public int getSubnetID() {
		return m_SubnetID;
	}

    /**
     * Sets the subnetId identifier of this
     * {@link ModbusMessage}.<br>
     * The identifier should be a 1-byte non negative
     * integer value valid in the range of 0-255.
     *
     * @param subnetId the unit identifier number to be set.
     */
	public void setSubnetID(int subnetId) {
		this.m_SubnetID = subnetId;
	}

	@Override
    public int getUnitID() {
        return m_UnitID;
    }// getUnitID

    /**
     * Sets the unit identifier of this
     * {@link ModbusMessage}.<br>
     * The identifier should be a 1-byte non negative
     * integer value valid in the range of 0-255.
     *
     * @param num the unit identifier number to be set.
     */
    public void setUnitID(int num) {
        m_UnitID = num;
        // setChanged(true);
    }// setUnitID

    @Override
    public int getFunctionCode() {
        return m_FunctionCode;
    }// getFunctionCode

    /**
     * Sets the function code of this {@link ModbusMessage}.<br>
     * The function code should be a 1-byte non negative
     * integer value valid in the range of 0-127.<br>
     * Function codes are ordered in conformance
     * classes their values are specified in
     * {@link ro.ciprianpascu.sbus.Modbus}.
     *
     * @param code the code of the function to be set.
     * @see ro.ciprianpascu.sbus.Modbus
     */
    protected void setFunctionCode(int code) {
        m_FunctionCode = code;
        // setChanged(true);
    }// setFunctionCode

    /*** Data ********************************************/

    /*** Transportable ***********************************/

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
        dout.writeByte(getSubnetID());
        dout.writeByte(getUnitID());
        dout.writeByte(getFunctionCode());
        writeData(dout);
    }// writeTo

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
        setSubnetID(din.readUnsignedByte());
        setUnitID(din.readUnsignedByte());
        setFunctionCode(din.readUnsignedByte());
        readData(din);
    }// readFrom

    /**
     * Reads the subclass specific data from the given DataInput instance.
     *
     * @param din the DataInput to read from.
     * @throws IOException if an I/O related error occurs.
     */
    public abstract void readData(DataInput din) throws IOException;



    /*** END Transportable *******************************/

    /**
     * Returns the this message as hexadecimal string.
     *
     * @return the message as hex encoded string.
     */
    @Override
    public String getHexMessage() {
        return ModbusUtil.toHex(this);
    }// getHexMessage

}// class ModbusMessageImpl
