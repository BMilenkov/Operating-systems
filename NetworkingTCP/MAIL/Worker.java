package NetworkingTCP.MAIL;

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
        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //1.
            String emailTo;
            String mailCC;
            String line = bufferedReader.readLine();
            System.out.println("SERVER received: " + line);
            if (line.equals("Connected")) {
                bufferedWriter.write("START" + socket.getInetAddress() + "\n");
                bufferedWriter.flush();
            }
            //2.
            line = bufferedReader.readLine();
            System.out.println("SERVER received: " + line);
            emailTo = line.split(" ")[1];
            bufferedWriter.write("TNX\n");
            bufferedWriter.flush();
            //3.
            line = bufferedReader.readLine();
            System.out.println("SERVER received: " + line);
            bufferedWriter.write("200\n");
            bufferedWriter.flush();
            //4.
            line = bufferedReader.readLine();
            System.out.println("SERVER received: " + line);
            mailCC = line.split(" ")[1];
            bufferedWriter.write("RECEIVERS " + emailTo + " " + mailCC + "\n");
            bufferedWriter.flush();
            //5.
            int chars = 0;
            int words = 0;
            List<String> data = bufferedReader.lines().takeWhile(l -> !l.equals("?")).toList();
            for (String d : data) {
                chars += d.replaceAll("\\s++", "").length();
                words += d.split("\\s++").length;
            }

            System.out.println("SERVER received: ");
            data.forEach(System.out::println);
            bufferedWriter.write("RECEIVED-> LINES: " + data.size() + ", CHARS: " + chars + ", WORDS: " + words + "\n");
            bufferedWriter.write("\n");
            bufferedWriter.flush();
            //6.
            line = bufferedReader.readLine();
            System.out.println("SERVER received: EXIT");
            Server.enterData(data, words);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert bufferedReader != null;
                bufferedReader.close();
                bufferedWriter.close();
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
