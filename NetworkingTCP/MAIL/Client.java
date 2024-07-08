package NetworkingTCP.MAIL;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends Thread {
    private final String server_name;
    private final int server_port;
    Socket socket = null;
    PrintWriter writer = null;
    BufferedReader reader = null;

    public Client(String server_name, int server_port) {
        this.server_name = server_name;
        this.server_port = server_port;
    }

    @Override
    public void run() {

        String emailTo = "stefan@gmail.com";
        String emailFrom = "aleksandra@gmail.com";
        String emailCC = "branko@gmail.com";
        String data = "Ovo je fudbal\nHello\nWorld\nSta je ovo\nAko je ovo ovo\nSta sam ja dolazio\nA\n?";

        try {
            socket = new Socket(InetAddress.getByName(this.server_name), this.server_port);
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer.println("Connected");
            writer.flush();
            String response;
            while (!(response = reader.readLine()).isEmpty()) {
                if (response.startsWith("START")) {
                    System.out.println("MAIL_TO " + emailTo);
                    writer.println("MAIL_TO " + emailTo);
                    writer.flush();
                } else if (response.startsWith("TNX")) {
                    System.out.println("MAIL_FROM " + emailFrom);
                    writer.println("MAIL_FROM " + emailFrom);
                    writer.flush();
                } else if (response.startsWith("200")) {
                    System.out.println("MAIL_CC " + emailCC);
                    writer.println("MAIL_CC: " + emailCC);
                    writer.flush();
                } else if (response.startsWith("RECEIVERS")) {
                    System.out.println(data);
                    writer.println(data);
                    writer.flush();
                } else if (response.startsWith("RECEIVED")) {
                    System.out.println(response);
                    writer.println("EXIT");
                    writer.flush();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (writer != null) {
                writer.flush();
                writer.close();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 7000);
//        List<Client> clients = IntStream.range(0,5).mapToObj(i->new Client("localhost",7000)).toList();
//        clients.forEach(Thread::start);
        client.start();
    }
}
