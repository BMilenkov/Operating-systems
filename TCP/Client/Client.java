package TCP.Client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client extends Thread {
    private int serverPort;
    private String serverName;

    public Client(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        Socket socket = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            socket = new Socket(InetAddress.getByName(this.serverName), this.serverPort);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));


            writer.write("login\n");
            writer.write("Hi, I'm Branko :)\n");
            writer.write("logout\n");
            writer.flush();

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Client received: " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
       new Client("localhost",7000).start();
    }
}
