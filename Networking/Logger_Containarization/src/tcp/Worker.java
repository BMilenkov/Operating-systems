package Networking.Logger_Containarization.src.tcp;


import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Worker extends Thread {

    private final Socket socket;

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

            WebRequest request = WebRequest.of(reader);
            System.out.println(request.command + " " + request.url);

            shareLog(socket.getInetAddress().getHostAddress(), request.command, request.url);

            writer.write("HTTP/1.1 200 OK\n");
            writer.write("Content-Type: text/html\n");
            writer.write("Hello " + request.header.get("User-Agent") + "! <br/>" + "\n");
            writer.write("You requested: " + request.command + " " + request.url + " by using HTTP version " + request.version + "\n");
            writer.write("\n");
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                this.socket.close();
                if (reader != null)
                    reader.close();
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void shareLog(String clientIPAddress, String command, String url) throws IOException {

        String serverName = "localhost";
        String serverPort = "7050";
//        String serverName = System.getenv("logger");
//        String serverPort = System.getenv("7050");

        if (serverPort == null) {
            throw new RuntimeException("Logger Server port is not specified {LOGGER_SERVERPORT}!");
        }
        Socket socket = new Socket(InetAddress.getByName(serverName), Integer.parseInt(serverPort));
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bufferedWriter.write(String.format("[%s] %s: %s %s\n", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), clientIPAddress, command, url));
        bufferedWriter.close();
//        BufferedWriter writer = null;
//        try {
//            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//
//        } catch (IOException e) {
//            throw e;
//        } finally {
//            if (writer != null) {
//                writer.flush();
//                writer.close();
//            }
//            socket.close();
//        }
    }

    public static class WebRequest {

        private final String command;
        private final String url;
        private final String version;

        private final Map<String, String> header;

        private WebRequest(String command, String url, String version, Map<String, String> header) {
            this.command = command;
            this.url = url;
            this.version = version;
            this.header = header;
        }

        public static WebRequest of(BufferedReader reader) throws IOException {

            String[] line1 = reader.readLine().split(" ");
            String command = line1[0];
            String url = line1[1];
            String version = line1[2];

            HashMap<String, String> headers = new HashMap<>();

            reader.lines().takeWhile(line -> !line.isEmpty())
                    .forEach(l -> {
                        String[] parts = l.split(":");
                        headers.put(parts[0].trim(), parts[1].trim());
                    });

            return new WebRequest(command, url, version, headers);
        }
    }
}
