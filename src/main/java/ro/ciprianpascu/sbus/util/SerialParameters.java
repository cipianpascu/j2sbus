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

package ro.ciprianpascu.sbus.util;

import java.util.Objects;
import java.util.Properties;

import gnu.io.SerialPort;
import ro.ciprianpascu.sbus.Modbus;

/**
 * Helper class for managing serial port communication parameters in the SBus protocol.
 * This class encapsulates all parameters needed for serial communication, including
 * port name, baud rate, flow control, data bits, stop bits, parity, encoding, and
 * timeout settings. It provides methods for setting and getting these parameters,
 * with validation to ensure proper configuration.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu
 * @author John Charlton
 * @version %I% (%G%)
 */
public class SerialParameters {

    /** Default timeout for receiving data from serial port (1.5 seconds) */
    public static int DEFAULT_RECEIVE_TIMEOUT_MILLIS = 1500;

    /** Name of the serial port (e.g., COM1, /dev/ttyUSB0) */
    private String m_PortName;
    
    /** Communication speed in bits per second */
    private int m_BaudRate;
    
    /** Flow control setting for receiving data */
    private int m_FlowControlIn;
    
    /** Flow control setting for sending data */
    private int m_FlowControlOut;
    
    /** Number of data bits per character */
    private int m_Databits;
    
    /** Number of stop bits per character */
    private int m_Stopbits;
    
    /** Parity checking mode */
    private int m_Parity;
    
    /** Message encoding format (ASCII, RTU, or BIN) */
    private String m_Encoding;
    
    /** Flag for RS485 echo mode */
    private boolean m_Echo;
    
    /** Timeout for receiving data in milliseconds */
    private int m_ReceiveTimeoutMillis;

    /**
     * Constructs a new SerialParameters instance with default values:
     * 9600 baud - 8N1 - ASCII encoding.
     */
    public SerialParameters() {
        this("", 9600, SerialPort.FLOWCONTROL_NONE, SerialPort.FLOWCONTROL_NONE, SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, Modbus.DEFAULT_SERIAL_ENCODING, false,
                DEFAULT_RECEIVE_TIMEOUT_MILLIS);
    }

    /**
     * Constructs a new SerialParameters instance with given parameters.
     *
     * @param portName The name of the serial port
     * @param baudRate The communication speed in bits per second
     * @param flowControlIn Type of flow control for receiving data
     * @param flowControlOut Type of flow control for sending data
     * @param databits The number of data bits per character (5-8)
     * @param stopbits The number of stop bits (1, 1.5, or 2)
     * @param parity The type of parity checking (NONE, EVEN, ODD)
     * @param encoding The message encoding format (ASCII, RTU, BIN)
     * @param echo Flag for enabling RS485 echo mode
     * @param receiveTimeoutMillis Timeout for receiving data in milliseconds
     */
    public SerialParameters(String portName, int baudRate, int flowControlIn, int flowControlOut, int databits,
            int stopbits, int parity, String encoding, boolean echo, int receiveTimeoutMillis) {
        setPortName(portName);
        setBaudRate(baudRate);
        setFlowControlIn(flowControlIn);
        setFlowControlOut(flowControlOut);
        setDatabits(databits);
        setStopbits(stopbits);
        setParity(parity);
        setEncoding(encoding);
        setEcho(echo);
        setReceiveTimeoutMillis(receiveTimeoutMillis);
    }

    /**
     * Constructs a new SerialParameters instance from Properties.
     * Loads serial parameters from a Properties object, using the given prefix
     * to locate the relevant properties.
     *
     * @param props Properties object containing serial parameters
     * @param prefix Prefix for property keys (can be empty string)
     */
    public SerialParameters(Properties props, String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        setPortName(props.getProperty(prefix + "portName", ""));
        setBaudRate(props.getProperty(prefix + "baudRate", "" + 9600));
        setFlowControlIn(props.getProperty(prefix + "flowControlIn", "" + SerialPort.FLOWCONTROL_NONE));
        setFlowControlOut(props.getProperty(prefix + "flowControlOut", "" + SerialPort.FLOWCONTROL_NONE));
        setParity(props.getProperty(prefix + "parity", "" + SerialPort.PARITY_NONE));
        setDatabits(props.getProperty(prefix + "databits", "" + SerialPort.DATABITS_8));
        setStopbits(props.getProperty(prefix + "stopbits", "" + SerialPort.STOPBITS_1));
        setEncoding(props.getProperty(prefix + "encoding", Modbus.DEFAULT_SERIAL_ENCODING));
        setEcho("true".equals(props.getProperty(prefix + "echo")));
        setReceiveTimeoutMillis(props.getProperty(prefix + "timeout", "" + DEFAULT_RECEIVE_TIMEOUT_MILLIS));
    }

