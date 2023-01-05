package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Integer, Item> items = new HashMap<>();
    private int id = 0;

    @Override
    public Item addItem(Item item) {
        int id = getId();
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Item getItemById(int id) {
        return items.get(id);
    }

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> getItemsByUserId(int userId) {
        return getAllItems()
                .stream()
                .filter(i -> Objects.equals(i.getOwner().getId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getItemsBySubString(String text) {
        List<Item> findItems = new ArrayList<>();
        if (text.isEmpty()) {
            return findItems;
        }
        for (Item item : items.values()) {
            boolean condition = item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase());
            if (condition && item.getAvailable()) {
                findItems.add(item);
            }
        }
        return findItems;
    }

    @Override
    public Item updateItem(Item item) {
        int idItem = item.getId();
        Item itemDb = items.get(idItem);
        if (item.getName() != null) {
            itemDb.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemDb.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemDb.setAvailable(item.getAvailable());
        }
        items.put(idItem, itemDb);
        return itemDb;
    }

    @Override
    public void deleteItem(int id) {
        if (isContainItem(id)) {
            items.remove(id);
        }
    }

    @Override
    public boolean isContainItem(int id) {
        return items.containsKey(id);
    }

    private int getId() {
        id++;
        return id;
    }
}
