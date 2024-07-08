package Syncronization;

import java.util.concurrent.Semaphore;

public class UseJoin {
    public static void main(String[] args) {
        Count c = new Count();
        c.start();

        Semaphore s = new Semaphore(1);
        
        try {
            c.join();
            System.out.println("Result = " + c.getResult());
        } catch (InterruptedException e) {
            System.out.println("The thread im waiting for is interrupted by another thread!");
        }
//        System.out.println("TEST");
    }
}

class Count extends Thread {
    private long result;

    public void run() {
        result = count();
//        System.out.print("zdravo");
    }

    public long getResult() {
        return result;
    }

    public static long count() {
        long r = 0;
        for (r = 0; r < 100000; r++) ;
        return r;
    }
}