package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.InputDataException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceIntegrationTest {
    private final UserService userService;
    private final UserMapper userMapper;
    private final User mockUser = User.builder().id(1).name("User").email("user@ya.ru").build();


    @Test
    void testFindUserById() throws InputDataException {
        userService.addUser(userMapper.toUserDto(mockUser));

        UserDto user = userMapper.toUserDto(userService.getUser(1));

        assertThat(user.getId(), equalTo(mockUser.getId()));
        assertThat(user.getName(), equalTo(mockUser.getName()));
        assertThat(user.getEmail(), equalTo(mockUser.getEmail()));
    }

    @Test
    void testFindUserWrongId() {
        Exception exception = assertThrows(InputDataException.class, () -> userService.getUser(1));
        assertEquals("Пользователь с таким id не найден", exception.getMessage());
    }
}