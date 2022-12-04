package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

@Service
public interface ItemService {


    public ItemDto addItem(ItemDto itemDto, Integer userId);

    public ItemDto getItemById(int id);

    public List<ItemDto> getItemsByUserId(int userId);

    public List<ItemDto> getItemsBySubString(String text);

    public List<ItemDto> getAllItems(Integer userId);

    public ItemDto updateItem(ItemDto itemDto, Integer userId);

    public void deleteItem(int id);

    public boolean isContainItem(int id);
}
