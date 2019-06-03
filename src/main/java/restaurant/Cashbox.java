package restaurant;

import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

public class Cashbox {
    private final Restaurant restaurant;

    private final int number;
    private final Deque<Visitor> queue;

    public Cashbox(Restaurant restaurant, int number, Deque<Visitor> queue) {
        this.restaurant = restaurant;
        this.number = number;
        this.queue = queue;
    }

    public void startWork() {
        new Thread(new Script()).start();
    }

    public int getNumber() { return number; }

    public Deque<Visitor> getQueue() { return queue; }

    private void serveFirst() {
        var locks = restaurant.getQueuesToLocks();
        ReentrantLock lock = locks.get(queue);
        lock.lock();
        try {
            Optional.ofNullable(queue.pollFirst())
                    .ifPresent(user ->
                            Record.recorded("Cashier number "
                                    + this.getNumber()
                                    + " served "
                                    + user.toString()));
        } finally { lock.unlock(); }
    }

    private class Script implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    serveFirst();
                    Thread.sleep(9);
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }
}