package NetworkingTCP.FileSize;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends Thread {

    private int port;
    private String serverName;

    public Client(int port, String serverName) {
        this.port = port;
        this.serverName = serverName;
    }

    @Override
    public void run() {
        BufferedWriter pw = null;
        Socket socket = null;
        BufferedReader br = null;


        try {
            socket = new Socket(InetAddress.getByName(serverName), this.port);
            pw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            File file = new File("C:\\Users\\user\\OS24\\src\\TCP_Exercises_Networking\\FileSize\\myfile.txt");
            int size = (int) file.length();

            while (true) {
                //1.
                pw.write("hello:223010" + "\n");
                pw.flush();
                System.out.println("Client received: " + br.readLine());

                //2.
                BufferedReader fileR = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line;
                while ((line = fileR.readLine()) != null) {
                    pw.write(line + "\n");
                }
                pw.write("\n");
                pw.flush();
                System.out.println("Client received: " + br.readLine());

                //3.
                pw.write("223010:fileSize:" + size + "\n");
                pw.flush();
                System.out.println("Client received: " + br.readLine());
                break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String serverName = "localhost";
        int port = 9753;
        Client client = new Client(port, serverName);
        client.start();
    }
}