package NetworkingTCP.FileSize;

import java.io.*;
import java.net.Socket;

public class Worker extends Thread {
    private final Socket socket;

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
                //1.
                String line = bufferedReader.readLine();
                System.out.println("\nServer received:\n" + line);
                bufferedWriter.write("Hello 223010...Send me file content!" + "\n");
                bufferedWriter.flush();

                //2
                System.out.println("\nServer received the content:");
                bufferedReader.lines().takeWhile(l->!l.isEmpty()).forEach(System.out::println);
//                while (!(line = bufferedReader.readLine()).isEmpty()) {
//                    System.out.println(line);
//                }
                bufferedWriter.write("Send the file size now!" + "\n");
                bufferedWriter.flush();

                //3
                bufferedWriter.write("Your file size is " + bufferedReader.readLine().split(":")[2] + "\n");
                bufferedWriter.flush();
                break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
