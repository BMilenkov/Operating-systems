package Networking.CovidCentre;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    public static File dataCSV;
    private int port;

    public Server(int port, String path) {
        dataCSV = new File(path);
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
            System.out.println("Server: new client is CONNECTED!");
            new Worker(socket, dataCSV).start();
        }
    }

    //Most important for sync is to add synchronized or to do it with Lock()
    public static synchronized void logInFile(String line) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataCSV, true)));
        bufferedWriter.write(line + "\n");
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    public static void main(String[] args) {
        Server server = new Server(8888, "C:/Users/user/OS24/src/TCP_Exercises_Networking/CovidCentre/data.csv");
        server.start();
    }
}
