package NetworkingTCP.Logger_Containarization.src.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

    private final int port;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        System.out.println("SERVER: staring...");
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }
        System.out.println("SERVER: started!");
        System.out.println("SERVER: waiting for connections...");

        while (true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("SERVER: new client is connected!");
            new Worker(socket).start();
        }

    }

    public static void main(String[] args) {
        String serverPort = System.getenv("SERVER_PORT");
//        if (serverPort == null) {
//            throw new RuntimeException("Server port should be defined as ENV {SERVER_PORT}.");
//        }

        Server server = new Server(7000);
        server.start();
    }
}

