package ru.practicum.shareit.requests.mapper;

import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

public class ItemRequestMapper {
    public static ItemRequestDto toItemDto(ItemRequest item) {
        return new ItemRequestDto(
                item.getId(),
                item.getDescription(),
                toUserItemRequest(item.getRequester()),
                item.getCreated()
        );
    }

    public static ItemRequest toItem(ItemRequestDto itemDto) {
        return new ItemRequest(
                itemDto.getId(),
                itemDto.getDescription(),
                toUser(itemDto.getRequester()),
                itemDto.getCreated()
        );
    }

    private static ItemRequestDto.User toUserItemRequest(User user) {
        return new ItemRequestDto.User(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    private static User toUser(ItemRequestDto.User bookingUser) {
        return new User(
                bookingUser.getId(),
                bookingUser.getName(),
                bookingUser.getEmail()
        );
    }
}
