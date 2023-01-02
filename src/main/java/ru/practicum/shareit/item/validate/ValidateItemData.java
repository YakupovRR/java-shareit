package ru.practicum.shareit.item.validate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.InputDataException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

@Slf4j
@Component
public class ValidateItemData {

    private Item item;
    private Integer userId;

    private UserService userService;

    private void setItem(Item item) {
        this.item = item;
    }

    private void setUserId(Integer userId) {
        this.userId = userId;
    }

    private void setUserService(UserService userService) {
        this.userService = userService;
    }

    public boolean checkAllData(Integer userId, Item item, UserService userService) {
        setItem(item);
        setUserId(userId);
        setUserService(userService);
        return (isCorrectName() && isCorrectDescription() && isCorrectAvailable() && userIdNotNull() &&
                isContainsUser());
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
            log.warn("Ошибка во входных данных. Не заполнено поле доступность вещи");
            return false;
        } else {
            return (item.getAvailable() != null);
        }
    }

    public boolean userIdNotNull() {
        if (userId == null) {
            throw new ValidationException("Отсутствует id пользователя, создавший данную вещь");
        } else {
            return true;
        }
    }

    public boolean isContainsUser() {
        if (!userService.isContainsUser(userId)) {
            throw new InputDataException("Пользователь с id=" + userId + " не найден");
        } else {
            return true;
        }
    }
}