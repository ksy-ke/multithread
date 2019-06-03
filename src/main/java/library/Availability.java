package library;

import java.util.Optional;

public interface Availability {
    Optional<User> getUser();
    Optional<Type> getType();

    enum Type {READING_ROOM, HOUSE}
}