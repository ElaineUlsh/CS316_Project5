import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

public class CallCenter {
    private static final int CUSTOMERS_PER_AGENT = 5;
    private static final int NUMBER_OF_AGENTS = 3;
    private static final int NUMBER_OF_CUSTOMERS = NUMBER_OF_AGENTS * CUSTOMERS_PER_AGENT;
    private static final int NUMBER_OF_THREADS = 15;

    private final static Queue<Customer> wait = new LinkedList<>();
    private final static Queue<Customer> serve = new LinkedList<>();

    private final static ReentrantLock waitLock = new ReentrantLock();
    private final static ReentrantLock serveLock = new ReentrantLock();

    private final static Condition waitNotEmpty = waitLock.newCondition();
    private final static Condition serveNotEmpty = serveLock.newCondition();

    public static class Agent implements Runnable {
        private final int ID;

        public Agent(int i) {
            ID = i;
        }

        public void run() {
            for (int i = 0; i < CUSTOMERS_PER_AGENT; i++) {
                Customer customer = null;
                serveLock.lock();
                try {
                    while (serve.isEmpty()) {
                        serveNotEmpty.await();
                    }
                    customer = serve.remove();
                } catch (InterruptedException e ){
                    e.printStackTrace();
                } finally {
                    serveLock.unlock();
                }
                if (customer != null) {
                    serve(customer.getID());
                }
            }
        }

        public void serve(int customerID) {
            System.out.println("Agent " + ID + " is serving customer " + customerID);

            try {
                sleep(ThreadLocalRandom.current().nextInt(10, 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    } // Agent class

    public static class Greeter implements Runnable {
        public void run() {
            for (int i = 0; i < NUMBER_OF_CUSTOMERS; i++) {
                Customer customer = null;
                waitLock.lock();
                try {
                    while (wait.isEmpty()) {
                        waitNotEmpty.await();
                    }
                    customer = wait.remove();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    waitLock.unlock();
                }
                if (customer != null) {
                    greet(customer.getID());
                }

                serveLock.lock();
                try {
                    serve.add(customer);
                    serveNotEmpty.signal();
                    System.out.println("Customer position in the serve queue is " + serve.size());
                } finally {
                    serveLock.unlock();
                }
            }
        }

        public void greet(int customerID) {
            System.out.println("Greeting customer " + customerID);

            try {
                sleep(ThreadLocalRandom.current().nextInt(10, 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    } // Greeter class

    public static class Customer implements Runnable {
        private final int ID;

        public Customer(int i) {
            ID = i;
        }

        public void run() {
            waitLock.lock();
            try {
                wait.add(this);
                waitNotEmpty.signal();
            } finally {
                waitLock.unlock();
            }
        }

        public int getID() {
            return ID;
        }
    } // Customer class

    public static void main(String[] args) {
        ExecutorService es = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        es.submit(new Greeter());
        for (int j = 1; j <= NUMBER_OF_AGENTS; j++) {
            es.submit(new Agent(j));
        }

        for (int i = 1; i <= NUMBER_OF_CUSTOMERS; i++) {
            es.submit(new Customer(i));
        }

        es.shutdown();
    }
}
