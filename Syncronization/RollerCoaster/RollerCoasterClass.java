package Syncronization.RollerCoaster;


import Syncronization.StateEvaluationClasses.ProblemExecution;
import Syncronization.StateEvaluationClasses.TemplateThread;

import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author bmilenkov
 */


public class RollerCoasterClass {

    public static Semaphore car;
    public static Semaphore passenger;
    public static Semaphore herePass;
    public static Semaphore AllPassHere;
    public static Semaphore canValidate;
    public static int numPassengers;
    public static Lock lock;


    public static void init() {
        car = new Semaphore(1);
        passenger = new Semaphore(0);
        herePass = new Semaphore(0);
        AllPassHere = new Semaphore(0);
        canValidate = new Semaphore(0);
        numPassengers = 0;
        lock = new ReentrantLock();

    }

    public static class Car extends TemplateThread {

        public Car(int numRuns) {
            super(numRuns);
        }

        //load() run() unload()
        @Override
        public void execute() throws InterruptedException {

            car.acquire();
            state.load();
            passenger.release(10);

            AllPassHere.acquire();
            state.run();
            state.unload();
            herePass.release(10);
            canValidate.acquire();
            state.validate();
            car.release();
        }
    }

    public static class Passenger extends TemplateThread {

        public Passenger(int numRuns) {
            super(numRuns);
        }

        //board() unboard()
        @Override
        public void execute() throws InterruptedException {
            passenger.acquire();
            state.board();

            lock.lock();
            numPassengers++;
            lock.unlock();

            if (numPassengers == 10) {
                AllPassHere.release();
            }
            herePass.acquire();
            state.unboard();
            lock.lock();
            numPassengers--;
            if (numPassengers == 0) {
                canValidate.release();
            }
            lock.unlock();
        }
    }

    static RollerCoasterState state = new RollerCoasterState();

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            run();
        }
    }

    public static void run() {
        try {
            int numRuns = 1;
            int numScenarios = 100;
            HashSet<Thread> threads = new HashSet<Thread>();

            for (int i = 0; i < numScenarios; i++) {
                Passenger p = new Passenger(numRuns);
                threads.add(p);
                if (i % 10 == 0) {
                    Car c = new Car(numRuns);
                    threads.add(c);
                }
            }

            init();

            ProblemExecution.start(threads, state);
            System.out.println(new Date().getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}