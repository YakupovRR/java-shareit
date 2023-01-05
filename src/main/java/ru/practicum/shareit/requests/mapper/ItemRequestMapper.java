package ru.practicum.shareit.requests.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ItemRequestMapper {
    public ItemRequestDto toItemRequestDto(ItemRequest item) {
        return ItemRequestDto.builder()
                .id(item.getId())
                .description(item.getDescription())
                .requester(toUserItemRequest(item.getRequester()))
                .created(item.getCreated())
                .items(item.getItems().stream().map(this::toItemItemRequest).collect(Collectors.toList()))
                .build();
    }

    public ItemRequest toItemRequest(ItemRequestDto itemDto) {
        return ItemRequest.builder()
                .id(itemDto.getId())
                .description(itemDto.getDescription())
                .requester(Optional.ofNullable(itemDto.getRequester()).map(this::toUser).orElse(null))
                .created(itemDto.getCreated())
                .items(itemDto.getItems().stream().map(this::toItem).collect(Collectors.toList()))
                .build();
    }

    private ItemRequestDto.User toUserItemRequest(User user) {
        return new ItemRequestDto.User(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    private User toUser(ItemRequestDto.User bookingUser) {
        return new User(
                bookingUser.getId(),
                bookingUser.getName(),
                bookingUser.getEmail()
        );
    }

    private ItemRequestDto.Item toItemItemRequest(Item item) {
        return new ItemRequestDto.Item(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest().getId()
        );
    }

    private Item toItem(ItemRequestDto.Item itemItemRequestDto) {
        return new Item(
                itemItemRequestDto.getId(),
                itemItemRequestDto.getName(),
                itemItemRequestDto.getDescription(),
                itemItemRequestDto.getAvailable(),
                null,
                new ItemRequest(itemItemRequestDto.getId(), null, null, null, null),
                null,
                null,
                null
        );
    }
}
