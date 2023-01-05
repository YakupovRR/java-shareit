package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

/**
 * // TODO .
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookingDto {
    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingDtoItem item;
    private BookingDtoUser booker;
    private BookingStatus status;
}
