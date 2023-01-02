package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    @Override
    public User addUser(User user) {
        int id = getId();
        user.setId(id);
        users.put(id, user);
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
            userFromMemory.setEmail(user.getEmail());
        }
        users.put(id, userFromMemory);
        return userFromMemory;
    }

    @Override
    public void deleteUser(int id) {
        users.remove(id);
    }

    @Override
    public boolean isContainUser(int id) {
        return users.containsKey(id);
    }

    @Override
    public boolean isExistEmail(String email) {
        boolean isExistEmail = false;
        for (User user :users.values()) {
            if (user.getEmail().equals(email)) {
                isExistEmail = true;
                break;
            }
        }
        return isExistEmail;
    }

    private int getId() {
        id++;
        return id;
    }
}
