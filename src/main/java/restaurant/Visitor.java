package restaurant;

import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Visitor {
    private final static ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private final Restaurant restaurant;
    private static int COUNTER = 3;

    private final String name;
    private final int number;
    private Deque<Visitor> currentQueue;

    public Visitor(String name, Restaurant restaurant) {
        this.restaurant = restaurant;
        this.name = name;
        number = COUNTER++;
        setFirstQueue();
        new Thread(new UserThread()).start();
    }

    public String getName() { return name; }

    private void setFirstQueue() {
        List<Cashbox> cashboxes = restaurant.getCashboxes();
        Map<Deque<Visitor>, ReentrantLock> queuesToLocks = restaurant.getQueuesToLocks();

        int cashboxNumber = RANDOM.nextInt(cashboxes.size());
        Cashbox cashbox = cashboxes.get(cashboxNumber);

        Deque<Visitor> queue = cashbox.getQueue();
        ReentrantLock lock = queuesToLocks.get(queue);

        lock.lock();
        try {
            queue.add(this);
            currentQueue = queue;
        } finally {
            lock.unlock();
            Record.recorded(this.toString()
                    + " got in queue cashbox register number "
                    + cashbox.getNumber());
        }
    }

    private void moveToShortQueue() {
        Map<Deque<Visitor>, ReentrantLock> queuesToLocks = restaurant.getQueuesToLocks();
        Lock lockCurrentQueue = queuesToLocks.get(currentQueue);

        lockCurrentQueue.lock();
        try {
            if (this == currentQueue.peekFirst() || this != currentQueue.peekLast()) return;
            int currentQueueLength = currentQueue.size() - 1;

            for (Deque<Visitor> queue : queuesToLocks.keySet()) {
                Lock lock = queuesToLocks.get(queue);
                if (!lock.tryLock(5, TimeUnit.SECONDS)) continue;

                try {
                    if (queue == currentQueue) continue;
                    if (currentQueueLength <= queue.size()) continue;

                    queue.addLast(currentQueue.removeLast());
                    currentQueue = queue;
                    Record.recorded(this.toString() +
                            " record to the queue with number " +
                            restaurant.getCashbox(currentQueue).getNumber());
                } finally {
                    lock.unlock();
                }
                break;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lockCurrentQueue.unlock();
        }
    }

    class UserThread implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    moveToShortQueue();
                    Thread.sleep(number);
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    @Override
    public String toString() { return "Visitor " + this.getName(); }
}
