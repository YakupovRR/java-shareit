package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    public Item fromItemDto(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                Optional.ofNullable(itemDto.getOwner()).map(this::toUser).orElse(null),
                Optional.ofNullable(itemDto.getRequestId()).map(
                        (requestId) -> new ItemRequest(requestId, null, null, null, null)
                ).orElse(null),
                Optional.ofNullable(itemDto.getLastBooking()).map(this::toBooking).orElse(null),
                Optional.ofNullable(itemDto.getNextBooking()).map(this::toBooking).orElse(null),
                itemDto.getComments().stream().map(this::toComment).collect(Collectors.toList())
        );
    }

    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(toUserItem(item.getOwner()))
                .requestId(Optional.ofNullable(item.getRequest()).map(ItemRequest::getId).orElse(null))
                .lastBooking(Optional.ofNullable(item.getLastBooking()).map(this::toBookingItem).orElse(null))
                .nextBooking(Optional.ofNullable(item.getNextBooking()).map(this::toBookingItem).orElse(null))
                .comments(item.getComments().stream().map(this::toCommentItem).collect(Collectors.toList()))
                .build();
    }

    private ItemDto.User toUserItem(User user) {
        return new ItemDto.User(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    private User toUser(ItemDto.User itemUser) {
        return new User(
                itemUser.getId(),
                itemUser.getName(),
                itemUser.getEmail()
        );
    }

    private ItemDto.Booking toBookingItem(Booking booking) {
        return new ItemDto.Booking(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd()
        );
    }

    private Booking toBooking(ItemDto.Booking itemBooking) {
        return Booking.builder()
                .id(itemBooking.getId())
                .start(itemBooking.getStart())
                .end(itemBooking.getEnd())
                .booker(new User(itemBooking.getBookerId(), null, null))
                .build();
    }

    private ItemDto.Comment toCommentItem(Comment comment) {
        return new ItemDto.Comment(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName()
        );
    }

    private Comment toComment(ItemDto.Comment itemComment) {
        return new Comment(
                itemComment.getId(),
                itemComment.getText(),
                null,
                null,
                null
        );
    }
}

