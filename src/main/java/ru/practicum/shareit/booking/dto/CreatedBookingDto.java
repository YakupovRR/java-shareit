package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatedBookingDto {
    private int id;
    @NonNull
    private LocalDateTime start;
    @NonNull
    private LocalDateTime end;
    @NonNull
    private int itemId;
    @NonNull
    private BookingStatus status;
    @NonNull
    private Item item;
    @NonNull
    private User booker;

}
