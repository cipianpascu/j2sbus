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
package com.ciprianpascu.j2sbus.modbus;

/**
 * Interface defining all constants related to the
 * Modbus protocol.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public interface Modbus {

    /**
     * Defines the class 1 function code
     * for <code>read coils</code>.
     */
    int READ_COILS = 1;

    /**
     * Defines a class 1 function code
     * for <code>read input discretes</code>.
     */
    int READ_INPUT_DISCRETES = 2;

    /**
     * Defines a class 1 function code
     * for <code>read holding registers</code>
     */
    int READ_HOLDING_REGISTERS = 3;

    /**
     * Defines the class 0 function code
     * for <code>read multiple registers</code>.  The
     * proper name is "Read Holding Registers".
     */
    int READ_MULTIPLE_REGISTERS = 3;

    /**
     * Defines a class 1 function code
     * for <code>read input registers</code>.
     */
    int READ_INPUT_REGISTERS = 4;

    /**
     * Defines a class 1 function code
     * for <code>write coil</code>.
     */
    int WRITE_COIL = 5;

    /**
     * Defines a class 1 function code
     * for <code>write single register</code>.
     */
    int WRITE_SINGLE_REGISTER = 6;

    /**
     * <code>read exception status</code>
     *
     * Serial devices only.
     */
    int READ_EXCEPTION_STATUS = 7;

    /**
     * <code>get serial diagnostics</code>
     *
     * Serial devices only.
     */
    int READ_SERIAL_DIAGNOSTICS = 8;

    /**
     * <code>get comm event counter</code>
     *
     * Serial devices only.
     */
    int READ_COMM_EVENT_COUNTER = 11;

    /**
     * <code>get comm event log</code>
     *
     * Serial devices only.
     */
    int READ_COMM_EVENT_LOG = 12;

    /**
     * Defines a standard function code
     * for <code>write multiple coils</code>.
     */
    int WRITE_MULTIPLE_COILS = 15;

    /**
     * Defines the class 0 function code
     * for <code>write multiple registers</code>.
     */
    int WRITE_MULTIPLE_REGISTERS = 16;

    /**
     * Defines a standard function code
     * for <code>read slave ID</code>.
     */
    int REPORT_SLAVE_ID = 17;

    /**
     * <code>read file record</code>
     */
    int READ_FILE_RECORD = 20;

    /**
     * <code>write file record</code>
     */
    int WRITE_FILE_RECORD = 21;

    /**
     * <code>mask write register</code>
     *
     * Update a single register using its current value and an AND
     * and OR mask.
     */
    int MASK_WRITE_REGISTER = 22;

    /**
     * <code>read / write multiple registers</code>
     *
     * Write some number of registers, then read some number of
     * potentially other registers back.
     */
    int READ_WRITE_MULTIPLE = 23;

    /**
     * <code>read FIFO queue</code>
     *
     * Read from a FIFO queue.
     */
    int READ_FIFO_QUEUE = 24;

    /**
     * Defines the function code for reading
     * encapsulated data, such as vendor information.
     */
    int READ_MEI = 43;
    int READ_MEI_VENDOR_INFO = 14;

    /**
     * Defines the byte representation of the coil state <b>on</b>.
     */
    int COIL_ON = (byte)255;

    /**
     * Defines the byte representation of the coil state <b>pos</b>.
     */
    int COIL_OFF = 0;

    /**
     * Defines the word representation of the coil state <b>on</b>.
     */
    byte[] COIL_ON_BYTES = {(byte)COIL_ON, (byte)COIL_OFF};

    /**
     * Defines the word representation of the coil state <b>pos</b>.
     */
    byte[] COIL_OFF_BYTES = {(byte)COIL_OFF, (byte)COIL_OFF};

    /**
     * Defines the maximum number of bits in multiple read/write
     * of input discretes or coils (<b>2000</b>).
     */
    int MAX_BITS = 2000;

    /**
     * Defines the Modbus slave exception offset that is added to the
     * function code, to flag an exception.
     */
    int EXCEPTION_OFFSET = 128;            //the last valid function code is 127

    /**
     * Defines the Modbus slave exception type <code>illegal function</code>.
     * This exception code is returned if the slave:
     * <ul>
     * <li>does not implement the function code <b>or</b></li>
     * <li>is not in a state that allows it to process the function</li>
     * </ul>
     */
    int ILLEGAL_FUNCTION_EXCEPTION = 1;

    /**
     * Defines the Modbus slave exception type <code>illegal data address</code>.
     * This exception code is returned if the reference:
     * <ul>
     * <li>does not exist on the slave <b>or</b></li>
     * <li>the combination of reference and length exceeds the bounds
     * of the existing registers.
     * </li>
     * </ul>
     */
    int ILLEGAL_ADDRESS_EXCEPTION = 2;

    /**
     * Defines the Modbus slave exception type <code>illegal data value</code>.
     * This exception code indicates a fault in the structure of the data values
     * of a complex request, such as an incorrect implied length.<br>
     * <b>This code does not indicate a problem with application specific validity
     * of the value.</b>
     */
    int ILLEGAL_VALUE_EXCEPTION = 3;

    /**
     * Defines the Modbus slave exception type <code>slave device failure</code>.
     * This exception code indicates a fault in the slave device itself.
     */
    int SLAVE_DEVICE_FAILURE = 4;

    /**
     * Defines the Modbus slave exception type <code>slave busy</code>.  This
     * exception indicates the the slave is unable to perform the operation
     * because it is performing an operation which cannot be interrupted.
     */
    int SLAVE_BUSY_EXCEPTION = 6;

    /**
     * Defines the Modbus slave exception type <code>negative acknowledgment</code>.
     * This exception code indicates the slave cannot perform the requested
     * action.
     */
    int NEGATIVE_ACKNOWLEDGEMENT = 7;

    /**
     * Defines the Modbus slave exception type <code>Gateway target failed to
     * respond</code>.  This exception code indicates that a Modbus gateway
     * failed to receive a response from the specified target.
     */
    int GATEWAY_TARGET_NO_RESPONSE = 11;

    /**
     * Defines the default port number of Modbus
     * (=<code>502</code>).
     */
    int DEFAULT_PORT = 502;

    /**
     * Defines the maximum message length in bytes
     * (=<code>256</code>).
     */
    int MAX_MESSAGE_LENGTH = 256;

    /**
     * Defines the default transaction identifier (=<code>0</code>).
     */
    int DEFAULT_TRANSACTION_ID = 0;

    /**
     * Defines the default protocol identifier (=<code>0</code>).
     */
    int DEFAULT_PROTOCOL_ID = 0;

    /**
     * Defines the default unit identifier (=<code>0</code>).
     */
    int DEFAULT_UNIT_ID = 0;

    /**
     * Defines the default subnet identifier (=<code>0</code>).
     */
    int DEFAULT_SUBNET_ID = 0;

    /**
     * Defines the default setting for validity checking
     * in transactions (=<code>true</code>).
     */
    boolean DEFAULT_VALIDITYCHECK = true;

    /**
     * Defines the default setting for I/O operation timeouts
     * in milliseconds (=<code>3000</code>).
     */
    int DEFAULT_TIMEOUT = 3000;

    /**
     * Defines the sleep period between transaction retries
     * in milliseconds (=<code>200</code>).
     */
    int RETRY_SLEEP_TIME = 500;

    /**
     * Defines the default reconnecting setting for
     * transactions (=<code>false</code>).
     */
    boolean DEFAULT_RECONNECTING = false;

    /**
     * Defines the default amount of retires for opening
     * a connection (=<code>3</code>).
     */
    int DEFAULT_RETRIES = 5;

    /**
     * Defines the default number of msec to delay before transmission<br>
     * Inter-message delays are managed by the SerialTransaction object automatically based on the
     * baud rate. Setting this value to anything other than zero will bypass that process and force
     * a specific inter-message delay
     * (=<code>0</code>).
     */
    int DEFAULT_TRANSMIT_DELAY = 0;

    /**
     * Defines the default number of msec to delay before transmission if not overridden by DEFAULT_TRANSMIT_DELAY
     * (=<code>2</code>).
     */
    int MINIMUM_TRANSMIT_DELAY = 2;

    /**
     * The number of characters delay that must be maintained between adjacent requests on
     * the same serial port (within the same transaction)
     */
    double INTER_MESSAGE_GAP = 4;

    /**
     * The number of characters delay that is the allowed maximum between characters on
     * the same serial port (within the same transaction)
     */
    double INTER_CHARACTER_GAP = 1.5;
    
    /**
     * Defines the maximum value of the transaction identifier.
     *
     *<b>Note:</b> The standard requires that the server copy whatever
     * value the client provides. However, the transaction ID is being
     * limited to signed 16-bit integers to prevent problems with servers
     * that might incorrectly assume the value is a signed value.
     */
    int MAX_TRANSACTION_ID = Short.MAX_VALUE;

    /**
     * Defines the serial encoding "ASCII".
     */
    String SERIAL_ENCODING_ASCII = "ascii";

    /**
     * Defines the serial encoding "RTU".
     */
    String SERIAL_ENCODING_RTU = "rtu";

    /**
     * Defines the default serial encoding (ASCII).
     */
    String DEFAULT_SERIAL_ENCODING = SERIAL_ENCODING_ASCII;

}