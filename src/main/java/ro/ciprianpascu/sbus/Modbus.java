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
 * SBus protocol.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public interface Modbus {

    /**
     * JVM flag for debug mode. Can be set passing the system property
     * ro.ciprianpascu.sbus.debug=false|true (-D flag to the jvm).
     */
    public static final boolean debug = "true".equals(System.getProperty("ro.ciprianpascu.sbus.debug"));
    
    /**
     * Defines the success response code (0xF8)
     */
    public static final int SUCCESS = 0xF8;

    /**
     * Defines the failure response code (0xF5)
     */
    public static final int FAILURE = 0xF5;

    /**
     * Function code for reading multiple registers (0xDD28)
     */
    public static final int READ_MULTIPLE_REGISTERS = 0xDD28;

    /**
     * Function code for writing multiple registers (0xDD2A)
     */
    public static final int WRITE_MULTIPLE_REGISTERS = 0xDD2A;

    /**
     * Function code for reading status channels (0x0033)
     */
    public static final int READ_STATUS_CHANNELS_REQUEST = 0x0033;

    /**
     * Function code for reading temperature (0xE3E7)
     */
    public static final int READ_TEMPERATURE_REQUEST = 0xE3E7;

    /**
     * Function code for writing to a single channel (0x0031)
     */
    public static final int WRITE_SINGLE_CHANNEL_REQUEST = 0x0031;

    /**
     * Function code for reading RGBW values (0xF016)
     */
    public static final int READ_RGBW_REQUEST = 0xF016;

    /**
     * Function code for writing RGBW values (0xF080)
     */
    public static final int WRITE_RGBW_REQUEST = 0xF080;
    
    /**
     * Defines the maximum number of bits in multiple read/write
     * of input discretes or coils (2000).
     */
    public static final int MAX_BITS = 2000;

    /**
     * Defines the SBus slave exception offset that is added to the
     * function code, to flag an exception.
     */
    public static final int EXCEPTION_OFFSET = 128; // the last valid function code is 127

    /**
     * Exception code returned when the slave does not implement the function code
     * or is not in a state that allows it to process the function.
     */
    public static final int ILLEGAL_FUNCTION_EXCEPTION = 1;

    /**
     * Exception code returned when the reference does not exist on the slave
     * or the combination of reference and length exceeds the bounds
     * of the existing registers.
     */
    public static final int ILLEGAL_ADDRESS_EXCEPTION = 2;

    /**
     * Exception code indicating a fault in the structure of the data values
     * of a complex request, such as an incorrect implied length.
     * This code does not indicate a problem with application specific validity
     * of the value.
     */
    public static final int ILLEGAL_VALUE_EXCEPTION = 3;

    /**
     * Default port number for SBus protocol (6000)
     */
    public static final int DEFAULT_PORT = 6000;

    /**
     * Maximum message length in bytes (256)
     */
    public static final int MAX_MESSAGE_LENGTH = 256;

    /**
     * Default transaction identifier (0)
     */
    public static final int DEFAULT_TRANSACTION_ID = 0;

    /**
     * Default unit identifier (0)
     */
    public static final int DEFAULT_UNIT_ID = 0;

    /**
     * Default subnet identifier (1)
     */
    public static final int DEFAULT_SUBNET_ID = 1;

    /**
     * Default source unit identifier (0xBB)
     */
    public static final int DEFAULT_SOURCE_UNIT_ID = 0xBB;

    /**
     * Default source subnet identifier (0xBB)
     */
    public static final int DEFAULT_SOURCE_SUBNET_ID = 0xBB;

    /**
     * Default source device type (0xCCCC)
     */
    public static final int DEFAULT_SOURCE_DEVICE_TYPE = 0xCCCC;

    /**
     * Default setting for validity checking in transactions (true)
     */
    public static final boolean DEFAULT_VALIDITYCHECK = true;

    /**
     * Default I/O operation timeout in milliseconds (3000)
     */
    public static final int DEFAULT_TIMEOUT = 3000;

    /**
     * Default reconnecting setting for transactions (false)
     */
    public static final boolean DEFAULT_RECONNECTING = false;

    /**
     * Default number of retries for opening a connection (3)
     */
    public static final int DEFAULT_RETRIES = 3;

    /**
     * Default delay before transmission in milliseconds (0)
     */
    public static final int DEFAULT_TRANSMIT_DELAY = 0;

    /**
     * Maximum value of the transaction identifier
     */
    public static final int MAX_TRANSACTION_ID = (Short.MAX_VALUE * 2) - 1;

    /**
     * Serial encoding type "ASCII"
     */
    public static final String SERIAL_ENCODING_ASCII = "ascii";

    /**
     * Serial encoding type "RTU"
     */
    public static final String SERIAL_ENCODING_RTU = "rtu";

    /**
     * Serial encoding type "BIN"
     */
    public static final String SERIAL_ENCODING_BIN = "bin";

    /**
     * Default serial encoding (ASCII)
     */
    public static final String DEFAULT_SERIAL_ENCODING = SERIAL_ENCODING_ASCII;

    /**
     * List of valid serial encoding options
     */
    public static final String[] validSerialEncodings = {
        SERIAL_ENCODING_ASCII,
        SERIAL_ENCODING_RTU,
        SERIAL_ENCODING_BIN
    };

}
