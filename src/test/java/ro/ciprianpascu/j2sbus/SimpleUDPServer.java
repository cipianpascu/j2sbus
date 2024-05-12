package ro.ciprianpascu.j2sbus;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class SimpleUDPServer {
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	
    public static void main(String[] args) {
        int port = 6000; // Example port number
        byte[] buffer = new byte[1024]; // Adjust buffer size as needed

        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("UDP Server is running on port " + port);
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                //String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println(" Lenght: " + packet.getLength() + " Data: " + bytesToHex(packet.getData(), packet.getLength()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String bytesToHex(byte[] bytes, int length) {
        char[] hexChars = new char[length * 2];
        for (int j = 0; j < length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
