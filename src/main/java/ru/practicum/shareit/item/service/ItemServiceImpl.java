package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.InputDataException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.validate.ValidateItemData;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemServiceImpl implements ItemService {


    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ValidateItemData validateItemData;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserService userService, ValidateItemData validateItemData) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.validateItemData = validateItemData;
    }

@Override
    public ItemDto addItem(ItemDto itemDto, Integer userId) {
        Item item = ItemMapper.fromItemDto(itemDto);
        item.setUserId(userId);
        if (userId == null) {
            throw new ValidationException("Отсутствует id пользователя, создавший данную вещь");
        }
        if (!userService.isContainsUser(userId)) {
            throw new InputDataException("Пользователь с id=" + userId + " не найден в БД");
        }
        if (validateItemData.checkAllData(item)) {
            item.setId(userId);
            return ItemMapper.toItemDto(itemRepository.addItem(item));
        } else {
            throw new ValidationException("Ошибка во входных данных");
        }
    }
    @Override
    public ItemDto getItemById(int id) {
        if (isContainItem(id)) {
            return ItemMapper.toItemDto(itemRepository.getItemById(id));
        } else {
            throw new InputDataException("Вещь по id не найдена");
        }
    }
    @Override
    public List<ItemDto> getItemsByUserId(int userId) {
        return itemRepository.getItemsByUserId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
    @Override
    public List<ItemDto> getItemsBySubString(String text) {
        return itemRepository.getItemsBySubString(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
    @Override
    public List<ItemDto> getAllItems(Integer userId) {
        if (userId != null) {
            return getItemsByUserId(userId);
        } else {
            return itemRepository.getAllItems()
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }
    @Override
    public ItemDto updateItem(ItemDto itemDto, Integer userId) {
        if (userId == null) {
            throw new ValidationException("Отсутствует id пользователя, создавший данную вещь");
        }
        Item itemFromDb = itemRepository.getItemById(itemDto.getId());
        if (itemFromDb.getUserId() == userId) {
            Item item = ItemMapper.fromItemDto(itemDto);
            return ItemMapper.toItemDto(itemRepository.updateItem(item));
        } else {
            throw new InputDataException("Id пользователя не совпадает с id создавшего вещь пользователя");
        }
    }
    @Override
    public void deleteItem(int id) {
        itemRepository.deleteItem(id);
    }
    @Override
    public boolean isContainItem(int id) {
        return itemRepository.isContainItem(id);
    }

}
