package ru.practicum.shareit.requests.service;

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
import ru.practicum.shareit.exception.InputDataException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.requests.ItemRequestService;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
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
public class ItemRequestServiceUnitTest {
    @Mock
    private UserService userService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    private ItemRequestService itemRequestService;

    private MockitoSession mockitoSession;

    private ItemRequestMapper itemRequestMapper;

    @BeforeEach
    void setUp() {
        mockitoSession = Mockito.mockitoSession().initMocks(this).startMocking();
        itemRequestService = new ItemRequestService(itemRequestRepository, userService, itemRequestMapper);
        itemRequestMapper = new ItemRequestMapper();
    }

    @AfterEach
    void finish() {
        mockitoSession.finishMocking();
    }

    private final User user1 = User.builder().id(1).name("User1").email("user1@ya.ru").build();
    private final User user2 = User.builder().id(2).name("User2").email("user2@ya.ru").build();
    private final ItemRequest mockItemRequest1 = ItemRequest.builder().id(1).description("RequestDescription1")
            .requester(user1).created(LocalDateTime.now()).build();
    private final ItemRequest mockItemRequest2 = ItemRequest.builder().id(2).description("RequestDescription2")
            .requester(user2).created(LocalDateTime.now().plusDays(1)).build();
    private final ItemRequest mockItemRequestWithoutDescAndUSer = ItemRequest.builder().id(2)
            .created(LocalDateTime.now().plusDays(1)).build();
    private final ItemRequest mockItemRequest3 = ItemRequest.builder().id(3).description("RequestDescription3")
            .requester(user1).created(LocalDateTime.now()).build();


    @Test
    void testAddItemRequest() throws ValidationException {
        Mockito.when(userService.getUser(anyInt())).thenReturn(user2);
        Mockito.when(itemRequestRepository.save(Mockito.any(ItemRequest.class))).thenReturn(mockItemRequest1);

        ItemRequest itemRequest = itemRequestService.createRequest(2, mockItemRequest1);

        Mockito.verify(itemRequestRepository, Mockito.times(1)).save(mockItemRequest1);

        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(mockItemRequest1.getDescription()));
        assertThat(itemRequest.getCreated(), equalTo(mockItemRequest1.getCreated()));
    }

    @Test
    void testCreateItemRequestWithoutDesc() throws InputDataException {
        Mockito.lenient().when(userService.getUser(anyInt())).thenReturn(user2);

        Exception exception = assertThrows(ValidationException.class, () ->
                itemRequestService.createRequest(user1.getId(),
                        mockItemRequestWithoutDescAndUSer));

        assertEquals("Отсутствует описание запрашиваемой вещи", exception.getMessage());
    }

    @Test
    void testFindItemRequestsByUser() throws InputDataException {
        Mockito.when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(anyInt()))
                .thenReturn(List.of(mockItemRequest1, mockItemRequest3));

        Collection<ItemRequest> itemRequests = itemRequestService.getAllRequestByUserId(1);

        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findAllByRequesterIdOrderByCreatedDesc(1);

        assertThat(itemRequests, hasSize(2));
        assertThat(itemRequests, equalTo(List.of(mockItemRequest1, mockItemRequest3)));
    }

    @Test
    void testFindAllItemRequest() throws InputDataException, ValidationException {
        Mockito.when(itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(anyInt(),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockItemRequest1, mockItemRequest2, mockItemRequest3));

        Collection<ItemRequest> itemRequests = itemRequestService.getAllRequest(1, 0, 20);

        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findAllByRequesterIdNotOrderByCreatedDesc(anyInt(), Mockito.any(Pageable.class));

        assertThat(itemRequests, hasSize(3));
        assertThat(itemRequests, equalTo(List.of(mockItemRequest1, mockItemRequest2, mockItemRequest3)));
    }

    @Test
    void testGetAllItemRequestFailedValidationWithInvalidParameter() {
        Exception exception = assertThrows(ValidationException.class, () ->
                itemRequestService.getAllRequest(user1.getId(), -1, 20));

        assertEquals("Ошибка во входных данных страницы", exception.getMessage());
    }

    @Test
    void testFindItemRequestById() throws InputDataException {
        Mockito.when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.of(mockItemRequest1));

        ItemRequest itemRequests = itemRequestService.getRequestById(1, 1);

        Mockito.verify(itemRequestRepository, Mockito.times(1)).findById(1);

        assertThat(itemRequests.getId(), equalTo(1));
        assertThat(itemRequests.getDescription(), equalTo(mockItemRequest1.getDescription()));
        assertThat(itemRequests.getCreated(), equalTo(mockItemRequest1.getCreated()));
    }

    @Test
    void testFindItemRequest_WrongId() {
        Mockito.when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.empty());

        Exception exception = assertThrows(InputDataException.class, () ->
                itemRequestService.getRequestById(1, 1));

        assertEquals("Не найден запрос по id = 1", exception.getMessage());
    }

    @Test
    void testCheckItemRequestExistsById() throws InputDataException {
        Mockito.when(itemRequestRepository.existsById(anyInt())).thenReturn(true);

        itemRequestService.checkItemRequestExistsById(1);

        Mockito.verify(itemRequestRepository, Mockito.times(1)).existsById(1);
    }

    @Test
    void testCheckItemRequestNotExistsById() {
        Mockito.when(itemRequestRepository.existsById(anyInt())).thenReturn(false);

        Exception exception = assertThrows(InputDataException.class, () ->
                itemRequestService.checkItemRequestExistsById(1));

        assertEquals("Запрос вещи по id = 1 не найден в базе данных", exception.getMessage());
    }
}
