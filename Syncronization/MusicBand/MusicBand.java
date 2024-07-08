package Syncronization.MusicBand;


import Syncronization.StateEvaluationClasses.ProblemExecution;
import Syncronization.StateEvaluationClasses.TemplateThread;

import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MusicBand {


    private static Semaphore singers;
    private static Semaphore guitarists;
    private static int performers;
    private static Lock lock;
    private static Semaphore canPlay;
    private static Semaphore canLeave;
    static MusicBandState state = new MusicBandState();

    public static void init() {
        performers = 0;
        lock = new ReentrantLock();
        singers = new Semaphore(2);
        guitarists = new Semaphore(3);
        canLeave = new Semaphore(0);
        canPlay = new Semaphore(0);
    }

    public static class GuitarPlayer extends TemplateThread {

        public GuitarPlayer(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            guitarists.acquire();
            lock.lock();
            performers++;
            if (performers == 5)
                canPlay.release(5);
            lock.unlock();
            canPlay.acquire();
            state.play();
            lock.lock();
            performers--;
            if (performers == 0) {
                state.evaluate();
                canLeave.release(5);
            }
            lock.unlock();
            canLeave.acquire();
            guitarists.release();
        }
    }

    public static class Singer extends TemplateThread {

        public Singer(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            singers.acquire();
            lock.lock();
            performers++;
            if (performers == 5)
                canPlay.release(5);
            lock.unlock();
            canPlay.acquire();
            state.play();
            lock.lock();
            performers--;
            if (performers == 0) {
                state.evaluate();
                canLeave.release(5);
            }
            lock.unlock();
            canLeave.acquire();
            singers.release();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            run();
        }
    }

    public static void run() {
        try {
            Scanner s = new Scanner(System.in);
            int numRuns = 1;
            int numIterations = 100;
            s.close();

            HashSet<Thread> threads = new HashSet<Thread>();

            for (int i = 0; i < numIterations; i++) {
                Singer singer = new Singer(numRuns);
                threads.add(singer);
                GuitarPlayer gp = new GuitarPlayer(numRuns);
                threads.add(gp);
                gp = new GuitarPlayer(numRuns);
                threads.add(gp);
                singer = new Singer(numRuns);
                threads.add(singer);
                gp = new GuitarPlayer(numRuns);
                threads.add(gp);
            }

            init();

            ProblemExecution.start(threads, state);
            System.out.println(new Date().getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}