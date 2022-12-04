package ru.practicum.shareit.user.model;

import lombok.Data;

/**
 * // TODO .
 */
@Data
public class User {
    private int id;
    private String name;
    private String email;

    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
