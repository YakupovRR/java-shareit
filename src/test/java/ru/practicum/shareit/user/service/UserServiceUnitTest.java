package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.InputDataException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.validate.ValidateUserData;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceUnitTest {
    @Mock
    private UserRepository userRepository;

    private UserService userService;

    private MockitoSession mockitoSession;

    private final ValidateUserData validateUserData = new ValidateUserData();
    private final UserMapper userMapper = new UserMapper();

    @BeforeEach
    void setUp() {
        mockitoSession = Mockito.mockitoSession().initMocks(this).startMocking();
        userService = new UserService(userRepository, validateUserData, userMapper);
    }

    @AfterEach
    void finish() {
        mockitoSession.finishMocking();
    }

    private final User user1 = User.builder().id(1).name("User1").email("User1@ya.ru").build();
    private final User user1Update = User.builder().id(2).name("User1Update").email("User1@ya.ru").build();
    private final User user2 = User.builder().id(2).name("User2").email("User2@ya.ru").build();

    @Test
    void testAddUser() {
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user1);
        UserDto user = userService.addUser(userMapper.toUserDto(user1));

        Mockito.verify(userRepository, times(1)).save(user1);

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(user1.getName()));
        assertThat(user.getEmail(), equalTo(user1.getEmail()));
    }

    @Test
    void testGetUserById() throws InputDataException {
        Mockito.when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));

        User user = userService.getUser(1);

        Mockito.verify(userRepository, times(1)).findById(1);

        assertThat(user.getName(), equalTo(user1.getName()));
        assertThat(user.getEmail(), equalTo(user1.getEmail()));
    }

    @Test
    void testGetUserByWrongId() {
        Mockito.when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        Exception exception = assertThrows(InputDataException.class, () -> userService.getUser(1));

        assertEquals("Пользователь с таким id не найден", exception.getMessage());
    }

    @Test
    void testFindAll() {
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        Collection<UserDto> users = userService.getAllUsers();

        Mockito.verify(userRepository, times(1)).findAll();

        assertThat(users, hasSize(2));
    }

    @Test
    void testUpdateUser() throws InputDataException {
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user1Update);
        Mockito.when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));

        user1.setName("User1Update");
        UserDto userDto = userMapper.toUserDto(user1);
        UserDto user = userService.updateUser(userDto, user1.getId());

        Mockito.verify(userRepository, times(1)).save(user1);

        assertThat(user.getId(), equalTo(user1Update.getId()));
        assertThat(user.getName(), equalTo(user1Update.getName()));
    }

    @Test
    void testDeleteUser() throws InputDataException {
        userService.deleteUser(1);

        Mockito.verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    void testCheckUserId() throws InputDataException {
        Mockito.when(userRepository.existsById(anyInt())).thenReturn(true);

        userService.isContainsUser(1);

        Mockito.verify(userRepository, times(1)).existsById(1);
    }

    @Test
    void testCheckUserIdNotExist() {
        Mockito.when(userRepository.existsById(anyInt())).thenReturn(false);

        Exception exception = assertThrows(InputDataException.class, () -> userService.isContainsUser(1));

        assertEquals("Пользователь не найден", exception.getMessage());
    }
}
