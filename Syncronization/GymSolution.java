package Syncronization;


import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class GymSolution {

    static Semaphore sala;
    static Semaphore soblekuvalna;
    static Semaphore ready;
    static Semaphore readySoblekuvalna;
    static Lock lock;
    static int counter = 0;
    static int COUNTER = 0;

    public static void init() {
        sala = new Semaphore(12);
        soblekuvalna = new Semaphore(4);
        ready = new Semaphore(0);
        readySoblekuvalna = new Semaphore(0);
        lock = new ReentrantLock();
    }

    static void presobleci() {
        System.out.println("Se presoblekuva...");
    }

    static void sportuvaj() {
        System.out.println("Sportuvam...");
    }

    static void oslobodiSala() {
        System.out.println("Osloboduvanje na SALA!");
    }

    public static class Player extends Thread {

        public Player() {
        }

        //MOST IMPORTANT!!
        public void execute() throws InterruptedException {
            sala.acquire();
            soblekuvalna.acquire();
            lock.lock();
            COUNTER++;
            if (COUNTER == 4)
                readySoblekuvalna.release(4);
            lock.unlock();
            readySoblekuvalna.acquire();
            presobleci();
            lock.lock();
            COUNTER--;
            if (COUNTER == 0)
                soblekuvalna.release(4);
            lock.unlock();

            lock.lock();
            counter++;
            if (counter == 12) {
                ready.release(12);
            }
            lock.unlock();
            ready.acquire();
            sportuvaj();
            lock.lock();
            counter--;
            if (counter == 0) {
                oslobodiSala();
                sala.release(12);
            }
            lock.unlock();
        }

        @Override
        public void run() {
            try {
                execute();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public static void main(String[] args) {
            init();
            ArrayList<Player> players = IntStream.range(0, 50)
                    .mapToObj(i -> new Player()).collect(Collectors.toCollection(ArrayList::new));
            players.forEach(Thread::start);
        }
    }
}


