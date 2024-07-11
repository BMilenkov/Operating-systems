package Networking.Logger_Containarization.src.shared;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LoggerWorker extends Thread {

    private final Socket socket;
    private final File txtFilePath;
    private final File counterFile;
    private static final Lock log = new ReentrantLock();
    private static final Lock counterLog = new ReentrantLock();

    public LoggerWorker(Socket socket, File txtFilePath, File counterFilePath) {
        this.socket = socket;
        this.txtFilePath = txtFilePath;
        this.counterFile = counterFilePath;
    }

    @Override
    public void run() {
        BufferedWriter bw = null;
        BufferedReader br = null;
        RandomAccessFile counterFileRaf = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter((new FileOutputStream(txtFilePath, true))));
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            counterFileRaf = new RandomAccessFile(counterFile, "rw");
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        int local = 0;
        try {
            String line;
            assert br != null;
            sb.append(br.readLine());
////            while ((line = br.readLine()) != null) {
////                sb.append(line).append("\n");
////                local++;
//            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.lock();
        try {
            assert bw != null;
            bw.write(sb.toString());
            bw.write("\n");
            bw.flush();
            bw.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            log.unlock();
        }

        counterLog.lock();
        try {
            assert counterFileRaf != null;
            int numLogs = counterFileRaf.readInt();
            counterFileRaf.seek(0);
            counterFileRaf.writeInt(numLogs + 1);
            counterFileRaf.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            counterLog.unlock();
        }
    }
}
