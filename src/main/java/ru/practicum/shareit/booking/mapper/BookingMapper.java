package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static CreatedBookingDto toCreatedBookingDto(Booking booking) {
        return new CreatedBookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId()
        );
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(toItem(booking.getItem()))
                .booker(toUser(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(CreatedBookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .item(new Item(bookingDto.getItemId(), null, null, null, null,
                        null, null, null, null))
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(null)
                .status(null)
                .build();
    }

    private static BookingDto.User toUser(User user) {
        return new BookingDto.User(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    private static BookingDto.Item toItem(Item item) {
        return new BookingDto.Item(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }
}
