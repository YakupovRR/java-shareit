package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

public interface BookingService {

    Booking createBooking(int userId, Booking booking);

    Booking setApproved(int userId, int bookingId, boolean approved);

    Booking findBookingById(int userId, int bookingId);

    Collection<Booking> findAllByBookerId(int userId, String state, int from, int size);

    Collection<Booking> findAllByOwnerId(int userId, String state, int from, int size);

    void checkBookingState(String result);
}
