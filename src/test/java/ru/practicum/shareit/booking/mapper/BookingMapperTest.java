package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.dto.BookingDtoUser;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingMapperTest {
    private final BookingMapper bookingMapper;

    private final LocalDateTime localDateTime = LocalDateTime.now();
    private final Item item = Item.builder()
            .id(1)
            .name("Сверло")
            .description("Для дрели")
            .available(true)
            .build();
    private final User booker = User.builder()
            .id(1)
            .name("Mike")
            .email("ab@ya.ru")
            .build();
    private final Booking booking = Booking.builder()
            .id(1)
            .start(localDateTime.plusDays(1))
            .end(localDateTime.plusDays(2))
            .status(BookingStatus.WAITING)
            .item(item)
            .booker(booker)
            .build();

    private final CreatedBookingDto createdBookingDtoTest = CreatedBookingDto.builder()
            .id(1)
            .start(localDateTime.plusDays(1))
            .end(localDateTime.plusDays(2))
            .itemId(1)
            .build();

    private final BookingDto bookingDtoTest = BookingDto.builder()
            .id(1)
            .start(localDateTime.plusDays(1))
            .end(localDateTime.plusDays(2))
            .item(new BookingDtoItem(1, "Сверло", "Для дрели", true))
            .booker(new BookingDtoUser(1, "Mike", "ab@ya.ru"))
            .status(BookingStatus.WAITING)
            .build();

    private final Booking bookingTest = Booking.builder()
            .id(1)
            .start(localDateTime.plusDays(1))
            .end(localDateTime.plusDays(2))
            .item(Item.builder().id(1).build())
            .build();

    @Test
    public void testToCreatedBookingDto() {
        CreatedBookingDto createdBookingDto = bookingMapper.toCreatedBookingDto(booking);
        assertEquals(createdBookingDtoTest, createdBookingDto);
    }

    @Test
    public void testToBookingDto() {
        BookingDto bookingDto = bookingMapper.toBookingDto(booking);
        assertEquals(bookingDtoTest.getId(), bookingDto.getId());
        assertEquals(bookingDtoTest.getStart(), bookingDto.getStart());
        assertEquals(bookingDtoTest.getEnd(), bookingDto.getEnd());
    }

    @Test
    void testToBooking() {
        Booking booking = bookingMapper.toBooking(createdBookingDtoTest);
        assertEquals(bookingTest, booking);
    }
}