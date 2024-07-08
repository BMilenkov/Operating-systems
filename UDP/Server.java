package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server extends Thread {
    private DatagramSocket socket;
    private byte[] buffer;

    public Server(int port) {
        try {
            socket = new DatagramSocket(port);
            this.buffer = new byte[256];
            System.out.println("Server starts working...");
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
            socket.receive(packet);
            String message = new String(packet.getData(), 0, packet.getLength());
            System.out.println("SERVER RECEIVED: " + message);
            buffer = new String("SENT THE SAME!" + message).getBytes();
            packet = new DatagramPacket(buffer, 0, buffer.length, packet.getAddress(), packet.getPort());
            socket.send(packet);
        } catch (IOException e) {
            socket.close();
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Server server = new Server(8888);
        server.start();
    }
}