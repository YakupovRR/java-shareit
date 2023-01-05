package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;


import java.util.List;

public interface ItemStorage {

    Item addItem(Item item);

    Item getItemById(int id);

    List<Item> getItemsByUserId(int userId);

    List<Item> getItemsBySubString(String text);

    List<Item> getAllItems();

    Item updateItem(Item item);

    void deleteItem(int id);

    boolean isContainItem(int id);
}
