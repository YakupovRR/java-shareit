package ru.practicum.shareit.requests;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * // TODO .
 */
@Data
public class ItemRequest {
    private int id;
    private String description;
    private int requestorId;
    private LocalDateTime created;
}
