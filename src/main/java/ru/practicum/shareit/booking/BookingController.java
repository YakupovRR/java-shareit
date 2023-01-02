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
import ru.practicum.shareit.exception.InputExistDataException;
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

    private final BookingService bookingService;

    @PostMapping
    public CreatedBookingDto createBooking(@RequestHeader(HEADER_USER_ID) int userId,
                                           @Valid @RequestBody CreatedBookingDto bookingDto) {
        log.info("Получен запрос к эндпоинту POST /bookings");
        Booking booking = bookingService.createBooking(userId, BookingMapper.toBooking(bookingDto));
        return BookingMapper.toCreatedBookingDto(booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setApprove(@RequestHeader(HEADER_USER_ID) int userId,
                                 @PathVariable int bookingId, @RequestParam boolean approved) {
        log.info("Получен запрос к эндпоинту PATCH /bookingId");
        return BookingMapper.toBookingDto((bookingService.setApproved(userId, bookingId, approved)));
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@RequestHeader(HEADER_USER_ID) int userId, @PathVariable int bookingId) {
        log.info("Получен запрос к эндпоинту GET /bookingId");
        return BookingMapper.toBookingDto(bookingService.findBookingById(userId, bookingId));
    }

    @GetMapping
    public Collection<BookingDto> findAllByBookerId(@RequestParam(defaultValue = "ALL") String state,
                                                    @RequestHeader(HEADER_USER_ID) int userId) {
        log.info("Получен запрос к эндпоинту GET /booking/" + state);
        return bookingService.findAllByBookerId(userId, state)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public Collection<BookingDto> findAllByOwnerId(@RequestHeader(HEADER_USER_ID) int userId,
                                                   @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получен запрос к эндпоинту GET /owner/" + state);
        return bookingService.findAllByOwnerId(userId, state)
                .stream()
                .map(BookingMapper::toBookingDto)
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

    @ExceptionHandler
    public ResponseEntity<String> handleConflictDataException(InputExistDataException e) {
        log.warn("При обработке запроса возникло исключение: " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

}
