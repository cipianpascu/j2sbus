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

package ro.ciprianpascu.sbus;

/**
 * Interface defining all constants related to the
 * Modbus protocol.
 *
 * @author Dieter Wimberger
 * @version %I% (%G%)
 */
public interface Modbus {

    /**
     * JVM flag for debug mode. Can be set passing the system property
     * ro.ciprianpascu.sbus.debug=false|true (-D flag to the jvm).
     */
    public static final boolean debug = "true".equals(System.getProperty("ro.ciprianpascu.sbus.debug"));

    /**
     * Defines the class 0 function code
     * for {@link READ_MULTIPLE_REGISTERS}.
     */
    public static final int READ_MULTIPLE_REGISTERS = 3;

    /**
     * Defines the class 0 function code
     * for {@link WRITE_MULTIPLE_REGISTERS}.
     */
    public static final int WRITE_MULTIPLE_REGISTERS = 16;


    /**
     * Defines a class 1 function code
     * for {@link READ_STATUS_CHANNELS_REQUEST}.
     */
    public static final int READ_STATUS_CHANNELS_REQUEST = 0x0033;

    /**
     * Defines a class 1 function code
     * for {@link READ_STATUS_CHANNELS_REQUEST}.
     */
    public static final int READ_STATUS_CHANNELS_RESPONSE = 0x0034;

    /**
     * Defines a class 1 function code
     * for {@link WRITE_SINGLE_CHANNEL_REQUEST}.
     */
    public static final int WRITE_SINGLE_CHANNEL_REQUEST = 0x0031;

    /**
     * Defines a class 1 function code
     * for {@link WRITE_SINGLE_CHANNEL_RESPONSE}.
     */
    public static final int WRITE_SINGLE_CHANNEL_RESPONSE = 0x0032;


    /**
     * Defines the maximum number of bits in multiple read/write
     * of input discretes or coils (<b>2000</b>).
     */
    public static final int MAX_BITS = 2000;

    /**
     * Defines the Modbus slave exception offset that is added to the
     * function code, to flag an exception.
     */
    public static final int EXCEPTION_OFFSET = 128; // the last valid function code is 127

    /**
     * Defines the Modbus slave exception type {@link ILLEGAL_FUNCTION_EXCEPTION}.
     * This exception code is returned if the slave:
     * <ul>
     * <li>does not implement the function code <b>or</b></li>
     * <li>is not in a state that allows it to process the function</li>
     * </ul>
     */
    public static final int ILLEGAL_FUNCTION_EXCEPTION = 1;

    /**
     * Defines the Modbus slave exception type {@link ILLEGAL_ADDRESS_EXCEPTION}.
     * This exception code is returned if the reference:
     * <ul>
     * <li>does not exist on the slave <b>or</b></li>
     * <li>the combination of reference and length exceeds the bounds
     * of the existing registers.
     * </li>
     * </ul>
     */
    public static final int ILLEGAL_ADDRESS_EXCEPTION = 2;

    /**
     * Defines the Modbus slave exception type {@link ILLEGAL_VALUE_EXCEPTION}.
     * This exception code indicates a fault in the structure of the data values
     * of a complex request, such as an incorrect implied length.<br>
     * This code does not indicate a problem with application specific validity
     * of the value.
     */
    public static final int ILLEGAL_VALUE_EXCEPTION = 3;

    /**
     * Defines the default port number of Modbus
     * (=502).
     */
    public static final int DEFAULT_PORT = 6000;

    /**
     * Defines the maximum message length in bytes
     * (=256).
     */
    public static final int MAX_MESSAGE_LENGTH = 256;

    /**
     * Defines the default transaction identifier (=0).
     */
    public static final int DEFAULT_TRANSACTION_ID = 0;

    /**
     * Defines the default protocol identifier (=0).
     */
    public static final int DEFAULT_PROTOCOL_ID = 0;

    /**
     * Defines the default unit identifier (=0).
     */
    public static final int DEFAULT_UNIT_ID = 0;

    /**
     * Defines the default unit identifier (=1).
     */
    public static final int DEFAULT_SUBNET_ID = 1;

    /**
     * Defines the default unit identifier (=254).
     */
    public static final int DEFAULT_SOURCE_UNIT_ID = 254;

    /**
     * Defines the default unit identifier (=1).
     */
    public static final int DEFAULT_SOURCE_SUBNET_ID = 1;

    /**
     * Defines the default unit identifier (=0).
     */
    public static final int DEFAULT_SOURCE_DEVICE_TYPE = 0xFFFE;

    /**
     * Defines the default setting for validity checking
     * in transactions (=true).
     */
    public static final boolean DEFAULT_VALIDITYCHECK = true;

    /**
     * Defines the default setting for I/O operation timeouts
     * in milliseconds (=3000).
     */
    public static final int DEFAULT_TIMEOUT = 3000;

    /**
     * Defines the default reconnecting setting for
     * transactions (=false).
     */
    public static final boolean DEFAULT_RECONNECTING = false;

    /**
     * Defines the default amount of retires for opening
     * a connection (=3).
     */
    public static final int DEFAULT_RETRIES = 3;

    /**
     * Defines the default number of msec to delay before transmission
     * (=50).
     */
    public static final int DEFAULT_TRANSMIT_DELAY = 0;

    /**
     * Defines the maximum value of the transaction identifier.
     */
    public static final int MAX_TRANSACTION_ID = (Short.MAX_VALUE * 2) - 1;

    /**
     * Defines the serial encoding "ASCII".
     */
    public static final String SERIAL_ENCODING_ASCII = "ascii";

    /**
     * Defines the serial encoding "RTU".
     */
    public static final String SERIAL_ENCODING_RTU = "rtu";

    /**
     * Defines the serial encoding "BIN".
     */
    public static final String SERIAL_ENCODING_BIN = "bin";

    /**
     * Defines the default serial encoding (ASCII).
     */
    public static final String DEFAULT_SERIAL_ENCODING = SERIAL_ENCODING_ASCII;

    /**
     * presents a list of valid modbus serial encoding options
     */
    public static final String[] validSerialEncodings = { SERIAL_ENCODING_ASCII, SERIAL_ENCODING_RTU,
            SERIAL_ENCODING_BIN };

}// class Modbus
