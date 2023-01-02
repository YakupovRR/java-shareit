package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User addUser(User user);

    User getUser(int id);

    List<User> getAllUsers();

    User updateUser(User user);

    void deleteUser(int id);

    boolean isContainUser(int id);

    boolean isExistEmail(String email);

}
