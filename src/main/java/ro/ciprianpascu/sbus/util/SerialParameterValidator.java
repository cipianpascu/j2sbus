/**
 * Copyright (c) 2010-${year}, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ***/

package ro.ciprianpascu.sbus.util;

import java.util.Arrays;

import gnu.io.SerialPort;
import ro.ciprianpascu.sbus.Sbus;

/**
 * Utility class for validating serial communication parameters.
 * This class provides methods to validate various serial port settings
 * such as baud rate, data bits, stop bits, parity, and flow control.
 * It is used by SerialParameters to ensure valid configuration.
 *
 * @author Nick Mayerhofer
 */
public class SerialParameterValidator {

    /**
     * Private constructor to prevent instantiation of this utility class.
     * All methods in this class are static and should be accessed directly.
     */
    private SerialParameterValidator() {
        // Utility class, not meant to be instantiated
    }

    /** List of commonly supported baud rates for serial communication */
    public static final Integer[] COMMON_BAUD_RATES = { 
        75, 110, 300, 1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200 
    };

    /** List of valid stop bit values (1.0, 1.5, or 2.0) */
    public static final Double[] VALID_STOP_BITS = { 1.0, 1.5, 2.0 };

    /** List of valid parity settings from SerialPort constants */
    public static final Integer[] VALID_PARITYS = { 
        SerialPort.PARITY_NONE, 
        SerialPort.PARITY_EVEN,
        SerialPort.PARITY_ODD 
    };

    /** List of valid flow control settings as strings */
    public static final String[] VALID_FLOWCONTROL_STRINGS = { 
        "none", "xon/xoff out", "xon/xoff in", "rts/cts in", "rts/cts out" 
    };

    /** List of valid flow control settings as integer values */
    public static final Integer[] VALID_FLOWCONTROL_INT = { 
        0, (1 << 0), (1 << 1), (1 << 2), (1 << 3) 
    };

    /**
     * Validates if the given baud rate is supported.
     *
     * @param baudRate the baud rate to validate
     * @return true if the baud rate is in the list of common baud rates
     */
    public static boolean isBaudRateValid(int baudRate) {
        return Arrays.asList(COMMON_BAUD_RATES).contains(baudRate);
    }

    /**
     * Validates if the given number of data bits is valid.
     * Valid values are between DATABITS_5 and DATABITS_8 from SerialPort.
     *
     * @param databits the number of data bits to validate
     * @return true if the data bits value is valid
     */
    public static boolean isDataBitsValid(int databits) {
        return (databits >= SerialPort.DATABITS_5) && (databits <= SerialPort.DATABITS_8);
    }

    /**
     * Validates if the given number of stop bits is valid.
     * Valid values are 1.0, 1.5, and 2.0.
     *
     * @param stopbits the number of stop bits to validate
     * @return true if the stop bits value is valid
     */
    public static boolean isStopbitsValid(double stopbits) {
        return Arrays.asList(VALID_STOP_BITS).contains(stopbits);
    }

    /**
     * Validates if the given parity setting is valid.
     * Valid values are PARITY_NONE, PARITY_EVEN, and PARITY_ODD from SerialPort.
     *
     * @param parity the parity setting to validate
     * @return true if the parity setting is valid
     */
    public static boolean isParityValid(int parity) {
        return Arrays.asList(VALID_PARITYS).contains(parity);
    }

    /**
     * Validates if the given encoding is valid for SBus serial communication.
     *
     * @param enc the encoding to validate
     * @return true if the encoding is valid
     */
    public static boolean isEncodingValid(String enc) {
        return Arrays.asList(Sbus.validSerialEncodings).contains(enc);
    }

    /**
     * Validates if the given receive timeout value is valid.
     * The timeout must be greater than 0.
     *
     * @param receiveTimeout the timeout value in milliseconds
     * @return true if the timeout value is valid
     */
    public static boolean isReceiveTimeoutValid(int receiveTimeout) {
        return (receiveTimeout > 0);
    }

    /**
     * Validates if the given flow control string is valid.
     * Valid values are "none", "xon/xoff out", "xon/xoff in", 
     * "rts/cts in", and "rts/cts out".
     *
     * @param flowcontrol the flow control setting as a string
     * @return true if the flow control setting is valid
     */
    public static boolean isFlowControlValid(String flowcontrol) {
        return Arrays.asList(VALID_FLOWCONTROL_STRINGS).contains(flowcontrol);
    }

    /**
     * Validates if the given flow control integer value is valid.
     * Valid values are 0 and bit-shifted values 1, 2, 4, and 8.
     *
     * @param flowcontrol the flow control setting as an integer
     * @return true if the flow control setting is valid
     */
    public static boolean isFlowControlValid(int flowcontrol) {
        return Arrays.asList(VALID_FLOWCONTROL_INT).contains(flowcontrol);
    }
}
