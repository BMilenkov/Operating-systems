package NetworkingTCP.FileSize;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    private int port;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        System.out.println("Server starting...");
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            throw new RuntimeException(e);
         }
        System.out.println("Server started!");
        System.out.println("Waiting for new connections...");

        while (true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Server: new client is detected!");
            new Worker(socket).start();
        }
    }


    public static void main(String[] args) {
        Server server = new Server(9753);
        server.start();
    }
}

