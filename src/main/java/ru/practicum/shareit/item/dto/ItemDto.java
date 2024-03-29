package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * // TODO .
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {
    private int id;
    @NotBlank
    @NonNull
    private String name;
    @NonNull
    private String description;
    @NonNull
    private Boolean available;
    private User owner;
    private Booking lastBooking;
    private Booking nextBooking;
    private Integer requestId;
    private List<Comment> comments = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class User {
        private int id;
        private String name;
        private String email;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class Booking {
        private int id;
        private int bookerId;
        private LocalDateTime start;
        private LocalDateTime end;
    }


    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Comment {
        private int id;
        private String text;
        private String authorName;
    }
}
