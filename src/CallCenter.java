import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

// TODO: Each class in the superclass needs a run method (run()).

public class CallCenter {
    private static final int CUSTOMERS_PER_AGENT = 5;
    private static final int NUMBER_OF_AGENTS = 3;
    private static final int NUMBER_OF_CUSTOMERS = NUMBER_OF_AGENTS * CUSTOMERS_PER_AGENT;
    private static final int NUMBER_OF_THREADS = 10;

    private final static Queue<Customer> wait = new LinkedList<>();
    private final static Queue<Customer> serve = new LinkedList<>();
    private final static ReentrantLock lock = new ReentrantLock();
    private final static Condition notEmpty = lock.newCondition();

    public static class Agent implements Runnable {
        private final int ID;

        public Agent(int i) {
            ID = i; // can modify constructor
        }

        public void serve(int customerID) {
            System.out.println("Agent " + ID + " is serving customer " + customerID);

            try {
                sleep(ThreadLocalRandom.current().nextInt(10, 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {

            }
        }
    } // Agent class

    public static class Greeter implements Runnable {
        public void greet(int customerID) {
            System.out.println("Greeting customer " + customerID);

            try {
                sleep(ThreadLocalRandom.current().nextInt(10, 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {

            }
        }

        public void run() {
            lock.lock();
            try {
                while () {
                    notEmpty.await();
                }
            } catch (InterruptedException e)
        }
    } // Greeter class

    public static class Customer implements Runnable {
        private final int ID;

        public Customer(int i) {
            ID = i;
        }
    } // Customer class

    public static void main(String[] args) {
        // complete this.
    }
}
