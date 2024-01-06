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
 * Helper class wrapping all serial port communication parameters.
 * Very similar to the gnu.io demos, however, not the same.
 *
 * @author Dieter Wimberger
 * @author John Charlton
 * @version %I% (%G%)
 */
public class SerialParameters {

    public static int DEFAULT_RECEIVE_TIMEOUT_MILLIS = 1500; // 1.5 secs

    // instance attributes
    private String m_PortName;
    private int m_BaudRate;
    private int m_FlowControlIn;
    private int m_FlowControlOut;
    private int m_Databits;
    private int m_Stopbits;
    private int m_Parity;
    private String m_Encoding;
    private boolean m_Echo;
    private int m_ReceiveTimeoutMillis;

    /**
     * Constructs a new {@link SerialParameters} instance with
     * default values: 9600 boud - 8N1 - ASCII.
     */
    public SerialParameters() {
        this("", 9600, SerialPort.FLOWCONTROL_NONE, SerialPort.FLOWCONTROL_NONE, SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, Modbus.DEFAULT_SERIAL_ENCODING, false,
                DEFAULT_RECEIVE_TIMEOUT_MILLIS);
    }// constructor

    /**
     * Constructs a new {@link SerialParameters}  instance with
     * given parameters.
     *
     * @param portName The name of the port.
     * @param baudRate The baud rate.
     * @param flowControlIn Type of flow control for receiving.
     * @param flowControlOut Type of flow control for sending.
     * @param databits The number of data bits.
     * @param stopbits The number of stop bits.
     * @param encoding
     * @param parity The type of parity.
     * @param echo Flag for setting the RS485 echo mode.
     * @param receiveTimeoutMillis timeout in milliseconds for read operations
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
    }// constructor

    @Override
    public String toString() {
        return "SerialParameters [m_PortName=" + m_PortName + ", m_BaudRate=" + m_BaudRate + ", m_FlowControlIn="
                + m_FlowControlIn + ", m_FlowControlOut=" + m_FlowControlOut + ", m_Databits=" + m_Databits
                + ", m_Stopbits=" + m_Stopbits + ", m_Parity=" + m_Parity + ", m_Encoding=" + m_Encoding + ", m_Echo="
                + m_Echo + ", m_ReceiveTimeoutMillis=" + m_ReceiveTimeoutMillis + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_BaudRate, m_Databits, m_Echo, m_Encoding, m_FlowControlIn, m_FlowControlOut, m_Parity,
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
        return m_BaudRate == rhs.m_BaudRate && m_Databits == rhs.m_Databits && m_Echo == rhs.m_Echo
                && Objects.equals(m_Encoding, rhs.m_Encoding) && m_FlowControlIn == rhs.m_FlowControlIn
                && m_FlowControlOut == rhs.m_FlowControlOut && m_Parity == rhs.m_Parity
                && Objects.equals(m_PortName, rhs.m_PortName) && m_ReceiveTimeoutMillis == rhs.m_ReceiveTimeoutMillis
                && m_Stopbits == rhs.m_Stopbits;
    }

    /**
     * Constructs a new {@link SerialParameters} instance with
     * parameters obtained from a {@link Properties} instance.
     *
     * @param props a {@link Properties} instance.
     * @param prefix a prefix for the properties keys if embedded into
     *            other properties.
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
        setReceiveTimeoutMillis(props.getProperty(prefix + "timeout", "" + 500));
    }// constructor

    /**
     * Sets the port name.
     *
     * @param name the new port name.
     */
    public void setPortName(String name) {
        m_PortName = name;
    }// setPortName

    /**
     * Returns the port name.
     *
     * @return the port name.
     */
    public String getPortName() {
        return m_PortName;
    }// getPortName

    /**
     * Sets the baud rate.
     *
     * @param rate the new baud rate.
     */
    public void setBaudRate(int rate) throws IllegalArgumentException {
        if (!SerialParameterValidator.isBaudRateValid(rate)) {
            throw new IllegalArgumentException("invalid baud rate: " + Integer.toString(rate));
        }
        m_BaudRate = rate;
    }// setBaudRate

