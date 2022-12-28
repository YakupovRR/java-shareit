package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InputDataException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingRepository bookingRepository;

    @Override
    public Booking createBooking(int userId, Booking booking) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        userService.isContainsUser(userId);
        int itemId = booking.getItem().getId();
        Item item = ItemMapper.fromItemDto(itemService.getItemById(itemId, userId));
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не свободна.");
        }
        if (item.getOwner().getId() == userId) {
            throw new InputDataException("У пользователя нет прав");
        }
        if (booking.getStart().isBefore(currentDateTime) || booking.getEnd().isBefore(currentDateTime)
                || booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("Ошибка во входных данных по дате");
        }

        booking.setBooker(new User(userId, null, null));
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking setApproved(int userId, int bookingId, boolean approved) {
        Booking booking = findBookingById(userId, bookingId);
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Статус у вещи не позволяет её забронировать");
        }
        if (booking.getItem().getOwner().getId() != userId) {
            throw new InputDataException("Пользователь с id=" + userId + "не может меня статус");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking findBookingById(int userId, int bookingId) {
        userService.isContainsUser(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new InputDataException("Бронирование по id не найдено"));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new InputDataException("У пользователя нет прав на бронирование");
        }
        return booking;
    }

    @Override
    public Collection<Booking> findAllByOwnerId(int userId, String state) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        userService.isContainsUser(userId);
        checkBookingState(state);
        Collection<Booking> result = null;

        switch (state) {
            case "ALL":
                result = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                result = bookingRepository.findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore(
                        userId, currentDateTime, currentDateTime);
                break;
            case "PAST":
                result = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId, currentDateTime);
                break;
            case "FUTURE":
                result = bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(
                        userId, currentDateTime);
                break;
            case "WAITING":
                result = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                result = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED);
                break;
        }
        return result;
    }

    @Override
    public Collection<Booking> findAllByBookerId(int userId, String state) {
        userService.isContainsUser(userId);
        checkBookingState(state);
        Collection<Booking> result = null;

        switch (state) {
            case "ALL":
                result = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                result = bookingRepository.findForBookerCurrent(userId);
                break;
            case "PAST":
                result = bookingRepository.findAllByBookerIdAndEndIsBefore(userId, LocalDateTime.now());
                break;
            case "FUTURE":
                result = bookingRepository.findAllByBookerAndFutureState(userId);
                break;
            case "WAITING":
                result = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                result = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED);
                break;
        }
        return result;
    }

    private void checkBookingState(String result) {
        boolean flag = false;

        for (BookingState state : BookingState.values()) {
            if (state.name().equals(result)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            throw new ValidationException("Unknown state: " + result);
        }
    }
}
