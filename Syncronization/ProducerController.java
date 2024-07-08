package Syncronization;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerController {

    public static int NUM_RUN = 50;
    public static Semaphore producer;
    public static Semaphore controller;
    public static Semaphore canCheck;
    public static int numChecks;
    public static Lock lock;


    public static void init() {
        producer = new Semaphore(1);
        controller = new Semaphore(10);
        //this is to make sure there is 10 controllers in so they can start at the same time
        //NOT a task requirement but ok :)!
        canCheck = new Semaphore(0);
        lock = new ReentrantLock();
        numChecks = 0;
    }

    public static class Buffer {

        private boolean producing = false;
        private int checkingCount = 0;

        public void produce() {
            producing = true;
            if (checkingCount > 0) {
                throw new RuntimeException("Can't produce if controllers checking");
            }

            System.out.println("Producer is producing...");

            producing = false;
        }

        public synchronized void check() {
            checkingCount++;

            if (producing) {
                throw new RuntimeException("Can't check if producer is producing");
            }

            if (checkingCount > 10) {
                throw new RuntimeException(
                        "No more than 10 checks can be in progress simultaneously"
                );
            }

            System.out.println("Controller is checking...");

            checkingCount--;
        }
    }

    public static class Producer extends Thread {
        private final Buffer buffer;

        public Producer(Buffer b) {
            this.buffer = b;
        }

        public void execute() throws InterruptedException {
            producer.acquire();
            buffer.produce();
            producer.release();
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

    public static class Controller extends Thread {

        private final Buffer buffer;

        public Controller(Buffer buffer) {
            this.buffer = buffer;
        }

        public void execute() throws InterruptedException {
            controller.acquire();
            if (numChecks == 0)
                producer.acquire();
            lock.lock();
            numChecks++;
            if (numChecks == 10) {
                canCheck.release(10);
            }
            lock.unlock();
            canCheck.acquire();
            buffer.check();
            lock.lock();
            numChecks--;
            if (numChecks == 0) {
                controller.release(10);
                producer.release();
            }
            lock.unlock();
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
        Buffer buffer = new Buffer();
        Producer p = new Producer(buffer);
        List<Controller> controllers = new ArrayList<>();
        init();
        for (int i = 0; i < 100; i++) {
            controllers.add(new Controller(buffer));
        }
        p.start();
        for (int i = 0; i < 100; i++) {
            controllers.get(i).start();
        }

    }

}
