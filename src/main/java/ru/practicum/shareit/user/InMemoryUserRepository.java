package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictEmailException;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        Optional<User> findUser = users.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(user.getEmail()))
                .findFirst();
        if (findUser.isPresent()) {
            throw new ConflictEmailException("Email " + user.getEmail() + " is already presented");
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        User prevUser = users.get(user.getId());
        users.remove(user.getId());
        Optional<User> sameEmailUser = users.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(user.getEmail()))
                .findFirst();
        if (sameEmailUser.isPresent()) {
            users.put(prevUser.getId(), prevUser);
            throw new ConflictEmailException("Email " + user.getEmail() + " is already presented");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Collection<User> findAll() {
        return Collections.unmodifiableCollection(users.values());
    }

    private long generateId() {
        long prevId = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
        return ++prevId;
    }
}
