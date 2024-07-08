package NetworkingTCP.Login;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends Thread {
    private final int serverPort;
    private final String serverName;

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
            writer.flush();

            while (true) {
                String line = reader.readLine();
                System.out.println("\nClient received:\n" + line);
                writer.write("Hi server, my name is Branko!\n");
                writer.write("I'm an enthusiastic and curious web developer!\n");
                writer.write("\n");
                writer.flush();
                System.out.println("\nClient received:");
                reader.lines().takeWhile(l -> !l.isEmpty()).forEach(System.out::println);
                writer.write("logout\n");
                writer.flush();
                System.out.println("\nClient received:\n" + reader.readLine());
                break;
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

//        IntStream.range(0,5).mapToObj(i->new Client("localhost",7000))
//                .forEach(Thread::start);

        new Client("localhost", 7000).start();
    }
}