    /**
     * Sets the port name.
     *
     * @param name the new port name
     */
    public void setPortName(String name) {
        m_PortName = name;
    }

    /**
     * Returns the port name.
     *
     * @return the port name
     */
    public String getPortName() {
        return m_PortName;
    }

    /**
     * Sets the baud rate.
     *
     * @param rate the new baud rate
     * @throws IllegalArgumentException if the baud rate is not valid
     */
    public void setBaudRate(int rate) {
        if (!SerialParameterValidator.isBaudRateValid(rate)) {
            throw new IllegalArgumentException("Invalid baud rate: " + rate);
        }
        m_BaudRate = rate;
    }

    /**
     * Sets the baud rate from a string value.
     *
     * @param rate the new baud rate as a string
     * @throws IllegalArgumentException if the string cannot be parsed or rate is invalid
     */
    public void setBaudRate(String rate) {
        try {
            setBaudRate(Integer.parseInt(rate));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid baud rate string: " + rate);
        }
    }

    /**
     * Returns the baud rate.
     *
     * @return the current baud rate
     */
    public int getBaudRate() {
        return m_BaudRate;
    }

    /**
     * Returns the baud rate as a string.
     *
     * @return the current baud rate as a string
     */
    public String getBaudRateString() {
        return Integer.toString(m_BaudRate);
    }

    /**
     * Sets the flow control for receiving data.
     *
     * @param flowcontrol the new flow control setting
     * @throws IllegalArgumentException if the flow control value is invalid
     */
    public void setFlowControlIn(int flowcontrol) {
        if (!SerialParameterValidator.isFlowControlValid(flowcontrol)) {
            throw new IllegalArgumentException("Invalid flow control: " + flowcontrol);
        }
        m_FlowControlIn = flowcontrol;
    }

    /**
     * Sets the flow control for receiving data from a string value.
     *
     * @param flowcontrol the new flow control setting as a string
     * @throws IllegalArgumentException if the flow control string is invalid
     */
    public void setFlowControlIn(String flowcontrol) {
        if (!SerialParameterValidator.isFlowControlValid(flowcontrol)) {
            throw new IllegalArgumentException("Invalid flow control string: " + flowcontrol);
        }
        m_FlowControlIn = stringToFlow(flowcontrol);
    }

    /**
     * Returns the input flow control setting.
     *
     * @return the current input flow control setting
     */
    public int getFlowControlIn() {
        return m_FlowControlIn;
    }

    /**
     * Returns the input flow control setting as a string.
     *
     * @return the current input flow control setting as a string
     */
    public String getFlowControlInString() {
        return flowToString(m_FlowControlIn);
    }

    /**
     * Sets the flow control for sending data.
     *
     * @param flowControlOut the new flow control setting
     * @throws IllegalArgumentException if the flow control value is invalid
     */
    public void setFlowControlOut(int flowControlOut) {
        if (!SerialParameterValidator.isFlowControlValid(flowControlOut)) {
            throw new IllegalArgumentException("Invalid flow control: " + flowControlOut);
        }
        m_FlowControlOut = flowControlOut;
    }

    /**
     * Sets the flow control for sending data from a string value.
     *
     * @param flowControlOut the new flow control setting as a string
     * @throws IllegalArgumentException if the flow control string is invalid
     */
    public void setFlowControlOut(String flowControlOut) {
        if (!SerialParameterValidator.isFlowControlValid(flowControlOut)) {
            throw new IllegalArgumentException("Invalid flow control string: " + flowControlOut);
        }
        m_FlowControlOut = stringToFlow(flowControlOut);
    }

    /**
     * Returns the output flow control setting.
     *
     * @return the current output flow control setting
     */
    public int getFlowControlOut() {
        return m_FlowControlOut;
    }

    /**
     * Returns the output flow control setting as a string.
     *
     * @return the current output flow control setting as a string
     */
    public String getFlowControlOutString() {
        return flowToString(m_FlowControlOut);
    }

    /**
     * Sets the number of data bits.
     *
     * @param databits the new number of data bits (5-8)
     * @throws IllegalArgumentException if the data bits value is invalid
     */
    public void setDatabits(int databits) {
        if (!SerialParameterValidator.isDataBitsValid(databits)) {
            throw new IllegalArgumentException("Invalid data bits: " + databits);
        }
        m_Databits = databits;
    }

