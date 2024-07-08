package TCP.Server;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Worker extends Thread {

    private Socket socket;


    public Worker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String line = reader.readLine();
            if (line.equals("login")) {
                writer.write("logged in\n");
                Server.incrementCounter();
                while ((line = reader.readLine()) != null) {
                    if (line.equals("logout")) {
                        Server.incrementCounter();
                        writer.write("logged out\n");
                        break;
                    }
                    Server.incrementCounter();
                    writer.write( line + "\n");
                }
            }
            writer.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static class WebRequest {

        private String command;
        private String url;
        private String version;
        private Map<String, String> header;

        private WebRequest(String command, String url, String version, Map<String, String> header) {
            this.command = command;
            this.url = url;
            this.version = version;
            this.header = header;
        }


        public static WebRequest of(BufferedReader reader) throws IOException {
            List<String> input = new ArrayList<>();
            String line;
            while (!(line = reader.readLine()).equals("")) {
                Server.incrementCounter();
                input.add(line);
            }

            String[] args = input.get(0).split(" ");
            String command = args[0];
            String url = args[1];
            String version = args[2];

            HashMap<String, String> headers = new HashMap<>();

            for (int i = 1; i < input.size(); i++) {
                String[] pair = input.get(i).split(":");
                headers.put(pair[0], pair[1]);
            }

            return new WebRequest(command, url, version, headers);
        }

    }
}
