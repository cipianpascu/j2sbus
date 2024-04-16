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

package ro.ciprianpascu.sbus.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.ciprianpascu.sbus.Modbus;
import ro.ciprianpascu.sbus.io.ModbusTransport;

/**
 * Class that implements a UDPMasterConnection.
 *
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public class UDPMasterConnection implements ModbusSlaveConnection {
	
	private static final Logger logger = LoggerFactory.getLogger(UDPMasterConnection.class);

	private static int LOCAL_PORT = Modbus.DEFAULT_PORT;

	// instance attributes
	private UDPMasterTerminal m_Terminal;
	private int m_Timeout = Modbus.DEFAULT_TIMEOUT;
	private boolean m_Connected;

	private int m_Port = Modbus.DEFAULT_PORT;
	
	public UDPMasterConnection() {
		
	}


	public UDPMasterConnection(int port) {
		setPort(port);
	}

	/**
	 * Opens this {@link UDPMasterConnection}.
	 *
	 * @throws Exception if there is a network failure.
	 */
	@Override
	public synchronized boolean connect() throws Exception {
		if (!m_Connected) {
			InetAddress localAddress = getLocalIP();
			m_Terminal = new UDPMasterTerminal();
			m_Terminal.setLocalAddress(localAddress);
			m_Terminal.setLocalPort(LOCAL_PORT);
			m_Terminal.setRemoteAddress(InetAddress.getByAddress(getTargetIP(localAddress.getAddress())));
			m_Terminal.setRemotePort(m_Port);
			m_Terminal.setTimeout(m_Timeout);
			m_Terminal.activate();
			m_Connected = true;
		}
		return m_Connected;
	}// connect

	/**
	 * Closes this {@link UDPMasterConnection}.
	 */
	public void close() {
		if (m_Connected) {
			try {
				m_Terminal.deactivate();
			} catch (Exception ex) {
				if (Modbus.debug) {
					ex.printStackTrace();
				}
			}
			m_Connected = false;
		}
	}// close

	/**
	 * Returns the {@link ModbusTransport} associated with this
	 * {@link UDPMasterConnection}.
	 *
	 * @return the connection's {@link ModbusTransport}.
	 */
	public ModbusTransport getModbusTransport() {
		return m_Terminal.getModbusTransport();
	}// getModbusTransport

	/**
	 * Returns the terminal used for handling the package traffic.
	 *
	 * @return a {@link UDPTerminal} instance.
	 */
	public UDPTerminal getTerminal() {
		return m_Terminal;
	}// getTerminal

	/**
	 * Returns the timeout for this {@link UDPMasterConnection}.
	 *
	 * @return the timeout as {@link int}.
	 */
	public int getTimeout() {
		return m_Timeout;
	}// getReceiveTimeout

	/**
	 * Sets the timeout for this {@link UDPMasterConnection}.
	 *
	 * @param timeout the timeout as {@link int}.
	 */
	public void setTimeout(int timeout) {
		m_Timeout = timeout;
		m_Terminal.setTimeout(timeout);
	}// setReceiveTimeout

	/**
	 * Returns the destination port of this {@link UDPMasterConnection}.
	 *
	 * @return the port number as {@link int}.
	 */
	public int getPort() {
		return m_Port;
	}// getPort

	/**
	 * Sets the destination port of this {@link UDPMasterConnection}. The default is
	 * defined as {@link Modbus#DEFAULT_PORT}.
	 *
	 * @param port the port number as {@link int}.
	 */
	public void setPort(int port) {
		m_Port = port;
	}// setPort


	/**
	 * Tests if this {@link UDPMasterConnection} is connected.
	 *
	 * @return true if connected, false otherwise.
	 */
	@Override
	public boolean isConnected() {
		return m_Connected;
	}// isConnected

	@Override
	public void resetConnection() {
		close();
	}

	@Override
	public String toString() {
		return "UDPMasterConnection [m_Terminal=" + m_Terminal + ", m_Timeout=" + m_Timeout + ", m_Connected="
				+ m_Connected + ", m_Port=" + m_Port + "]";
	}

	private InetAddress getLocalIP() throws SocketException, UnknownHostException {
		for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
			NetworkInterface intf = en.nextElement();
			for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
				InetAddress inetAddress = enumIpAddr.nextElement();
				if (!inetAddress.isLoopbackAddress()) {
					return inetAddress;
				}
			}
		}
		return InetAddress.getLocalHost();
	}
	
	private byte[] getTargetIP(byte[] arraybyteLocalIP)
	{	
		byte[] arraybyteTargetIP= new byte[4];
		byte byteBit;
	       
		byteBit=(byte) ((arraybyteLocalIP[0] & 0xFF)>>5);
		if (((byteBit & 0xFF)>=0) && ((byteBit & 0xFF)<=3)) //IP type:A
		{
			arraybyteTargetIP[0]=arraybyteLocalIP[0];
			arraybyteTargetIP[1]=(byte) 255;
			arraybyteTargetIP[2]=(byte) 255;
			arraybyteTargetIP[3]=(byte) 255;	
		}
		else if (((byteBit & 0xFF)>=4) && ((byteBit & 0xFF)<=5)) //IP Type:B
		{
			arraybyteTargetIP[0]=arraybyteLocalIP[0];
			arraybyteTargetIP[1]=arraybyteLocalIP[1];
			arraybyteTargetIP[2]=(byte) 255;
			arraybyteTargetIP[3]=(byte) 255;	
		}
		else if (((byteBit & 0xFF)>=6) && ((byteBit & 0xFF)<=7)) //IP Type:C
		{
			arraybyteTargetIP[0]=arraybyteLocalIP[0];
			arraybyteTargetIP[1]=arraybyteLocalIP[1];
			arraybyteTargetIP[2]=arraybyteLocalIP[2];
			arraybyteTargetIP[3]=(byte) 255;	
		}
		else
		{
			arraybyteTargetIP[0]=(byte) 255;
			arraybyteTargetIP[1]=(byte) 255;
			arraybyteTargetIP[2]=(byte) 255;
    		arraybyteTargetIP[3]=(byte) 255;	
		}
	
		return arraybyteTargetIP;
	}	
}// class UDPMasterConnection
