package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collection;

/**
 * // TODO .
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private int id;
    @NotBlank
    @NonNull
    private String name;
    @NonNull
    private String description;
    @NonNull
    private Boolean available;
    private ItemDtoUser owner;
    private ItemDtoBooking lastBooking;
    private ItemDtoBooking nextBooking;
    private Collection<ItemDtoComment> comments = new ArrayList<>();

}
