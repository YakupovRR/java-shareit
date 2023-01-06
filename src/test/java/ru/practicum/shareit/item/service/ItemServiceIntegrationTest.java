package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceIntegrationTest {
    private final UserService userService;
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    private final User userMock1 = User.builder()
            .id(1)
            .name("User1")
            .email("User1@ya.ru")
            .build();

    private final User userMockFirst = User.builder()
            .id(1)
            .name("User1")
            .email("User1@ya.ru")
            .build();

    private final Item mockItem1 = Item.builder()
            .id(1)
            .name("Item")
            .description("Description")
            .available(true)
            .owner(userMockFirst)
            .build();

    @Test
    void testSearchItemByText() throws ValidationException {
        userService.addUser(userMapper.toUserDto(userMock1));
        itemService.addItem(mockItem1, userMock1.getId());

        Collection<Item> items = itemService.getItemsBySubString("Description", 0, 20);

        assertThat(items, hasSize(1));
        assertThat(items.stream().findFirst().isPresent(), is(true));
        assertThat(items.stream().findFirst().get().getId(), equalTo(mockItem1.getId()));
        assertThat(items.stream().findFirst().get().getName(), equalTo(mockItem1.getName()));
        assertThat(items.stream().findFirst().get().getDescription(), equalTo(mockItem1.getDescription()));
    }

    @Test
    void testSearchItemByEmptyText() throws ValidationException {
        userService.addUser(userMapper.toUserDto(userMock1));
        itemService.addItem(mockItem1, userMock1.getId());

        Collection<Item> items = itemService.getItemsBySubString("", 0, 20);

        assertThat(items, hasSize(0));
        assertThat(items, empty());
    }
}