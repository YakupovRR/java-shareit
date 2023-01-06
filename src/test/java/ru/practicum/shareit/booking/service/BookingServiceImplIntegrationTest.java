package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.InputDataException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.time.LocalDate;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceImplIntegrationTest {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final UserMapper userMapper;

    private final User mockOwner = User.builder()
            .id(1)
            .name("Owner1")
            .email("Owne1r@ya.com")
            .build();

    private final User mockBooker = User.builder()
            .id(2)
            .name("Booker1")
            .email("Booker@ya.com")
            .build();

    private final Item mockItem = Item.builder()
            .id(1)
            .name("Item")
            .description("Description")
            .available(true)
            .owner(mockOwner)
            .build();

    private final Booking mockBooking1 = Booking.builder()
            .id(1)
            .start(LocalDate.now().atStartOfDay().plusDays(1))
            .end(LocalDate.now().atStartOfDay().plusDays(2))
            .item(mockItem).booker(mockBooker)
            .status(BookingStatus.WAITING)
            .build();

    private final Booking mockBooking2 = Booking.builder()
            .id(2)
            .start(LocalDate.now().atStartOfDay().plusDays(2))
            .end(LocalDate.now().atStartOfDay().plusDays(4))
            .item(mockItem)
            .booker(mockBooker)
            .status(BookingStatus.WAITING)
            .build();

    @Test
    void testFindAllByBookerId() {
        userService.addUser(userMapper.toUserDto(mockOwner));
        userService.addUser(userMapper.toUserDto(mockBooker));
        itemService.addItem(mockItem, mockOwner.getId());
        bookingService.createBooking(mockBooker.getId(), mockBooking1);
        bookingService.createBooking(mockBooker.getId(), mockBooking2);

        Collection<Booking> bookings = bookingService.findAllByBookerId(mockBooker.getId(),
                BookingState.WAITING.toString(), 0, 20);

        assertThat(bookings, hasSize(2));
        assertThat(bookings.stream().findFirst().isPresent(), is(true));
        assertThat(bookings.stream().findFirst().get().getId(), equalTo(mockBooking2.getId()));
        assertThat(bookings.stream().findFirst().get().getStart(), equalTo(mockBooking2.getStart()));
        assertThat(bookings.stream().findFirst().get().getEnd(), equalTo(mockBooking2.getEnd()));
    }

    @Test
    void testFindAllByBookerIdWrongUser() {
        Exception exception = assertThrows(InputDataException.class, () ->
                bookingService.findAllByBookerId(mockBooker.getId(), BookingState.WAITING.toString(), 0, 20));

        assertEquals("Пользователь не найден", exception.getMessage());
    }
}