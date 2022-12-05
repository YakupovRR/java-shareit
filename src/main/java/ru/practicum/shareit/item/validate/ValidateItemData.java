package ru.practicum.shareit.item.validate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.InputDataException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.service.UserService;

@Slf4j
@Component
public class ValidateItemData {

    private Item item;
    private UserService userService;

    private void setItem(Item item) {
        this.item = item;
    }

    public boolean checkAllData(Item item) {
        setItem(item);
        if (isCorrectName() && isCorrectDescription() && isCorrectAvailable()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isCorrectName() {
        if (item.getName() != null && !item.getName().isEmpty()) {
            return true;
        } else {
            log.warn("Ошибка во входных данных. Некорректное название вещи.");
            return false;
        }
    }

    public boolean isCorrectDescription() {
        if (item.getDescription() != null && !item.getDescription().isEmpty()) {
            return true;
        } else {
            log.warn("Ошибка во входных данных. Описание вещи отсутствует или не заполнено");
            return false;
        }
    }

    public boolean isCorrectAvailable() {
        if (item.getAvailable() == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validateAddItem (Integer userId, Item item) {
        return (userId != null)&& (userService.isContainsUser(userId)) && checkAllData(item);
    }
}
