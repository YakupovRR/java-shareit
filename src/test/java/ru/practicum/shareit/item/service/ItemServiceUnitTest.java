package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InputDataException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.validate.ValidateItemData;
import ru.practicum.shareit.requests.ItemRequestService;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceUnitTest {
    @Mock
    private UserService userService;
    @Mock
    private ItemRequestService itemRequestService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    private ItemService itemService;

    private MockitoSession session;
    @Mock
    private ValidateItemData validateItemData;
    private final ItemMapper itemMapper;

    @BeforeEach
    void setUp() {
        session = Mockito.mockitoSession().initMocks(this).startMocking();
        itemService = new ItemService(itemRepository, userService,  validateItemData,  bookingRepository,
                commentRepository, itemRequestService, itemMapper);
    }

    @AfterEach
    void finish() {
        session.finishMocking();
    }

    private final User user1 = User.builder().id(1).name("User1").email("User1@ya.com").build();
    private final User user2 = User.builder().id(2).name("User2").email("User2@ya.com").build();

    private final ItemRequest mockItemRequest = new ItemRequest(1, "Desc", user2,
            LocalDateTime.now(), null);

    private final Item mockItem1 = Item.builder().id(1).name("Item")
            .description("Description").available(true).owner(user1).request(mockItemRequest).comments(new ArrayList<>()).build();

    private final Item mockItemWithoutName = Item.builder().id(1)
            .description("Description").available(true).owner(user1).build();

    private final Item mockItemWithoutDesc = Item.builder().id(1).name("Item")
            .available(true).owner(user1).build();

    private final Item mockItemWithoutAvailable = Item.builder().id(1).name("Item")
            .description("Description").owner(user1).build();

    private final Item mockUpdatedItem1 = Item.builder().id(1).name("Item")
            .description("Description").available(true).owner(user1).build();

    private final Item mockItem2 = Item.builder().id(1).name("Item2")
            .description("Description2").available(true).owner(user2).build();


    private final Booking mockBooking = Booking.builder()
            .id(1).start(LocalDateTime.now().plusDays(5)).end(LocalDateTime.now().plusDays(2))
            .item(mockItem1).booker(user2).status(BookingStatus.APPROVED).build();

    private final Comment mockComment = Comment.builder()
            .id(1).text("Comment").item(mockItem1).author(user2).created(LocalDateTime.now()).build();


    @Test
    void testCreateItem() throws ValidationException {
        Mockito.when(userService.isContainsUser(anyInt())).thenReturn(true);
        Mockito.when(validateItemData.checkAllData(Mockito.any(Item.class))).thenReturn(true);
        Mockito.when(userService.getUser(anyInt())).thenReturn(user1);
        Mockito.when(itemRepository.save(Mockito.any(Item.class))).thenReturn(mockItem1);

        Item item = itemService.addItem(mockItem1, 1);

        Mockito.verify(itemRepository, Mockito.times(1)).save(mockItem1);

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(mockItem1.getName()));
        assertThat(item.getDescription(), equalTo(mockItem1.getDescription()));
        assertThat(item.getAvailable(), equalTo(mockItem1.getAvailable()));
        assertThat(item.getOwner(), equalTo(mockItem1.getOwner()));
    }

    @Test
    void testCreateItemFailValidationItemWithoutAvailable() throws ValidationException {
        Mockito.when(validateItemData.checkAllData(Mockito.any(Item.class))).thenReturn(false);
        Mockito.when(userService.isContainsUser(anyInt())).thenReturn(true);
        Exception exception3 = assertThrows(ValidationException.class, () ->
                itemService.addItem(mockItemWithoutAvailable, user1.getId()));

        assertEquals("Ошибка во входных данных", exception3.getMessage());
    }

    @Test
    void testCreateItemFailValidationItemWithoutName() throws ValidationException {
        Mockito.when(userService.isContainsUser(anyInt())).thenReturn(true);
        Mockito.when(validateItemData.checkAllData(Mockito.any(Item.class))).thenReturn(false);

        Exception exception = assertThrows(ValidationException.class, () ->
                itemService.addItem(mockItemWithoutName, user1.getId()));

        assertEquals("Ошибка во входных данных", exception.getMessage());
    }

    @Test
    void testCreateItemWithoutDesc() throws ValidationException {
        Mockito.when(userService.isContainsUser(anyInt())).thenReturn(true);
        Mockito.when(validateItemData.checkAllData(Mockito.any(Item.class))).thenReturn(false);

        Exception exception2 = assertThrows(ValidationException.class, () ->
                itemService.addItem(mockItemWithoutDesc, user1.getId()));

        assertEquals("Ошибка во входных данных", exception2.getMessage());
    }

    @Test
    void testFindItemById() throws InputDataException {
        Mockito.when(itemRepository.findById(anyInt())).thenReturn(Optional.of(mockItem1));

        Item item = itemService.getItemById(1, 1);

        Mockito.verify(itemRepository, Mockito.times(1)).findById(1);

        assertThat(item.getId(), equalTo(1));
        assertThat(item.getName(), equalTo(mockItem1.getName()));
        assertThat(item.getDescription(), equalTo(mockItem1.getDescription()));
        assertThat(item.getAvailable(), equalTo(mockItem1.getAvailable()));
        assertThat(item.getOwner(), equalTo(mockItem1.getOwner()));
        assertThat(item.getRequest(), equalTo(mockItemRequest));
    }

    @Test
    void tesFindItemWrongId() {
        Mockito.when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        Exception exception = assertThrows(InputDataException.class, () ->
                itemService.getItemById(1, 1));

        assertEquals("Вещь по id не найдена", exception.getMessage());
    }

    @Test
    void testFindAllByUserId() throws InputDataException {
        Mockito.when(itemRepository.findAllByOwnerId(anyInt(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockItem1, mockItem2));

        Collection<Item> items = itemService.getItemsByUserId(1, 0, 20);

        Mockito.verify(itemRepository, Mockito.times(1))
                .findAllByOwnerId(anyInt(), Mockito.any(Pageable.class));

        assertThat(items, hasSize(2));
        assertThat(items, equalTo(List.of(mockItem1, mockItem2)));
    }

    @Test
    void testUpdateItem() throws InputDataException {
        Mockito.when(itemRepository.save(Mockito.any(Item.class))).thenReturn(mockUpdatedItem1);
        Mockito.when(itemRepository.findById(anyInt())).thenReturn(Optional.of(mockItem1));

        mockItem1.setName("Item1Update");

        Item item = itemService.updateItem(itemMapper.toItemDto(mockItem1), 1);

        Mockito.verify(itemRepository, Mockito.times(1)).save(mockItem1);

        assertThat(item.getId(), equalTo(mockUpdatedItem1.getId()));
        assertThat(item.getName(), equalTo(mockUpdatedItem1.getName()));
    }

    @Test
    void testUpdateItemWrongUser() {
        Mockito.when(itemRepository.findById(anyInt())).thenReturn(Optional.of(mockItem1));

        mockItem1.setName("Item1Update");
        ItemDto itemDto = itemMapper.toItemDto(mockItem1);

        Exception exception = assertThrows(InputDataException.class, () ->
                itemService.updateItem(itemDto, 2));

        assertEquals("Id пользователя не совпадает с id создавшего вещь пользователя", exception.getMessage());
    }

    @Test
    void testDeleteItem() throws InputDataException {
        Mockito.when(itemRepository.existsById(anyInt())).thenReturn(true);

        itemService.deleteItem(1);

        Mockito.verify(itemRepository, Mockito.times(1)).deleteById(1);
    }

    @Test
    void testSearchItemByText() {
        Mockito.when(itemRepository.search(Mockito.any(String.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockItem2));

        Collection<Item> items = itemService.getItemsBySubString("Desc2", 0, 20);

        Mockito.verify(itemRepository, Mockito.times(1))
                .search(Mockito.any(String.class), Mockito.any(Pageable.class));

        assertThat(items, hasSize(1));
        assertThat(items, equalTo(List.of(mockItem2)));
    }

    @Test
    void testCreateComment() throws ValidationException {
        Mockito.when(userService.getUser(anyInt()))
                .thenReturn(user2);
        Mockito.when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(mockItem1));
        Mockito.when(bookingRepository.findFirstByBookerIdAndItemIdAndStatusAndStartBefore(anyInt(),
                        anyInt(), Mockito.any(BookingStatus.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(Optional.of(mockBooking));
        Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(mockComment);

        Comment comment = itemService.addComment(2, mockItem1.getId(), mockComment);

        Mockito.verify(commentRepository, Mockito.times(1)).save(mockComment);

        assertThat(comment.getId(), notNullValue());
        assertThat(comment.getText(), equalTo(mockComment.getText()));
        assertThat(comment.getItem(), equalTo(mockComment.getItem()));
        assertThat(comment.getAuthor(), equalTo(mockComment.getAuthor()));
    }

    @Test
    void testCreateComment_ValidationFailed() throws InputDataException {
        Mockito.when(userService.getUser(anyInt()))
                .thenReturn(user2);
        Mockito.when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(mockItem1));
        Mockito.when(bookingRepository.findFirstByBookerIdAndItemIdAndStatusAndStartBefore(anyInt(),
                        anyInt(), Mockito.any(BookingStatus.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        Exception exception1 = assertThrows(ValidationException.class, () ->
                itemService.addComment(user1.getId(), mockItem1.getId(), mockComment));

        assertEquals("Ошибка во входных данных", exception1.getMessage());
    }
}
