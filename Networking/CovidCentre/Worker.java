package Networking.CovidCentre;

import java.io.*;
import java.net.Socket;

public class Worker extends Thread {
    private Socket socket;
    private File file;

    public Worker(Socket socket, File file) {
        this.socket = socket;
        this.file = file;
    }

    @Override
    public void run() {
        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;
        int counter = 0;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedWriter.write("HELLO " + socket.getInetAddress().getHostAddress() + "\n");
            bufferedWriter.flush();

            while (true) {
                String line = bufferedReader.readLine();
                if (counter == 0) {
                    String[] parts = line.split("\\s+");
                    if (parts.length != 2 || !parts[0].equals("HELLO"))
                        throw new IOException("Client do not handshake!");
                    //This will throw pars exception if it is not integer!
                    int port = Integer.parseInt(parts[1]);
                    bufferedWriter.write("SEND DAILY DATA!\n");
                    bufferedWriter.flush();
                } else if (counter == 1) {
                    String[] parts = line.split(",");
                    if (parts.length != 4)
                        throw new IOException("Not enough info in the daily data!");
                    //If everything is OK!
                    Server.logInFile(line);
                    bufferedWriter.write("OK!\n");
                    bufferedWriter.flush();
                }
                if (line.equals("QUIT"))
                    break;
                counter++;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (bufferedReader != null) bufferedReader.close();
                if (bufferedWriter != null) bufferedWriter.close();
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }


    }
}