    /**
     * Sets the number of data bits from a string value.
     *
     * @param databits the new number of data bits as a string
     * @throws IllegalArgumentException if the string cannot be parsed or value is invalid
     */
    public void setDatabits(String databits) {
        try {
            setDatabits(Integer.parseInt(databits));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid data bits string: " + databits);
        }
    }

    /**
     * Returns the number of data bits.
     *
     * @return the current number of data bits
     */
    public int getDatabits() {
        return m_Databits;
    }

    /**
     * Returns the number of data bits as a string.
     *
     * @return the current number of data bits as a string
     */
    public String getDatabitsString() {
        return Integer.toString(m_Databits);
    }

    /**
     * Sets the number of stop bits.
     *
     * @param stopbits the new number of stop bits (1, 1.5, or 2)
     * @throws IllegalArgumentException if the stop bits value is invalid
     */
    public void setStopbits(double stopbits) {
        if (!SerialParameterValidator.isStopbitsValid(stopbits)) {
            throw new IllegalArgumentException("Invalid stop bits: " + stopbits);
        }
        if (stopbits == 1) {
            m_Stopbits = SerialPort.STOPBITS_1;
        } else if (stopbits == 1.5) {
            m_Stopbits = SerialPort.STOPBITS_1_5;
        } else if (stopbits == 2) {
            m_Stopbits = SerialPort.STOPBITS_2;
        }
    }

    /**
     * Sets the number of stop bits from a string value.
     *
     * @param stopbits the new number of stop bits as a string
     * @throws IllegalArgumentException if the string cannot be parsed or value is invalid
     */
    public void setStopbits(String stopbits) {
        try {
            setStopbits(Double.parseDouble(stopbits));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid stop bits string: " + stopbits);
        }
    }

    /**
     * Returns the number of stop bits.
     *
     * @return the current number of stop bits
     */
    public int getStopbits() {
        return m_Stopbits;
    }

    /**
     * Returns the number of stop bits as a string.
     *
     * @return the current number of stop bits as a string
     */
    public String getStopbitsString() {
        switch (m_Stopbits) {
            case SerialPort.STOPBITS_1:
                return "1";
            case SerialPort.STOPBITS_1_5:
                return "1.5";
            case SerialPort.STOPBITS_2:
                return "2";
            default:
                return "1";
        }
    }

    /**
     * Sets the parity mode.
     *
     * @param parity the new parity mode (NONE, EVEN, or ODD)
     * @throws IllegalArgumentException if the parity value is invalid
     */
    public void setParity(int parity) {
        if (!SerialParameterValidator.isParityValid(parity)) {
            throw new IllegalArgumentException("Invalid parity: " + parity);
        }
        m_Parity = parity;
    }

    /**
     * Sets the parity mode from a string value.
     *
     * @param parity the new parity mode as a string
     * @throws IllegalArgumentException if the parity string is invalid
     */
    public void setParity(String parity) {
        parity = parity.toLowerCase();
        if (parity.equals("none") || parity.equals("n")) {
            setParity(SerialPort.PARITY_NONE);
        } else if (parity.equals("even") || parity.equals("e")) {
            setParity(SerialPort.PARITY_EVEN);
        } else if (parity.equals("odd") || parity.equals("o")) {
            setParity(SerialPort.PARITY_ODD);
        } else {
            throw new IllegalArgumentException("Invalid parity string: " + parity);
        }
    }

    /**
     * Returns the parity mode.
     *
     * @return the current parity mode
     */
    public int getParity() {
        return m_Parity;
    }

    /**
     * Returns the parity mode as a string.
     *
     * @return the current parity mode as a string
     */
    public String getParityString() {
        switch (m_Parity) {
            case SerialPort.PARITY_NONE:
                return "none";
            case SerialPort.PARITY_EVEN:
                return "even";
            case SerialPort.PARITY_ODD:
                return "odd";
            default:
                return "none";
        }
    }

    /**
     * Sets the message encoding format.
     *
     * @param enc the new encoding format (ASCII, RTU, or BIN)
     * @throws IllegalArgumentException if the encoding string is invalid
     */
    public void setEncoding(String enc) {
        enc = enc.toLowerCase();
        if (!SerialParameterValidator.isEncodingValid(enc)) {
            throw new IllegalArgumentException("Invalid encoding: " + enc);
        }
        m_Encoding = enc;
    }

    /**
     * Returns the message encoding format.
     *
     * @return the current encoding format
     */
    public String getEncoding() {
        return m_Encoding;
    }

    /**
     * Returns whether RS485 echo mode is enabled.
     *
     * @return true if echo mode is enabled, false otherwise
     */
    public boolean isEcho() {
        return m_Echo;
    }

