package library;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

public class Library {
    private Map<String, Book> bookNames = new HashMap<>();
    private Map<Book, MutableAvailability> books = new HashMap<>();

    public boolean getBook(String bookName, User user, MutableAvailability.Type type) {
        requireNonNull(bookName);
        requireNonNull(user);
        requireNonNull(type);

        Book book = bookNames.get(bookName);
        MutableAvailability availability = books.get(book);
        return availability.tryToGet(type, user);
    }

    public void returnBook(Book book) {
        requireNonNull(book);
        books.get(book).giveBack();
    }

    public void addBook(Book book, MutableAvailability availability) {
        requireNonNull(book);
        requireNonNull(availability);
        bookNames.put(book.getName(), book);
        books.put(book, availability);
    }

    public Map<Book, Availability> getBooks() {
        return books.entrySet().stream()
                .collect(toMap(
                        Map.Entry::getKey,
                        bookToAvailability -> (Availability) bookToAvailability.getValue()));
    }
}
