package library;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

public class LibraryTest {
    public static final Book LOLITA = new Book("Lolita");
    private Library library;
    private ExecutorService executorService;

    @Before
    public void setUp() {
        library = new Library();

        library.addBook(LOLITA, new MutableAvailability(EnumSet.allOf(MutableAvailability.Type.class)));
        library.addBook(new Book("The Willpower Instinct"), new MutableAvailability(EnumSet.allOf(MutableAvailability.Type.class)));
        library.addBook(new Book("Thirteen Reasons Why"), new MutableAvailability(EnumSet.allOf(MutableAvailability.Type.class)));
        library.addBook(new Book("Effective programming"), new MutableAvailability(EnumSet.of(Availability.Type.READING_ROOM)));
        library.addBook(new Book("Java concurrent in practice"), new MutableAvailability(EnumSet.of(Availability.Type.READING_ROOM)));
        library.addBook(new Book("Simon vs. the Homo Sapiens Agenda"), new MutableAvailability(EnumSet.of(Availability.Type.READING_ROOM)));
    }

    @After
    public void tearDown() { if (executorService != null) executorService.shutdownNow(); }

    public @Test     void getBook_bookExists_gotBookAndAnotherCantGetIt() {
        // given
        var firstUser = new User(1);
        var secondUser = new User(2);

        // when
        var gotBook = library.getBook(LOLITA.getName(), firstUser, Availability.Type.READING_ROOM);

        // then
        assertTrue(gotBook);
        assertFalse(library.getBook(LOLITA.getName(), secondUser, Availability.Type.READING_ROOM));
    }

    public @Test void tryToGet_manyUsersGettingSameBook_onlyOneUserGotBook() {
        // given
        int threads = 1000;
        executorService = Executors.newFixedThreadPool(threads);
        var latch = new CountDownLatch(threads);

        // when
        long gotBooks = IntStream.range(0, threads)
                .mapToObj(User::new)
                .map(user -> (Callable<Boolean>) () -> {
                    latch.countDown();
                    latch.await();

                    return library.getBook(LOLITA.getName(), user, Availability.Type.HOUSE);
                })
                .map(executorService::submit)
                .collect(toList())
                .stream()
                .map(this::getOrFail)
                .filter(Boolean::booleanValue)
                .count();

        // then
        assertEquals(1, gotBooks);
    }

    private boolean getOrFail(Future<Boolean> future) {
        try {
            return future.get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("Failed to get result: " + e.toString());
            fail(e.getMessage());
            return false;       // Never happened
        }
    }
}