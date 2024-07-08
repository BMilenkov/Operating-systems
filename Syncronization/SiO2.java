package Syncronization;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SiO2 {

    public static int NUM_RUN = 50;
    // define necessary synchronization structures

    static Semaphore S_here;
    static Semaphore O_here;
    static Semaphore ready;
    static Semaphore Ox;
    static Semaphore Sil;

    public static void init() {
        // initialize synchronization structures
        S_here = new Semaphore(0);
        O_here = new Semaphore(0);
        Sil = new Semaphore(1);
        Ox = new Semaphore(2);
        ready = new Semaphore(0);
    }

    public static class Si extends Thread {

        public void bond() {
            System.out.println("Si is bonding now.");
        }

        @Override
        public void run() {
            for (int i = 0; i < NUM_RUN; i++) {
                try {
                    execute();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void execute() throws InterruptedException {
            // synchronize call to bond() from Si
            Sil.acquire();
            S_here.release(2);
            O_here.acquire(2);
            ready.release(2);
            bond();
            Sil.release();
        }

    }

    public static class O extends Thread {

        public void execute() throws InterruptedException {
            // synchronize call to bond() from O
            Ox.acquire();
            S_here.acquire();
            O_here.release();
            ready.acquire();
            bond();
            Ox.release();
        }

        public void bond() {
            System.out.println("O is bonding now.");
        }


        @Override
        public void run() {
            for (int i = 0; i < NUM_RUN; i++) {
                try {
                    execute();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        init();
        ArrayList<O> oxygens = IntStream.range(0, 100)
                .mapToObj(i -> new O()).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Si> siliciums = IntStream.range(0, 50)
                .mapToObj(i -> new Si()).collect(Collectors.toCollection(ArrayList::new));
        oxygens.forEach(Thread::start);
        siliciums.forEach(Thread::start);
    }
}