    /**
     * Sets the baud rate.
     *
     * @param rate the new baud rate.
     */
    public void setBaudRate(String rate) throws IllegalArgumentException {
        int intBaudRate = 0;
        try {
            intBaudRate = Integer.parseInt(rate);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "baudString '" + rate + "' can not be converted to a number: " + e.getMessage());
        }
        setBaudRate(intBaudRate);
    }// setBaudRate

    /**
     * Return the baud rate as {@link int}.
     *
     * @return the baud rate as {@link int}.
     */
    public int getBaudRate() {
        return m_BaudRate;
    }// getBaudRate

    /**
     * Returns the baud rate as a {@link String}.
     *
     * @return the baud rate as {@link String}.
     */
    public String getBaudRateString() {
        return Integer.toString(m_BaudRate);
    }// getBaudRateString

    /**
     * Sets the type of flow control for the input
     * as given by the passed in {@link int}.
     *
     * @param flowcontrol the new flow control type.
     */
    public void setFlowControlIn(int flowcontrol) throws IllegalArgumentException {
        if (!SerialParameterValidator.isFlowControlValid(flowcontrol)) {
            throw new IllegalArgumentException("flowcontrol int '" + flowcontrol + "' invalid");
        }

        m_FlowControlIn = flowcontrol;
    }// setFlowControl

    /**
     * Sets the type of flow control for the input
     * as given by the passed in {@link String}.
     *
     * @param flowcontrol the flow control for reading type.
     */
    public void setFlowControlIn(String flowcontrol) throws IllegalArgumentException {
        if (!SerialParameterValidator.isFlowControlValid(flowcontrol)) {
            throw new IllegalArgumentException("flowcontrolIn string '" + flowcontrol + "' unknown");
        }

        setFlowControlIn(stringToFlow(flowcontrol));
    }// setFlowControlIn

    /**
     * Returns the input flow control type as {@link int}.
     *
     * @return the input flow control type as {@link int}.
     */
    public int getFlowControlIn() {
        return m_FlowControlIn;
    }// getFlowControlIn

    /**
     * Returns the input flow control type as {@link String}.
     *
     * @return the input flow control type as {@link String}.
     */
    public String getFlowControlInString() {
        return flowToString(m_FlowControlIn);
    }// getFlowControlIn

    /**
     * Sets the output flow control type as given
     * by the passed in {@link int}.
     *
     * @param flowControlOut new output flow control type as {@link int}.
     */
    public void setFlowControlOut(int flowControlOut) throws IllegalArgumentException {
        if (!SerialParameterValidator.isFlowControlValid(flowControlOut)) {
            throw new IllegalArgumentException("flowcontrol int '" + flowControlOut + "' unknown");
        }

        m_FlowControlOut = flowControlOut;
    }// setFlowControlOut

    /**
     * Sets the output flow control type as given
     * by the passed in {@link String}.
     *
     * @param flowControlOut the new output flow control type as {@link String}.
     */
    public void setFlowControlOut(String flowControlOut) throws IllegalArgumentException {
        if (!SerialParameterValidator.isFlowControlValid(flowControlOut)) {
            throw new IllegalArgumentException("flowcontrol string '" + flowControlOut + "' unknown");
        }

        m_FlowControlOut = stringToFlow(flowControlOut);
    }// setFlowControlOut

    /**
     * Returns the output flow control type as {@link int}.
     *
     * @return the output flow control type as {@link int}.
     */
    public int getFlowControlOut() {
        return m_FlowControlOut;
    }// getFlowControlOut

    /**
     * Returns the output flow control type as {@link String}.
     *
     * @return the output flow control type as {@link String}.
     */
    public String getFlowControlOutString() {
        return flowToString(m_FlowControlOut);
    }// getFlowControlOutString

    /**
     * Sets the number of data bits.
     *
     * @param databits the new number of data bits.
     */
    public void setDatabits(int databits) throws IllegalArgumentException {
        if (!SerialParameterValidator.isDataBitsValid(databits)) {
            throw new IllegalArgumentException("Databit '" + databits + "' invalid");
        }

        switch (databits) {
            case 5:
                m_Databits = SerialPort.DATABITS_5;
                break;
            case 6:
                m_Databits = SerialPort.DATABITS_6;
                break;
            case 7:
                m_Databits = SerialPort.DATABITS_7;
                break;
            case 8:
                m_Databits = SerialPort.DATABITS_8;
                break;
            default:
                m_Databits = SerialPort.DATABITS_8;
                break;
        }
    }// setDatabits

    /**
     * Sets the number of data bits from the given {@link String}.
     *
     * @param databits the new number of data bits as {@link String}.
     */
    public void setDatabits(String databits) throws IllegalArgumentException {
        int intDataBits = 0;
        try {
            intDataBits = Integer.parseInt(databits);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "databitsString '" + databits + "' can not be converted to a number: " + e.getMessage());
        }

        setDatabits(intDataBits);
    }// setDatabits

    /**
     * Returns the number of data bits as {@link int}.
     *
     * @return the number of data bits as {@link int}.
     */
    public int getDatabits() {
        return m_Databits;
    }// getDatabits

    /**
     * Returns the number of data bits as {@link String}.
     *
     * @return the number of data bits as {@link String}.
     */
    public String getDatabitsString() {
        switch (m_Databits) {
            case SerialPort.DATABITS_5:
                return "5";
            case SerialPort.DATABITS_6:
                return "6";
            case SerialPort.DATABITS_7:
                return "7";
            case SerialPort.DATABITS_8:
                return "8";
            default:
                return "8";
        }
    }// getDataBits

    /**
     * Sets the number of stop bits.
     *
     * @param stopbits the new number of stop bits setting.
     */
    public void setStopbits(double stopbits) throws IllegalArgumentException {
        if (!SerialParameterValidator.isStopbitsValid(stopbits)) {
            throw new IllegalArgumentException("stopbit value '" + stopbits + "' not valid");
        }

        if (stopbits == 1) {
            m_Stopbits = SerialPort.STOPBITS_1;
        } else if (stopbits == 1.5) {
            m_Stopbits = SerialPort.STOPBITS_1_5;
        } else if (stopbits == 2) {
            m_Stopbits = SerialPort.STOPBITS_2;
        } else {
            m_Stopbits = SerialPort.STOPBITS_1;
        }
    }// setStopbits

    /**
     * Sets the number of stop bits from the given {@link String}.
     *
     * @param stopbits the number of stop bits as {@link String}.
     */
    public void setStopbits(String stopbits) throws IllegalArgumentException {
        double doubleStopBits = 1.0;
        try {
            doubleStopBits = Double.parseDouble(stopbits);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "stopbitsString '" + stopbits + "' can not be converted to a number: " + e.getMessage());
        }

        setStopbits(doubleStopBits);
    }// setStopbits

    /**
     * Returns the number of stop bits as {@link int}.
     *
     * @return the number of stop bits as {@link int}.
     */
    public int getStopbits() {
        return m_Stopbits;
    }// getStopbits

    /**
     * Returns the number of stop bits as {@link String}.
     *
     * @return the number of stop bits as {@link String}.
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
    }// getStopbitsString

    /**
     * Sets the parity schema.
     *
     * @param parity the new parity schema as {@link int}.
     */
    public void setParity(int parity) {
        if (!SerialParameterValidator.isParityValid(parity)) {
            throw new IllegalArgumentException("parity value '" + parity + "' not valid");
        }
        m_Parity = parity;
    }// setParity

    /**
     * Sets the parity schema from the given
     * {@link String}.
     *
     * @param parity the new parity schema as {@link String}.
     */
    public void setParity(String parity) throws IllegalArgumentException {
        parity = parity.toLowerCase();
        int intParity = SerialPort.PARITY_NONE;

        if (parity.equals("none") || parity.equals("n")) {
            intParity = SerialPort.PARITY_NONE;
        } else if (parity.equals("even") || parity.equals("e")) {
            intParity = SerialPort.PARITY_EVEN;
        } else if (parity.equals("odd") || parity.equals("o")) {
            intParity = SerialPort.PARITY_ODD;
        } else {
            throw new IllegalArgumentException("unknown parity string '" + parity + "'");
        }

        setParity(intParity);
    }// setParity

    /**
     * Returns the parity schema as {@link int}.
     *
     * @return the parity schema as {@link int}.
     */
    public int getParity() {
        return m_Parity;
    }// getParity

    /**
     * Returns the parity schema as {@link String}.
     *
     * @return the parity schema as {@link String}.
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
    }// getParityString

    /**
     * Sets the encoding to be used.
     *
     * @param enc the encoding as string.
     * @see Modbus#SERIAL_ENCODING_ASCII
     * @see Modbus#SERIAL_ENCODING_RTU
     * @see Modbus#SERIAL_ENCODING_BIN
     */
    public void setEncoding(String enc) throws IllegalArgumentException {
        enc = enc.toLowerCase();
        if (!SerialParameterValidator.isEncodingValid(enc)) {
            throw new IllegalArgumentException("encoding value '" + enc + "' not valid");
        }

        m_Encoding = enc;
    }// setEncoding

    /**
     * Returns the encoding to be used.
     *
     * @return the encoding as string.
     * @see Modbus#SERIAL_ENCODING_ASCII
     * @see Modbus#SERIAL_ENCODING_RTU
     * @see Modbus#SERIAL_ENCODING_BIN
     */
    public String getEncoding() {
        return m_Encoding;
    }// getEncoding

    /**
     * Get the Echo value.
     *
     * @return the Echo value.
     */
    public boolean isEcho() {
        return m_Echo;
    }// getEcho

    /**
     * Set the Echo value.
     *
     * @param newEcho The new Echo value.
     */
    public void setEcho(boolean newEcho) {
        m_Echo = newEcho;
    }// setEcho

    /**
     * Returns the receive timeout for serial communication.
     *
     * @return the timeout in milliseconds.
     */
    public int getReceiveTimeoutMillis() {
        return m_ReceiveTimeoutMillis;
    }// getReceiveTimeout

    /**
     * Sets the receive timeout for serial communication.
     *
     * @param receiveTimeout the receiveTimeout in milliseconds.
     */
    public void setReceiveTimeoutMillis(int receiveTimeout) {
        if (!SerialParameterValidator.isReceiveTimeoutValid(receiveTimeout)) {
            throw new IllegalArgumentException("negative values like '" + receiveTimeout + "' invalid as timeout");
        }

        m_ReceiveTimeoutMillis = receiveTimeout;
    }// setReceiveTimeout

    /**
     * Sets the receive timeout for the serial communication
     * parsing the given String value.
     *
     * @param str the timeout as String.
     */
    public void setReceiveTimeoutMillis(String str) {
        setReceiveTimeoutMillis(Integer.parseInt(str));
    }// setReceiveTimeout

    /**
     * Converts a {@link String} describing a flow control type to the
     * {@link int} which is defined in SerialPort.
     *
     * @param flowcontrol the {@link String} describing the flow control type.
     * @return the {@link int} describing the flow control type.
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
    }// stringToFlow

    /**
     * Converts an {@link int} describing a flow control type to a
     * String describing a flow control type.
     *
     * @param flowcontrol the {@link int} describing the
     *            flow control type.
     * @return the {@link String} describing the flow control type.
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
    }// flowToString

    /**
     * Populates the settings from an {@link Proper}
     * that reads from a properties file or contains a
     * set of properties.
     *
     * @param in the {@link InputStream} to read from.
     *
     *            private void loadFrom(InputStream in) throws IOException {
     *            Properties props = new Properties();
     *            props.load(in);
     *            setPortName(props.getProperty("portName"));
     *            setBaudRate(props.getProperty("baudRate"));
     *            setFlowControlIn(props.getProperty("flowControlIn"));
     *            setFlowControlOut(props.getProperty("flowControlOut"));
     *            setParity(props.getProperty("parity"));
     *            setDatabits(props.getProperty("databits"));
     *            setStopbits(props.getProperty("stopbits"));
     *            setEncoding(props.getProperty("encoding"));
     *            setEcho(new Boolean(props.getProperty("echo")).booleanValue());
     *            }//loadFrom
     *
     */

}// class SerialParameters
