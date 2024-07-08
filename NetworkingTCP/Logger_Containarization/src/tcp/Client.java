package NetworkingTCP.Logger_Containarization.src.tcp;

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

            writer.write("GET /Movies/Delete/1 HTTP/1.1\n");
            writer.write("Host: developer.mozilla.org\n");
            writer.write("User-Agent: OSClient\n");
            writer.write("\n");
            writer.flush();

            String line;
            while (!(line = reader.readLine()).isEmpty()) {
                System.out.println("Client received: " + line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static void main(String[] args) {
        //String serverName = System.getenv("SERVER_NAME");
        //String serverPort = System.getenv("SERVER_PORT");
//        if (serverPort == null) {
//            throw new RuntimeException("Server port should be defined as ENV {SERVER_PORT}.");
//        }
        Client client = new Client("localhost", 7000);
        client.start();
//        IntStream.range(0,5).mapToObj(i->new Client("localhost", 7000))
//                .forEach(Thread::start);
    }
}
