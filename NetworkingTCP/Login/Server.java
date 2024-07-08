package NetworkingTCP.Login;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server extends Thread {
    private static final File file = new File("C:\\Users\\user\\OS24\\src\\TCP_Exercises_Networking\\Login\\count.bin");
    private final int port;
    private static int messageCounter = 0;
    public static Lock lock = new ReentrantLock();

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        System.out.println("SEVER: starting...");
        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(port);
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
                throw new RuntimeException(e);
            }
            System.out.println("SERVER: new client connected!");
            new Worker(socket).start();
        }
    }

    public static int getMessageCounter() {
        return messageCounter;
    }

    public static void incrementCounter() throws IOException {
        lock.lock();
        messageCounter++;
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(0);
            raf.writeInt(messageCounter);
            raf.close();
        //System.out.println("Current messages sent --> " + getMessageCounter());
        lock.unlock();
    }

    public static void main(String[] args) {
        Server server = new Server(7000);
        server.start();
    }
}