    /**
     * Sets the RS485 echo mode.
     *
     * @param newEcho true to enable echo mode, false to disable
     */
    public void setEcho(boolean newEcho) {
        m_Echo = newEcho;
    }

    /**
     * Returns the receive timeout value.
     *
     * @return the current receive timeout in milliseconds
     */
    public int getReceiveTimeoutMillis() {
        return m_ReceiveTimeoutMillis;
    }

    /**
     * Sets the receive timeout value.
     *
     * @param receiveTimeout the new timeout value in milliseconds
     * @throws IllegalArgumentException if the timeout value is negative
     */
    public void setReceiveTimeoutMillis(int receiveTimeout) {
        if (!SerialParameterValidator.isReceiveTimeoutValid(receiveTimeout)) {
            throw new IllegalArgumentException("Invalid timeout value: " + receiveTimeout);
        }
        m_ReceiveTimeoutMillis = receiveTimeout;
    }

    /**
     * Sets the receive timeout value from a string.
     *
     * @param str the new timeout value as a string
     * @throws IllegalArgumentException if the string cannot be parsed or value is invalid
     */
    public void setReceiveTimeoutMillis(String str) {
        try {
            setReceiveTimeoutMillis(Integer.parseInt(str));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid timeout string: " + str);
        }
    }

    @Override
    public String toString() {
        return "SerialParameters [portName=" + m_PortName +
               ", baudRate=" + m_BaudRate +
               ", flowControlIn=" + getFlowControlInString() +
               ", flowControlOut=" + getFlowControlOutString() +
               ", databits=" + m_Databits +
               ", stopbits=" + getStopbitsString() +
               ", parity=" + getParityString() +
               ", encoding=" + m_Encoding +
               ", echo=" + m_Echo +
               ", receiveTimeout=" + m_ReceiveTimeoutMillis + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_BaudRate, m_Databits, m_Echo, m_Encoding,
                          m_FlowControlIn, m_FlowControlOut, m_Parity,
                          m_PortName, m_ReceiveTimeoutMillis, m_Stopbits);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        SerialParameters rhs = (SerialParameters) obj;
        return m_BaudRate == rhs.m_BaudRate &&
               m_Databits == rhs.m_Databits &&
               m_Echo == rhs.m_Echo &&
               Objects.equals(m_Encoding, rhs.m_Encoding) &&
               m_FlowControlIn == rhs.m_FlowControlIn &&
               m_FlowControlOut == rhs.m_FlowControlOut &&
               m_Parity == rhs.m_Parity &&
               Objects.equals(m_PortName, rhs.m_PortName) &&
               m_ReceiveTimeoutMillis == rhs.m_ReceiveTimeoutMillis &&
               m_Stopbits == rhs.m_Stopbits;
    }

    /**
     * Converts a flow control string to its corresponding integer value.
     *
     * @param flowcontrol the flow control string
     * @return the corresponding SerialPort flow control constant
     */
    private int stringToFlow(String flowcontrol) {
        flowcontrol = flowcontrol.toLowerCase();
        if (flowcontrol.equals("none")) {
            return SerialPort.FLOWCONTROL_NONE;
        }
        if (flowcontrol.equals("xon/xoff out")) {
            return SerialPort.FLOWCONTROL_XONXOFF_OUT;
        }
        if (flowcontrol.equals("xon/xoff in")) {
            return SerialPort.FLOWCONTROL_XONXOFF_IN;
        }
        if (flowcontrol.equals("rts/cts in")) {
            return SerialPort.FLOWCONTROL_RTSCTS_IN;
        }
        if (flowcontrol.equals("rts/cts out")) {
            return SerialPort.FLOWCONTROL_RTSCTS_OUT;
        }
        return SerialPort.FLOWCONTROL_NONE;
    }

    /**
     * Converts a flow control integer value to its string representation.
     *
     * @param flowcontrol the SerialPort flow control constant
     * @return the corresponding flow control string
     */
    private String flowToString(int flowcontrol) {
        switch (flowcontrol) {
            case SerialPort.FLOWCONTROL_NONE:
                return "none";
            case SerialPort.FLOWCONTROL_XONXOFF_OUT:
                return "xon/xoff out";
            case SerialPort.FLOWCONTROL_XONXOFF_IN:
                return "xon/xoff in";
            case SerialPort.FLOWCONTROL_RTSCTS_IN:
                return "rts/cts in";
            case SerialPort.FLOWCONTROL_RTSCTS_OUT:
                return "rts/cts out";
            default:
                return "none";
        }
    }
}
