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

/**
 * Interface defining a ModbusMessage.
 *
 * @author Dieter Wimberger
 * @author Steve O'Hara (4NG)
 * @version 2.0 (March 2016)
 */
public interface ModbusMessage {

    /**
     * Check the flag which indicates that this <code>ModbusMessage</code> is for a
     * headless (serial, or headless networked) connection.
     * @return is for a headless (serial, or headless networked) connection
     */
    boolean isHeadless();

    /**
     * Sets the flag that marks this <code>ModbusMessage</code> as headless (for
     * serial transport).
     */
    void setHeadless();

    /**
     * Returns the transaction identifier of this <code>ModbusMessage</code> as
     * <code>int</code>.
     *
     *
     * The identifier is a 2-byte (short) non negative integer value valid in
     * the range of 0-65535.
     *
     * @return the transaction identifier as <code>int</code>.
     */
    int getTransactionID();

    /**
     * Returns the protocol identifier of this <code>ModbusMessage</code> as
     * <code>int</code>.
     *
     *
     * The identifier is a 2-byte (short) non negative integer value valid in
     * the range of 0-65535.
     *
     * @return the protocol identifier as <code>int</code>.
     */
    int getProtocolID();

    /**
     * Returns the length of the data appended after the protocol header.
     *
     *
     * @return the data length as <code>int</code>.
     */
    int getDataLength();

    /**
     * Returns the subnet identifier of this <code>ModbusMessage</code> as
     * <code>int</code>.
     *
     *
     * The identifier is a 1-byte non negative integer value valid in the range
     * of 0-255.
     *
     * @return the subnet identifier as <code>int</code>.
     */
    int getSubnetID();

    /**
     * Returns the unit identifier of this <code>ModbusMessage</code> as
     * <code>int</code>.
     *
     *
     * The identifier is a 1-byte non negative integer value valid in the range
     * of 0-255.
     *
     * @return the unit identifier as <code>int</code>.
     */
    int getUnitID();

    /**
     * Returns the function code of this <code>ModbusMessage</code> as <code>int</code>.<br>
     * The function code is a 1-byte non negative integer value valid in the
     * range of 0-127.
     *
     *
     * Function codes are ordered in conformance classes their values are
     * specified in <code>com.ciprianpascu.j2sbus.modbus.Modbus</code>.
     *
     * @return the function code as <code>int</code>.
     *
     * @see com.ciprianpascu.j2sbus.modbus.Modbus
     */
    int getFunctionCode();

    /**
     * Returns the <i>raw</i> message as an array of bytes.
     *
     *
     * @return the <i>raw</i> message as <code>byte[]</code>.
     */
    byte[] getMessage();

    /**
     * Returns the <i>raw</i> message as <code>String</code> containing a
     * hexadecimal series of bytes.
     *
     *
     * This method is specially for debugging purposes, allowing the user to log
     * the communication in a manner used in the specification document.
     *
     * @return the <i>raw</i> message as <code>String</code> containing a
     * hexadecimal series of bytes.
     */
    String getHexMessage();

    /**
     * Returns the number of bytes that will
     * be written by {@link #writeTo(DataOutput)}.
     *
     * @return the number of bytes that will be written as <code>int</code>.
     */
    int getOutputLength();

    /**
     * Writes this <code>Transportable</code> to the
     * given <code>DataOutput</code>.
     *
     * @param dout the <code>DataOutput</code> to write to.
     *
     * @throws java.io.IOException if an I/O error occurs.
     */
    void writeTo(DataOutput dout) throws IOException;

    /**
     * Reads this <code>Transportable</code> from the given
     * <code>DataInput</code>.
     *
     * @param din the <code>DataInput</code> to read from.
     *
     * @throws java.io.IOException if an I/O error occurs or the data
     *                             is invalid.
     */
    void readFrom(DataInput din) throws IOException;


}
