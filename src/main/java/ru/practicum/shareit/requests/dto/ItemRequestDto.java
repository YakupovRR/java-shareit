package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * // TODO .
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemRequestDto {
    private int id;
    private String description;
    private User requester;
    private LocalDateTime created;


    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class User {
        private int id;
        private String name;
        private String email;
    }
}
