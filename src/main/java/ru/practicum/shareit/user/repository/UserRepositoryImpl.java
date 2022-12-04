package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserRepositoryImpl implements UserRepository {

    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;
private HashSet<String> emailBase = new HashSet<>();

    @Override
    public User addUser(User user) {
        int id = getId();
        user.setId(id);
        users.put(id, user);
        emailBase.add(user.getEmail());
        return user;
    }

    @Override
    public User getUser(int id) {
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User updateUser(User user) {
        int id = user.getId();
        User userFromMemory = users.get(id);
        if (user.getName() != null) {
            userFromMemory.setName(user.getName());
        }
        if (user.getEmail() != null) {
            emailBase.remove(userFromMemory.getEmail());
            userFromMemory.setEmail(user.getEmail());
            emailBase.add(userFromMemory.getEmail());
        }
        users.put(id, userFromMemory);
        return userFromMemory;
    }

    @Override
    public void deleteUser(int id) {
        emailBase.remove(getUser(id).getEmail());
        users.remove(id);
    }

    @Override
    public boolean isContainUser(int id) {
        return users.containsKey(id);
    }

    @Override
    public boolean isExistEmail(String email) {
        return emailBase.contains(email);

    }
    private int getId() {
        id++;
        return id;
    }
}
