package Syncronization.Pocker;

import Syncronization.StateEvaluationClasses.ProblemExecution;
import Syncronization.StateEvaluationClasses.TemplateThread;


import java.util.HashSet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Poker {

    private static Lock lock;
    private static int onTable;
    private static int participants;
    private static Semaphore seatPlayer;
    private static Semaphore canPlay;
    private static Semaphore newCycle;

    public static void init() {
        lock = new ReentrantLock();
        newCycle = new Semaphore(15);
        seatPlayer = new Semaphore(5);
        canPlay = new Semaphore(0);
        onTable = 0;
        participants = 0;
    }

    public static class Player extends TemplateThread {

        public Player(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            newCycle.acquire();
            seatPlayer.acquire();
            state.playerSeat();
            lock.lock();
            onTable++;
            if (onTable == 5) {
                canPlay.release(5);
            }
            lock.unlock();
            canPlay.acquire();
            state.play();
            lock.lock();
            participants++;
            onTable--;
            if (onTable == 0) {
                state.endRound();
                seatPlayer.release(5);
            }
            if (participants == 15) {
                state.endCycle();
                newCycle.release(15);
                participants = 0;
            }
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            run();
        }
    }

    static PokerState state = new PokerState();

    public static void run() {
        try {
            int numRuns = 20;
            int numIterations = 15;

            HashSet<Thread> threads = new HashSet<Thread>();

            for (int i = 0; i < numIterations; i++) {
                Player c = new Player(numRuns);
                threads.add(c);
            }

            init();

            ProblemExecution.start(threads, state);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}