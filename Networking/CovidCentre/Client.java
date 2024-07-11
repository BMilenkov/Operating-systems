package Networking.CovidCentre;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread {
    private int serverPort;
    private String serverName;

    public Client(int serverPort, String serverName) {
        this.serverPort = serverPort;
        this.serverName = serverName;
    }

    @Override
    public void run() {
        Socket socket = null;
        BufferedWriter bw = null;
        BufferedReader br = null;

        try {
            socket = new Socket(InetAddress.getByName(serverName), serverPort);
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String line;
            Scanner scanner = new Scanner(System.in);
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                bw.write(scanner.nextLine() + "\n");
                bw.flush();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                assert socket != null;
                socket.close();
                if (br != null) br.close();
                if (bw != null) bw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        new Client(8888, "localhost").start();
//        ArrayList<Client> clients = IntStream.range(0,10)
//                .mapToObj(i->new Client(8888,"localhost"))
//                .collect(Collectors.toCollection(ArrayList::new));
    }
}
