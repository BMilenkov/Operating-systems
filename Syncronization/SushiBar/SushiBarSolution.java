package Syncronization.SushiBar;

import Syncronization.StateEvaluationClasses.ProblemExecution;
import Syncronization.StateEvaluationClasses.TemplateThread;

import java.util.HashSet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author bmilenkov
 */


public class SushiBarSolution {

    private static int numClients;
    private static Semaphore clients;
    private static Semaphore canEat;
    private static Lock lock;

    public static void init() {
        numClients = 0;
        lock = new ReentrantLock();
        clients = new Semaphore(6);
        canEat = new Semaphore(0);
    }

    public static class Customer extends TemplateThread {

        public Customer(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            clients.acquire();
            state.customerSeat();
            lock.lock();
            numClients++;
            if (numClients == 6) {
                state.callWaiter();
                canEat.release(6);
            }
            lock.unlock();

            canEat.acquire();
            state.customerEat();

            lock.lock();
            numClients--;
            if (numClients == 0) {
                    state.eatingDone();
                    clients.release(6);
            }
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            run();
        }
    }

    static SushiBarState state = new SushiBarState();

    public static void run() {
        try {
            int numRuns = 1;
            int numIterations = 1200;

            HashSet<Thread> threads = new HashSet<Thread>();

            for (int i = 0; i < numIterations; i++) {
                Customer c = new Customer(numRuns);
                threads.add(c);
            }

            init();

            ProblemExecution.start(threads, state);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}