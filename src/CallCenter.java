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
    private final static Condition waitNotEmpty = lock.newCondition();
    private final static Condition waitNotFull = lock.newCondition();
    private final static Condition serveNotEmpty = lock.newCondition();
    private final static Condition serveNotFull = lock.newCondition();


    // simulates a customer service agent who answers customer calls
    // has unique ID
    // - removes a customer from the serve queue
    // - serves the customer by calling the provided serve mehtod
    public static class Agent implements Runnable {
        private final int ID;

        public Agent(int i) {
            ID = i; // can modify constructor
        }

        public void run() {
            // remove customer from the serve queue
            // calls the serve method
            for (int i = 0; i < CUSTOMERS_PER_AGENT; i++) {
                Customer customer = serve.remove();
                serve(customer.ID);
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

    // simulates the automated greeting service
    // - removes customers from the wait queue
    // - greets the customer by calling the provided greet method
    // - places the customer into the serve queue
    // - announces the customer's place in the serve queue
    public static class Greeter implements Runnable {
        public void run() {
            // removes the customer from the wait queue
            // calls the greet method
            // places the customer into the serve queue
            // announces teh customer's place in the server queue

            lock.lock();
            for (int i = 0; i < NUMBER_OF_CUSTOMERS; i++) {
                try {
                    while (wait.isEmpty()) {
                        waitNotEmpty.await();
                    }
                    Customer customer = wait.remove();
                    waitNotFull.signal();

                    greet(customer.ID);

                    while (serve.size() == NUMBER_OF_CUSTOMERS) {
                        serveNotFull.await();
                    }
                    serve.add(customer);
                    serveNotEmpty.signal();

                    System.out.println("Customer " + customer.ID + " is waiting to be serviced.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
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

    // simulates a customer call
    // has unique ID
    public static class Customer implements Runnable {
        private final int ID;

        public Customer(int i) {
            ID = i;
        }

        public void run() {
            // places customer into the wait queue
            // each customer is a separate runnable task
            lock.lock();
            try {
                while (wait.size() == NUMBER_OF_CUSTOMERS) {
                    waitNotFull.await();
                }
                wait.add(new Customer(ID));
                waitNotEmpty.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    } // Customer class

    public static void main(String[] args) {
        // complete this.
    }
}
