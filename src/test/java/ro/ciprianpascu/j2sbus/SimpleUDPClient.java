package ro.ciprianpascu.j2sbus;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class SimpleUDPClient {
    public static void main(String[] args) {
        String serverHostname = "192.168.100.252"; // Adjust as needed
        int serverPort = 6000; // Must match the server's listening port

        try {
            DatagramSocket socket = new DatagramSocket();
            

            byte[] localIp = getLocalIP();
        	byte[] fullMessage = new byte[27];
        	fullMessage[0] = localIp[0];
        	fullMessage[1] = localIp[1];
        	fullMessage[2] = localIp[2];
        	fullMessage[3] = localIp[3];

        	fullMessage[4]=0x53; //S
        	fullMessage[5]=0x4D; //M
        	fullMessage[6]=0x41; //A
        	fullMessage[7]=0x52; //R
        	fullMessage[8]=0x54; //T
        	fullMessage[9]=0x43; //C
        	fullMessage[10]=0x4C; //L
        	fullMessage[11]=0x4F; //O
        	fullMessage[12]=0x55; //U
        	fullMessage[13]=0x44; //D
        	fullMessage[14]=(byte) 0xAA; //
        	fullMessage[15]=(byte) 0xAA; //
        	
        	fullMessage[16]=0x0B;
        	fullMessage[17]=0x01;
        	fullMessage[18]=(byte) 0xFE;
        	fullMessage[19]=(byte) 0xFF;
        	fullMessage[20]=(byte) 0xFE;
        	fullMessage[21]=0x00;
        	fullMessage[22]=0x0E;
        	fullMessage[23]=0x01;
        	fullMessage[24]=0x4B;
        	fullMessage[25]=0x2B;
        	fullMessage[26]=(byte) 0xA6;

        	
            InetAddress address = InetAddress.getByName(serverHostname);
            DatagramPacket packet = new DatagramPacket(fullMessage, fullMessage.length, address, serverPort);

            System.out.println("Sending message to " + serverHostname + ":" + serverPort);
            socket.send(packet);
            socket.close();
            System.out.println("Message sent!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	private static byte[] getLocalIP() {
		byte[] ipAddr = new byte[4];

		try {

			ipAddr = null;
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if(inetAddress instanceof Inet6Address)
						continue;
					if (!inetAddress.isLoopbackAddress()) {
						ipAddr = inetAddress.getAddress();
						return ipAddr;
					}
				}
			}
			return ipAddr;

		} catch (Exception e) {
			// Toast.makeText(getApplicationContext(), e.getMessage(),
			// Toast.LENGTH_SHORT).show();
		}
		return ipAddr;

	}
}
