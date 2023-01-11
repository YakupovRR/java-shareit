package ru.practicum.shareit.user.validate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class ValidateUserDataTest {
    private final ValidateUserData validate = new ValidateUserData();
    private final User userEmptyName = User.builder()
            .id(1)
            .name("")
            .email("ab@ya.ru")
            .build();
    private final User userEmptyMail = User.builder()
            .id(1)
            .name("Mike")
            .email("")
            .build();
    private final User userIncorrectMail = User.builder()
            .id(1)
            .name("Mike")
            .email("abya.ru")
            .build();

    @Test
    void testEmptyName() {
        assertFalse(validate.checkAllData(userEmptyName));
    }

    @Test
    void testEmptyEmail() {
        assertFalse(validate.checkAllData(userEmptyMail));
    }

    @Test
    void testIncorrectEmail() {
        assertFalse(validate.checkAllData(userIncorrectMail));
    }
}