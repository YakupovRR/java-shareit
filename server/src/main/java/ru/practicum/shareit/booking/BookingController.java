package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.InputDataException;
import ru.practicum.shareit.exception.ValidationErrorResponse;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * // TODO .
 */
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private static final String FROM = "0";
    private static final String SIZE = "20";
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @PostMapping
    public CreatedBookingDto createBooking(@RequestHeader(HEADER_USER_ID) int userId,
                                           @Valid @RequestBody CreatedBookingDto bookingDto) {
        log.info("Получен запрос к эндпоинту POST /bookings");
        Booking booking = bookingService.createBooking(userId, bookingMapper.toBooking(bookingDto));
        return bookingMapper.toCreatedBookingDto(booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setApprove(@RequestHeader(HEADER_USER_ID) int userId,
                                 @PathVariable int bookingId, @RequestParam boolean approved) {
        log.info("Получен запрос к эндпоинту PATCH /bookingId");
        return bookingMapper.toBookingDto((bookingService.setApproved(userId, bookingId, approved)));
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@RequestHeader(HEADER_USER_ID) int userId, @PathVariable int bookingId) {
        log.info("Получен запрос к эндпоинту GET /bookingId");
        return bookingMapper.toBookingDto(bookingService.findBookingById(userId, bookingId));
    }

    @GetMapping
    public Collection<BookingDto> findAllByBookerId(@RequestParam(defaultValue = "ALL") String state,
                                                    @RequestParam(defaultValue = FROM) int from,
                                                    @RequestParam(defaultValue = SIZE) int size,
                                                    @RequestHeader(HEADER_USER_ID) int userId) {
        log.info("Получен запрос к эндпоинту GET /booking/" + state);
        return bookingService.findAllByBookerId(userId, state, from, size)
                .stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public Collection<BookingDto> findAllByOwnerId(@RequestHeader(HEADER_USER_ID) int userId,
                                                   @RequestParam(defaultValue = FROM) int from,
                                                   @RequestParam(defaultValue = SIZE) int size,
                                                   @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получен запрос к эндпоинту GET /owner/" + state);
        return bookingService.findAllByOwnerId(userId, state, from, size)
                .stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @ExceptionHandler
    public ResponseEntity<ValidationErrorResponse> handleIncorrectValidation(ValidationException e) {
        log.warn("При обработке запроса возникло исключение: " + e.getMessage());
        return new ResponseEntity<>(new ValidationErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleException(Exception e) {
        log.warn("При обработке запроса возникло исключение " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleNotFoundException(InputDataException e) {
        log.warn("При обработке запроса возникло исключение: " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

}
