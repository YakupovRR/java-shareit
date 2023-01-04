package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.dto.ItemDtoComment;
import ru.practicum.shareit.item.dto.ItemDtoUser;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;
import java.util.stream.Collectors;

public class ItemMapper {

    public static Item fromItemDto(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                Optional.ofNullable(itemDto.getOwner()).map(ItemMapper::toUser).orElse(null),
                null,
                Optional.ofNullable(itemDto.getLastBooking()).map(ItemMapper::toBooking).orElse(null),
                Optional.ofNullable(itemDto.getNextBooking()).map(ItemMapper::toBooking).orElse(null),
                itemDto.getComments().stream().map(ItemMapper::toComment).collect(Collectors.toList())
        );
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                toUserItem(item.getOwner()),
                Optional.ofNullable(item.getLastBooking()).map(ItemMapper::toBookingItem).orElse(null),
                Optional.ofNullable(item.getNextBooking()).map(ItemMapper::toBookingItem).orElse(null),
                item.getComments().stream().map(ItemMapper::toCommentItem).collect(Collectors.toList())
        );
    }

    private static ItemDtoUser toUserItem(User user) {
        return new ItemDtoUser(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    private static User toUser(ItemDtoUser itemUser) {
        return new User(
                itemUser.getId(),
                itemUser.getName(),
                itemUser.getEmail()
        );
    }

    private static ItemDtoBooking toBookingItem(Booking booking) {
        return new ItemDtoBooking(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd()
        );
    }

    private static Booking toBooking(ItemDtoBooking itemBooking) {
         return Booking.builder()
                .id(itemBooking.getId())
                .start(itemBooking.getStart())
                .end(itemBooking.getEnd())
                .booker(new User(itemBooking.getBookerId(), null, null))
                .build();
    }

    private static ItemDtoComment toCommentItem(Comment comment) {
        return new ItemDtoComment(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName()
        );
    }

    private static Comment toComment(ItemDtoComment itemComment) {
        return new Comment(
                itemComment.getId(),
                itemComment.getText(),
                null,
                null,
                null
        );
    }
}

