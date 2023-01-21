package ru.practicum.shareit.requests.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/**
 * // TODO .
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ItemRequestDto {
    private Integer id;
    private String description;
    private User requester;
    private LocalDateTime created;
    private Collection<Item> items = new ArrayList<>();


    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class User {
        private int id;
        private String name;
        private String email;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Item {
        private int id;
        private String name;
        private String description;
        private Boolean available;
        private int requestId;
    }
}
