package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemDtoBooking {
    private int id;
    private int bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}
