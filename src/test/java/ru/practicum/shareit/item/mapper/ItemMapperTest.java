package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemMapperTest {
    private final ItemMapper itemMapper;
    private final LocalDateTime localDateTime = LocalDateTime.now();
    private final User owner = User.builder()
            .id(1)
            .name("Mike")
            .email("ab@ya.ru")
            .build();

    private final Booking lastBooking = Booking.builder()
            .id(1)
            .item(null)
            .start(localDateTime.minusDays(3))
            .end(localDateTime.minusDays(1))
            .booker(User.builder().id(1).build())
            .status(null)
            .build();

    private final Booking nextBooking = Booking.builder()
            .id(1)
            .item(null)
            .start(localDateTime.plusDays(1))
            .end(localDateTime.plusDays(3))
            .booker(User.builder().id(1).build())
            .status(null)
            .build();

    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1)
            .description("ItemReqDesc")
            .requester(new User(2, "Bob", "abb@ya.ru"))
            .created(localDateTime)
            .items(new ArrayList<>())
            .build();
    private final ItemDto.Comment itemCommentTest1 = new ItemDto.Comment(1, "text", "author");
    private final Comment commentTest1 = new Comment(1, "text", null, null, null);
    private final Comment commentTest2 = new Comment(1, "text", null,
            User.builder().name("author").build(), null);

    private final Item itemTest1 = Item.builder()
            .id(1)
            .name("Item")
            .description("Desc")
            .available(true)
            .owner(owner)
            .request(new ItemRequest().builder().id(1).build())
            .lastBooking(lastBooking)
            .nextBooking(nextBooking)
            .comments(List.of(commentTest1))
            .build();
    private final Item itemTest2 = Item.builder()
            .id(1)
            .name("Item")
            .description("Desc")
            .available(true)
            .owner(owner)
            .request(new ItemRequest().builder().id(1).build())
            .lastBooking(lastBooking)
            .nextBooking(nextBooking)
            .comments(List.of(commentTest2))
            .build();
    private final ItemDto itemDtoTest = ItemDto.builder()
            .id(1)
            .name("Item")
            .description("Desc")
            .available(true)
            .owner(new ItemDto.User(1, "Mike", "ab@ya.ru"))
            .requestId(1)
            .lastBooking(new ItemDto.Booking(1, 1, localDateTime.minusDays(3), localDateTime.minusDays(1)))
            .nextBooking(new ItemDto.Booking(1, 1, localDateTime.plusDays(1), localDateTime.plusDays(3)))
            .comments(List.of(itemCommentTest1))
            .build();

    @Test
    void testFromItemDto() {
        Item item = itemMapper.fromItemDto(itemDtoTest);
        assertEquals(itemTest1, item);
    }

    @Test
    void testToItemDto() {
        ItemDto itemDto = itemMapper.toItemDto(itemTest2);
        assertEquals(itemDtoTest, itemDto);
    }
}