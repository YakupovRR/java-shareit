package ru.practicum.shareit.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ErrorResponse;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";


    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingShortDto bookingShortDto,
                             @RequestHeader(HEADER_USER_ID) Long userId) {
        return bookingService.create(bookingShortDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@PathVariable Long bookingId, @RequestHeader(HEADER_USER_ID) Long userId,
                              @RequestParam Boolean approved) {
        return bookingService.approve(bookingId, userId, approved);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestHeader(HEADER_USER_ID) Long userId,
                                          @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllByOwner(userId, state);
    }

    @GetMapping
    public List<BookingDto> getAllByUser(@RequestHeader(HEADER_USER_ID) Long userId,
                                         @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllByUser(userId, state);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable Long bookingId, @RequestHeader(HEADER_USER_ID) Long userId) {
        return bookingService.getById(bookingId, userId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameterException(BadRequestException e) {
        return new ErrorResponse(e.getMessage());
    }
}
