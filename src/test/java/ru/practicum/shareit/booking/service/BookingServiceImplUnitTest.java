package ru.practicum.shareit.booking.service;

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
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InputDataException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
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
public class BookingServiceImplUnitTest {
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    @Mock
    private BookingRepository bookingRepository;
    private BookingService bookingService;
    private MockitoSession mockitoSession;
    private final ItemMapper itemMapper;

    @BeforeEach
    void setUp() {
        mockitoSession = Mockito.mockitoSession().initMocks(this).startMocking();
        bookingService = new BookingServiceImpl(itemService, userService, bookingRepository, itemMapper);
    }

    @AfterEach
    void finish() {
        mockitoSession.finishMocking();
    }

    private final User mockUser1 = User.builder().id(1).name("User1").email("User1@ya.ru").build();
    private final User mockUser2 = User.builder().id(2).name("User2").email("User2@ya.ru").build();
    private final Item mockItem1 = Item.builder().id(1).name("Item")
            .description("ItemDescription").available(true).owner(mockUser1).build();
    private final Item mockItemUnAvailable = Item.builder().id(1).name("Item")
            .description("ItemDescription").available(false).owner(mockUser1).build();
    private final Booking mockBooking1 = Booking.builder().id(1)
            .start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2))
            .item(mockItem1).booker(mockUser2).status(BookingStatus.WAITING).build();
    private final Booking mockBookingUnAvailable = Booking.builder()
            .id(1).start(LocalDateTime.now().plusDays(3)).end(LocalDateTime.now().plusDays(4))
            .item(mockItemUnAvailable).booker(mockUser2).status(BookingStatus.WAITING).build();
    private final Booking mockBookingEndFromLast = Booking.builder()
            .id(1).start(LocalDateTime.now().plusDays(5)).end(LocalDateTime.now().minusDays(6))
            .item(mockItem1).booker(mockUser2).status(BookingStatus.WAITING).build();
    private final Booking mockBookingStartFromLast = Booking.builder()
            .id(1).start(LocalDateTime.now().minusDays(2)).end(LocalDateTime.now().plusDays(6))
            .item(mockItem1).booker(mockUser2).status(BookingStatus.WAITING).build();
    private final Booking mockBookingStartAfterEnd = Booking.builder()
            .id(1).start(LocalDateTime.now().plusDays(5)).end(LocalDateTime.now().plusDays(2))
            .item(mockItem1).booker(mockUser2).status(BookingStatus.WAITING).build();
    private final Booking mockBookingWrongUser = Booking.builder()
            .id(1).start(LocalDateTime.now().plusDays(5)).end(LocalDateTime.now().plusDays(10))
            .item(mockItem1).booker(mockUser1).status(BookingStatus.WAITING).build();
    private final Booking mockBookingApproved1 = Booking.builder()
            .id(1).start(LocalDateTime.now().plusDays(5)).end(LocalDateTime.now().plusDays(10))
            .item(mockItem1).booker(mockUser2).status(BookingStatus.APPROVED).build();
    private final Booking mockBookingRejected1 = Booking.builder()
            .id(1).start(LocalDateTime.now().plusDays(5)).end(LocalDateTime.now().plusDays(10))
            .item(mockItem1).booker(mockUser2).status(BookingStatus.REJECTED).build();
    private final Booking mockBooking2 = Booking.builder()
            .id(1).start(LocalDateTime.now().plusDays(5)).end(LocalDateTime.now().plusDays(10))
            .item(mockItem1).booker(mockUser2).status(BookingStatus.WAITING).build();
    private final Booking mockBooking3 = Booking.builder()
            .id(5).start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2))
            .item(mockItem1).booker(mockUser2).status(BookingStatus.WAITING).build();

    @Test
    void testCreateBooking() throws ValidationException {
        Mockito.when(itemService.getItemById(anyInt(), anyInt())).thenReturn(mockItem1);
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(mockBooking1);
        Booking booking = bookingService.createBooking(2, mockBooking1);
        Mockito.verify(bookingRepository, Mockito.times(1)).save(mockBooking1);
        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(mockBooking1.getStart()));
        assertThat(booking.getEnd(), equalTo(mockBooking1.getEnd()));
    }

    @Test
    void testCreateBookingFailedValidationBookingStartFromLast() throws ValidationException {
        Mockito.when(itemService.getItemById(anyInt(), anyInt())).thenReturn(mockItem1);

        Exception exception = assertThrows(ValidationException.class, () ->
                bookingService.createBooking(2, mockBookingStartFromLast));

        assertEquals("Ошибка во входных данных по дате", exception.getMessage());
    }

    @Test
    void testCreateBookingFailedValidationBookingEndFromLast() throws ValidationException {
        Mockito.when(itemService.getItemById(anyInt(), anyInt())).thenReturn(mockItem1);

        Exception exception = assertThrows(ValidationException.class, () ->
                bookingService.createBooking(2, mockBookingEndFromLast));

        assertEquals("Ошибка во входных данных по дате", exception.getMessage());
    }

    @Test
    void testCreateBookingFailedValidationBookingStartAfterEnd() throws ValidationException {
        Mockito.when(itemService.getItemById(anyInt(), anyInt())).thenReturn(mockItem1);

        Exception exception = assertThrows(ValidationException.class, () ->
                bookingService.createBooking(2, mockBookingStartAfterEnd));

        assertEquals("Ошибка во входных данных по дате", exception.getMessage());
    }

    @Test
    void testCreateBookingFailedValidationBookingWrongUser() throws ValidationException {
        Mockito.when(itemService.getItemById(anyInt(), anyInt())).thenReturn(mockItem1);

        Exception exception = assertThrows(InputDataException.class, () ->
                bookingService.createBooking(1, mockBookingWrongUser));

        assertEquals("У пользователя нет прав", exception.getMessage());
    }

    @Test
    void testCreateBookingFailedValidationItem() throws ValidationException {
        Mockito.when(itemService.getItemById(anyInt(), anyInt())).thenReturn(mockItemUnAvailable);

        Exception exception1 = assertThrows(ValidationException.class, () ->
                bookingService.createBooking(2, mockBookingUnAvailable));

        assertEquals("Вещь не свободна.", exception1.getMessage());
    }

    @Test
    void testSetApproved() throws ValidationException {
        Mockito.when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(mockBooking1));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(mockBookingApproved1);

        Booking booking = bookingService.setApproved(1, mockBooking1.getId(), true);

        Mockito.verify(bookingRepository, Mockito.times(1)).save(mockBooking1);

        assertThat(booking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void testSetApprovedStatusNotWaiting() {
        Mockito.when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(mockBookingApproved1));

        Exception exception = assertThrows(ValidationException.class, () ->
                bookingService.setApproved(1, mockBookingApproved1.getId(), true));

        assertEquals("Статус у вещи не позволяет её забронировать", exception.getMessage());
    }

    @Test
    void testSetApprovedWrongUser() {
        Mockito.when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(mockBooking1));

        Exception exception = assertThrows(InputDataException.class, () ->
                bookingService.setApproved(2, mockBooking1.getId(), true));

        assertEquals("Пользователь не может меня статус", exception.getMessage());
    }

    @Test
    void testSetNotApproved() throws ValidationException {
        Mockito.when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(mockBooking2));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(mockBookingRejected1);

        Booking booking = bookingService.setApproved(1, mockBooking2.getId(), false);

        Mockito.verify(bookingRepository, Mockito.times(1)).save(mockBooking2);

        assertThat(booking.getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void testFindBookingById() throws InputDataException, ValidationException {
        Mockito.when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(mockBooking1));

        Booking booking = bookingService.findBookingById(1, 1);

        Mockito.verify(bookingRepository, Mockito.times(1)).findById(1);

        assertThat(booking.getId(), equalTo(mockBooking1.getId()));
        assertThat(booking.getStatus(), equalTo(mockBooking1.getStatus()));
        assertThat(booking.getStart(), equalTo(mockBooking1.getStart()));
        assertThat(booking.getEnd(), equalTo(mockBooking1.getEnd()));
        assertThat(booking.getItem(), equalTo(mockBooking1.getItem()));
    }

    @Test
    void testFindBooking_WrongId() {
        Mockito.when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());

        Exception exception = assertThrows(InputDataException.class, () ->
                bookingService.findBookingById(1, 1));

        assertEquals("Бронирование по id не найдено", exception.getMessage());
    }

    @Test
    void testFindBookingByIdWrongUser() {
        Mockito.when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(mockBooking1));

        Exception exception = assertThrows(InputDataException.class, () ->
                bookingService.findBookingById(3, 1));

        assertEquals("У пользователя нет прав на бронирование", exception.getMessage());
    }

    @Test
    void testFindAllByBookerIdStateAll() throws InputDataException, ValidationException {
        Mockito.when(bookingRepository.findAllByBookerId(anyInt(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking1, mockBooking2, mockBooking3));

        Collection<Booking> bookings = bookingService.findAllByBookerId(1, BookingState.ALL.toString(), 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerId(anyInt(), Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(3));
        assertThat(bookings, equalTo(List.of(mockBooking1, mockBooking2, mockBooking3)));
    }

    @Test
    void testFindAllByBookerIdStateCurrent() throws InputDataException, ValidationException {
        Mockito.when(bookingRepository.findForBookerCurrent(anyInt(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking2, mockBooking3));

        Collection<Booking> bookings = bookingService.findAllByBookerId(1,
                BookingState.CURRENT.toString(), 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findForBookerCurrent(anyInt(), Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(2));
        assertThat(bookings, equalTo(List.of(mockBooking2, mockBooking3)));
    }

    @Test
    void testFindAllByBookerIdStatePast() throws InputDataException, ValidationException {
        Mockito.when(bookingRepository.findAllByBookerIdAndEndIsBefore(anyInt(),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking1, mockBooking2));

        Collection<Booking> bookings = bookingService.findAllByBookerId(1, BookingState.PAST.toString(), 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerIdAndEndIsBefore(anyInt(), Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(2));
        assertThat(bookings, equalTo(List.of(mockBooking1, mockBooking2)));
    }

    @Test
    void testFindAllByBookerIdStateFuture() throws InputDataException, ValidationException {
        Mockito.when(bookingRepository.findAllByBookerAndFutureState(anyInt(),
                        Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking1, mockBooking3));

        Collection<Booking> bookings = bookingService.findAllByBookerId(1, BookingState.FUTURE.toString(), 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerAndFutureState(anyInt(),
                        Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(2));
        assertThat(bookings, equalTo(List.of(mockBooking1, mockBooking3)));
    }

    @Test
    void testFindAllByBookerIdStateWaiting() throws InputDataException, ValidationException {
        Mockito.when(bookingRepository.findAllByBookerIdAndStatus(anyInt(),
                        Mockito.any(BookingStatus.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking3));

        Collection<Booking> bookings = bookingService.findAllByBookerId(1,
                BookingState.WAITING.toString(), 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerIdAndStatus(anyInt(), Mockito.any(BookingStatus.class),
                        Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(1));
        assertThat(bookings, equalTo(List.of(mockBooking3)));
    }

    @Test
    void testFindAllByBookerIdStateRejected() throws InputDataException, ValidationException {
        Mockito.when(bookingRepository.findAllByBookerIdAndStatus(anyInt(),
                        Mockito.any(BookingStatus.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking2));

        Collection<Booking> bookings = bookingService.findAllByBookerId(1,
                BookingState.REJECTED.toString(), 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByBookerIdAndStatus(anyInt(), Mockito.any(BookingStatus.class),
                        Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(1));
        assertThat(bookings, equalTo(List.of(mockBooking2)));
    }

    @Test
    void testFindAllByOwnerIdStateAll() throws InputDataException, ValidationException {
        Mockito.when(bookingRepository.findAllByItemOwnerId(anyInt(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking1, mockBooking2, mockBooking3));

        Collection<Booking> bookings = bookingService.findAllByOwnerId(1, BookingState.ALL.toString(), 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerId(anyInt(), Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(3));
        assertThat(bookings, equalTo(List.of(mockBooking1, mockBooking2, mockBooking3)));
    }

    @Test
    void testFindAllByOwnerIdStateCurrent() throws InputDataException, ValidationException {
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore(anyInt(),
                        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking2, mockBooking3));

        Collection<Booking> bookings = bookingService.findAllByOwnerId(1, BookingState.CURRENT.toString(), 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore(anyInt(),
                        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(2));
        assertThat(bookings, equalTo(List.of(mockBooking2, mockBooking3)));
    }

    @Test
    void testGetAllByOwnerIdStatePast() throws InputDataException, ValidationException {
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndEndIsBefore(anyInt(),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking1, mockBooking2));

        Collection<Booking> bookings = bookingService.findAllByOwnerId(1, BookingState.PAST.toString(), 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerIdAndEndIsBefore(anyInt(), Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(2));
        assertThat(bookings, equalTo(List.of(mockBooking1, mockBooking2)));
    }

    @Test
    void testGetAllByOwnerIdStateFuture() throws InputDataException, ValidationException {
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStartIsAfter(anyInt(),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking1, mockBooking3));

        Collection<Booking> bookings = bookingService.findAllByOwnerId(1, BookingState.FUTURE.toString(), 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerIdAndStartIsAfter(anyInt(), Mockito.any(LocalDateTime.class),
                        Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(2));
        assertThat(bookings, equalTo(List.of(mockBooking1, mockBooking3)));
    }

    @Test
    void testGetAllByOwnerIdStateWaiting() throws InputDataException, ValidationException {
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStatus(anyInt(),
                        Mockito.any(BookingStatus.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking3));

        Collection<Booking> bookings = bookingService.findAllByOwnerId(1, BookingState.WAITING.toString(), 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerIdAndStatus(anyInt(), Mockito.any(BookingStatus.class),
                        Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(1));
        assertThat(bookings, equalTo(List.of(mockBooking3)));
    }

    @Test
    void testGetAllByOwnerIdStateRejected() throws InputDataException, ValidationException {
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStatus(anyInt(),
                        Mockito.any(BookingStatus.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(mockBooking2));

        Collection<Booking> bookings = bookingService.findAllByOwnerId(1, BookingState.REJECTED.toString(), 0, 20);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemOwnerIdAndStatus(anyInt(), Mockito.any(BookingStatus.class),
                        Mockito.any(Pageable.class));

        assertThat(bookings, hasSize(1));
        assertThat(bookings, equalTo(List.of(mockBooking2)));
    }
}
