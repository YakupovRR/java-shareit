package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.requests.ItemRequest;

import javax.validation.constraints.NotNull;

/**
 * // TODO .
 */
@Data
public class Item {
    private int id;
    private String name;
    private String description;
    @NotNull
    private Boolean available;
    private int userId;
    private ItemRequest request;

    public Item(int id, String name, String description, Boolean available, int userId, ItemRequest request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.userId = userId;
        this.request = request;
    }
}