package library;

import java.util.Optional;
import java.util.Set;

public class MutableAvailability implements Availability {

    private final Set<Type> allowedTypes;
    private User user;
    private Type type;

    public MutableAvailability(Set<Type> allowedTypes) { this.allowedTypes = allowedTypes; }

    public synchronized boolean tryToGet(Type type, User user) {
        if (getUser().isPresent()) return false;
        if (!allowedTypes.contains(type)) return false;

        this.type = type;
        this.user = user;
        return true;
    }

    public void giveBack() {
        user = null;
        type = null;
    }

    public Optional<User> getUser() { return Optional.ofNullable(user); }
    public Optional<Type> getType() { return Optional.ofNullable(type); }
}