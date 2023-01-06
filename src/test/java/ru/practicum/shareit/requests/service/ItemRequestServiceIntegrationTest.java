package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.InputDataException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.requests.ItemRequestService;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestServiceIntegrationTest {
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private final UserMapper userMapper;
    private final ItemRequestMapper itemRequestMapper;

    private final User mockUser = User.builder()
            .id(1).name("User1")
            .email("User1@ya.ru")
            .build();

    private final ItemRequest mockItemRequest = ItemRequest.builder()
            .id(1)
            .description("RequestDescription")
            .created(LocalDate.now().atStartOfDay())
            .items(new ArrayList<>())
            .build();

    @Test
    void testFindItemRequestById() throws ValidationException {
        userService.addUser(userMapper.toUserDto(mockUser));
        mockItemRequest.setRequester(mockUser);
        itemRequestService.createRequest(mockUser.getId(), mockItemRequest);

        ItemRequest itemRequest = itemRequestService.getRequestById(mockUser.getId(), mockItemRequest.getId());

        assertThat(itemRequest.getId(), equalTo(mockItemRequest.getId()));
        assertThat(itemRequest.getDescription(), equalTo(mockItemRequest.getDescription()));
    }


    @Test
    void testFindItemRequestWrongId() {
        userService.addUser(userMapper.toUserDto(mockUser));

        Exception exception = assertThrows(InputDataException.class, () ->
                itemRequestService.getRequestById(mockUser.getId(), mockItemRequest.getId()));

        assertEquals("Не найден запрос по id = 1", exception.getMessage());
    }
}