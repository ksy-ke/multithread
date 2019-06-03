package restaurant;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.stream.IntStream;

import static java.lang.Thread.currentThread;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertTrue;

public class RestaurantTest {
    private Restaurant restaurant;

    @Before
    public void setUp() { restaurant = new Restaurant(3); }

    @Test
    public void start() {
        // when
        restaurant.open();
        IntStream.range(0, 10).forEach(i -> new Visitor(String.valueOf(i), restaurant));

        // then
        waitForQueuesToGetEmpty();
        assertTrue(areQueuesEmpty());
    }

    private void waitForQueuesToGetEmpty() {
        try {
            while (true) {
                if (areQueuesEmpty()) return;
                MILLISECONDS.sleep(5);
            }
        } catch (InterruptedException error) {
            System.out.println("Wait for queues to get empty interrupted");
            currentThread().interrupt();
        }
    }

    private boolean areQueuesEmpty() {
        return restaurant.getQueuesToLocks().keySet().stream().allMatch(Collection::isEmpty);
    }
}