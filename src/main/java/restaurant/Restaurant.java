package restaurant;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Restaurant {
    private List<Cashbox> cashboxes;
    private volatile Map<Deque<Visitor>, ReentrantLock> queuesToLocks = new HashMap<>();

    public Restaurant(int cashboxCount) {
        cashboxes = new ArrayList<>(cashboxCount);
        for (int i = 1; i <= cashboxCount; i++) {
            Deque<Visitor> queue = new ArrayDeque<>();
            queuesToLocks.put(queue, new ReentrantLock());
            cashboxes.add(new Cashbox(this, i, queue));
        }
    }

    public void open() { for (Cashbox cashbox : cashboxes) cashbox.startWork(); }

    public Map<Deque<Visitor>, ReentrantLock> getQueuesToLocks() { return queuesToLocks; }

    public List<Cashbox> getCashboxes() { return cashboxes; }

    public Cashbox getCashbox(Deque<Visitor> queue) {
        for (Cashbox cashbox : cashboxes) { if (cashbox.getQueue() == queue) return cashbox; }
        return null;
    }
}