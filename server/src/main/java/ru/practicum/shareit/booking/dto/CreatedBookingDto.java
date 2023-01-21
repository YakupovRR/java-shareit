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
    private BookingStatus status;
    private User booker;
    private Item item;
    private String itemName;
    private int itemId;

    public CreatedBookingDto(int id, @NonNull LocalDateTime start, @NonNull LocalDateTime end, BookingStatus status,
                             User booker, Item item, String itemName) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.status = status;
        this.booker = booker;
        this.item = item;
        this.itemName = itemName;
    }
}
