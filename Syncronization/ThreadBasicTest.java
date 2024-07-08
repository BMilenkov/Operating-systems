package Syncronization;

public class ThreadBasicTest {
    public static void main(String[] args) {
        Thread ta = new ThreadA();
        Thread tb = new ThreadB();
        ta.start();
        tb.start();
        System.out.println("Main done");
    }
}
class ThreadA extends Thread {
    public void run() {
        for (int i = 1; i <= 20; i ++) {
            System.out.println("A: " + i);
        }
        System.out.println("A done");
    }
}
class ThreadB extends Thread {
    public void run() {
        for (int i = -1; i >= -20; i --) {
            System.out.println("\t\tB: " + i);
        }
        System.out.println("B done");
    }
}