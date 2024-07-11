package Networking.Email;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server extends Thread {
    private final int port;
    public static Lock lock = new ReentrantLock();

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        System.out.println("SEVER: starting...");
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }

        System.out.println("SERVER: started!");
        System.out.println("SERVER: waiting for connections...");

        while (true) {
            Socket socket;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("SERVER: new client connected!");
            new Worker(socket).start();
        }
    }

    public static void enterData(List<String> data, int words) throws IOException {
        lock.lock();
        //it is for testing .env
        String path = System.getenv("SERVER_FILE");
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:\\Users\\user\\OS24\\src\\TCP_Exercises_Networking\\MAIL\\server.txt", true)));
        RandomAccessFile raf = new RandomAccessFile("C:\\Users\\user\\OS24\\src\\TCP_Exercises_Networking\\MAIL\\counter.bin", "rw");
        int count = raf.readInt();
        raf.seek(0);
        count += words;
        raf.writeInt(count);
        raf.close();
        for (String d : data) {
            bufferedWriter.append(d).append("\n");
        }
        bufferedWriter.append("\n");
        bufferedWriter.flush();
        lock.unlock();
    }

    public static void main(String[] args) {
        Server server = new Server(7000);
        server.start();
    }
}
