package Networking.Login;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class Worker extends Thread {

    private Socket socket;

    public Worker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            while (true) {
                Server.incrementCounter();
                String line = bufferedReader.readLine();
                if (!line.equals("login"))
                    break;
                System.out.println("\nServer received:\n" + line);
                bufferedWriter.write("logged in\n");
                bufferedWriter.flush();
                System.out.println("\nServer received:");
                List<String> lines = bufferedReader.lines().takeWhile(l -> !l.isEmpty()).toList();
                lines.forEach(System.out::println);
                for (String l: lines) {
                    bufferedWriter.write(l + "\n");
                }
                bufferedWriter.write("\n");
                bufferedWriter.flush();
                System.out.println("\nServer received:\n" + bufferedReader.readLine());
                bufferedWriter.write("logged out\n");
                bufferedWriter.flush();
                break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
                if (bufferedWriter != null)
                    bufferedWriter.flush();
                if (bufferedReader != null)
                    bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
