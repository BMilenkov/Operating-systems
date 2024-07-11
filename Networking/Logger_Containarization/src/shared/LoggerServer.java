package Networking.Logger_Containarization.src.shared;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LoggerServer extends Thread {

    private final int serverPort;
    private File txtFilePath;
    private File counterFilePath;

    public LoggerServer(String serverPort, File txtFilePath, File counterFilePath) {
        this.serverPort = Integer.parseInt(serverPort);
        this.txtFilePath = txtFilePath;
        this.counterFilePath = counterFilePath;
    }

    @Override
    public void run() {
        ServerSocket serverSocket;
        System.out.println("Logger server is starting...");
        System.out.println("Logger server is waiting for new clients...");
        try {
            serverSocket = new ServerSocket(serverPort);
            System.out.println("Logger server has started successfully!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("New client has been connected successfully!");
                new LoggerWorker(socket, txtFilePath, counterFilePath).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
//        File txtFile = new File(System.getenv("LOG_FILE"));
//        File counterFile = new File(System.getenv("COUNTER_FILE"));
//        String serverPort = System.getenv("LOGGER-SERVER-PORT");
//        if (serverPort == null) {
//            throw new RuntimeException("Define port as ENV {SERVER_PORT}.");
//        }
        new LoggerServer("7050", new File("C:\\Users\\user\\OS24\\src\\TCP_Exercises_Networking\\Logger_Containarization\\vol\\log.txt"),
                new File("C:\\Users\\user\\OS24\\src\\TCP_Exercises_Networking\\Logger_Containarization\\vol\\counter.bin")).start();
    }
}
