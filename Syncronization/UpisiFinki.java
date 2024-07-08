package Syncronization;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UpisiFinki {

    static int NUM_STUDENTS = 10;
    static Semaphore Commission;
    static Semaphore enter;
    static Semaphore here;
    static Semaphore done;

    public static void init() {
        Commission = new Semaphore(4);
        enter = new Semaphore(0);
        here = new Semaphore(0);
        done = new Semaphore(0);
    }

    public static class Clen extends Thread {

        public void execute() throws InterruptedException {
            Commission.acquire();
            for (int i = 0; i < NUM_STUDENTS; i++) {
                enter.release();
                here.acquire();
                zapisi();
                done.release();
            }
            Commission.release();
        }

        public void zapisi() {
            System.out.println("Zapisuvam student...");
        }

        @Override
        public void run() {
            try {
                execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Student extends Thread {

        public void execute() throws InterruptedException {
            enter.acquire();
            ostaviDokumenti();
            here.release();
            done.acquire();
        }

        public void ostaviDokumenti() {
            System.out.println("Ostavam dokumenti...");
        }

        @Override
        public void run() {
            try {
                execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public static void main(String[] args) {
            init();
            ArrayList<Clen> clenovi = IntStream.range(0, 50)
                    .mapToObj(i -> new Clen())
                    .collect(Collectors.toCollection(ArrayList::new));
            ArrayList<Student> studenti =
                    IntStream.range(0, 200)
                            .mapToObj(i -> new Student())
                            .collect(Collectors.toCollection(ArrayList::new));
            clenovi.forEach(Thread::start);
            studenti.forEach(Thread::start);
        }
    }
}
