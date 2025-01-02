package ro.ciprianpascu.j2sbus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import ro.ciprianpascu.sbus.util.SbusUtil;

public class SbusUDPListener {
    private DatagramSocket socket;
    private int port;

    public SbusUDPListener(int port) throws SocketException {
        this.port = port;
        this.socket = new DatagramSocket(port);
        this.socket.setBroadcast(true); // Enable broadcasting
    }

    public void start() {
        new Thread(() -> {
            try {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                while (true) {
                    socket.receive(packet);
					System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort());
					System.out.println(SbusUtil.toHex(packet.getData(), 0, packet.getLength()));
                    // Handle the received packet here
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stop() {
        socket.close();
    }

    public static void main(String[] args) throws IOException {
        int port = 6000; // Replace with the desired port number

        SbusUDPListener listener = new SbusUDPListener(port);
        listener.start();

        // Wait for user input to stop the listener
        System.in.read();

        listener.stop();
    }
}