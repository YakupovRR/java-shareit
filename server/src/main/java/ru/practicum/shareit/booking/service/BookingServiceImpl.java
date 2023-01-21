package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import ru.practicum.shareit.trait.PageTool;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService, PageTool {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;

    @Override
    public Booking createBooking(int userId, Booking booking) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        userService.isContainsUser(userId);
        int itemId = booking.getItem().getId();
        Item item = itemService.getItemById(itemId, userId);
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
        System.out.println(bookingRepository.findFirstByBookerIdAndItemIdAndStatusAndStartBefore(userId, itemId, BookingStatus.APPROVED,
                LocalDateTime.now())); //удалить
        return bookingRepository.save(booking);
    }

    @Override
    public Booking setApproved(int userId, int bookingId, boolean approved) {
        Booking booking = findBookingById(userId, bookingId);
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Статус у вещи не позволяет её забронировать");
        }
        if (booking.getItem().getOwner().getId() != userId) {
            throw new InputDataException("Пользователь не может меня статус");
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
    public Collection<Booking> findAllByOwnerId(int userId, String state, int from, int size) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        userService.isContainsUser(userId);
        checkBookingState(state);
        if (from < 0) {
            throw new ValidationException("Размер страницы не соответствует исходным данным");
        }
        Pageable page = getPage(from, size, "start", Sort.Direction.DESC);

        Collection<Booking> result = null;
        switch (state) {
            case "ALL":
                result = bookingRepository.findAllByItemOwnerId(userId, page);
                break;
            case "CURRENT":
                result = bookingRepository.findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore(userId, currentDateTime,
                        currentDateTime, page);
                break;
            case "PAST":
                result = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId, currentDateTime, page);
                break;
            case "FUTURE":
                result = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(userId, currentDateTime, page);
                break;
            case "WAITING":
                result = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, page);
                break;
            case "REJECTED":
                result = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, page);
                break;
        }
        return result;
    }

    @Override
    public Collection<Booking> findAllByBookerId(int userId, String state, int from, int size) {
        userService.isContainsUser(userId);
        checkBookingState(state);
        Collection<Booking> result = null;
        if (from < 0) {
            throw new ValidationException("Размер страницы не соответствует исходным данным");
        }
        Pageable page = getPage(from, size, "start", Sort.Direction.DESC);

        switch (state) {
            case "ALL":
                result = bookingRepository.findAllByBookerId(userId, page);
                break;
            case "CURRENT":
                result = bookingRepository.findForBookerCurrent(userId, page);
                break;
            case "PAST":
                result = bookingRepository.findAllByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), page);
                break;
            case "FUTURE":
                result = bookingRepository.findAllByBookerAndFutureState(userId, page);
                break;
            case "WAITING":
                result = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, page);
                break;
            case "REJECTED":
                result = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, page);
                break;
        }
        return result;
    }

    @Override
    public void checkBookingState(String result) {
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
