package Syncronization;


//OS SEPT 2023
//
// Даден е код кој пресметува збир на броеви кои се наоѓаат во даден ранг од една целобројна низа. Горната и долната граница на рангот се генерираат случајно и нивните вредности се дадени во променливите LOWER_BOUND и UPPER_BOUND соодветно.
//
// Дадена е верзија на секвенцијална имплементација на пресметување на збирот. Да се имплементира верзија која истото ќе го прави со повеќе нитки. Дефинирајте ги сите потребни елементи за синхронизација и иницијализирајте ги во методот init().
// Со оглед на тоа што низата е голема, оптимизацијата на пребарувањето е паралелизирана со повеќе нитки. Да се модифицира кодот со тоа што ќе се стартуваат 15 нитки и секоја од нив ќе пресметува збир на бараните броеви низ подниза со еднаква големина.
// На крај, во главната нитка да се испечати вкупната сума на броевите кои се наоѓаат во тој ранг. После тоа, нитката која што го нашла најголемиот збир треба тоа да го испечати.
// Напомxена: Да не се менуваат деловите од кодот каде што има коментари дека не треба да се менува. Да се внимава на правилно стартување и стопирање на нитките, како и на евентуална потреба од синхронизација.
// NAPOMENA NAPOMENA:: DAVA MALKU IZMESTENI BROEVI BIDEJKI ARRAY LENGTH E 100.000 ELEMENTI. 100.000 NA 15 DELA DAVA 6666,6666... AKO SE ZAMENI ARRAY LENGTH SO 150.000, REZULTATOT ISPAGJA DOBAR

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RangeSum {
    public static long sum = 0;
    public static long maxSum = 0;
    public static int counter = 0;

    static final BoundedRandomGenerator random = new BoundedRandomGenerator();

    private static final int ARRAY_LENGTH = 150000;

    private static final int NUM_THREADS = 15;

    private static final int LOWER_BOUND = random.nextInt(); // This is the lower bound of the range
    private static final int UPPER_BOUND = random.nextInt() + LOWER_BOUND; // This is the upper bound of the range

    // TODO: Define sychronization elements
    private static Lock lock;
    private static Semaphore done;
    private static Semaphore canCheck;

    static void init() {
// TODO: Initialize synchronization elements
        lock = new ReentrantLock();
        done = new Semaphore(0);
        canCheck = new Semaphore(0);
    }

    // DO NOT CHANGE
    public static int[] getSubArray(int[] array, int start, int end) {
        return Arrays.copyOfRange(array, start, end);
    }

    public static void main(String[] args) throws InterruptedException {

        init();

        int[] arr = ArrayGenerator.generate(ARRAY_LENGTH, LOWER_BOUND, UPPER_BOUND);

// TODO: Make the SearchThread class a thread and start 15 instances

        List<SearchThread> searchThreads = new ArrayList<>();
        int start = 0;
        int end = ARRAY_LENGTH / 15;
        for (int i = 0; i < NUM_THREADS; i++) {
            searchThreads.add(new SearchThread(getSubArray(arr, start, end), LOWER_BOUND, UPPER_BOUND));
            start = end;
            if (i == NUM_THREADS - 2) {
                end = ARRAY_LENGTH;
            } else
                end = end + (ARRAY_LENGTH / 15);
        }

// TODO: Start the 15 threads
        searchThreads.forEach(Thread::start);


// TODO: The thread that counted the largest sum should print that information


//        done.acquire(NUM_THREADS);
//        canCheck.release(NUM_THREADS);
        //Wait for all threads to terminate and gives back the result to the main!
        searchThreads.forEach(s -> {
            try {
                s.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

// DO NOT CHANGE

        System.out.println("The number of total counted elements is: " + sum);
        System.out.println("The generated number of elements is: " + ArrayGenerator.elementSum);
        System.out.println("Max sum: " + maxSum);


        SynchronizationChecker.checkResult();

    }

    // TO DO: Make the SearchThread class a thread
    static class SearchThread extends Thread {

        private int[] arr;
        private int lower;
        private int upper;
        private long localSum = 0;

        public SearchThread(int[] arr, int lower, int upper) {
            this.arr = arr;
            this.lower = lower;
            this.upper = upper;
        }

        public void searchArray() {
            for (int num : this.arr) {

                if (num > lower && num < upper) {
                    sum += num;

                }
            }
        }

        public void searchArrayParalel() throws InterruptedException {

            // TO DO: Implement and run this method from the thread

            localSum = Arrays.stream(arr).filter(e -> e > lower && e < upper).sum();
            lock.lock();
            counter++;
            sum += localSum;
            if (localSum > maxSum) {
                maxSum = localSum;
            }
            if (counter == NUM_THREADS)
                canCheck.release(NUM_THREADS);
            lock.unlock();

            canCheck.acquire();

            if (localSum == maxSum) {
                System.out.printf("Thread with id %s is the max thread , maxC = %d%n", threadId(), localSum);
            }
        }

        @Override
        public void run() {
            try {
                searchArrayParalel();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

// DO NOT CHANGE THE CODE BELOW TO THE END OF THE FILE

    static class BoundedRandomGenerator {
        static final Random random = new Random();
        static final int RANDOM_BOUND = 50;

        public int nextInt() {
            return random.nextInt(RANDOM_BOUND);
        }

        public int nextInt(int bound) {
            return random.nextInt(bound);
        }

    }

    static class ArrayGenerator {

        static long elementSum;

        static int[] generate(int length, int lower, int upper) {
            int[] array = new int[length];

            for (int i = 0; i < length; i++) {
                int element = RangeSum.random.nextInt(100);

                if (element > lower && element < upper) {
                    elementSum += element;
                }

                array[i] = element;
            }
            return array;
        }
    }

    static class SynchronizationChecker {
        public static void checkResult() {
            if (ArrayGenerator.elementSum != sum) {
                throw new RuntimeException(String.format("The calculated result is not equal to the actual number of occurences! %s != %s", ArrayGenerator.elementSum, sum));
            }
        }
    }
}
