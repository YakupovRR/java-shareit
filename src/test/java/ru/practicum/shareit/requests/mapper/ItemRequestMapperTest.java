package ru.practicum.shareit.requests.mapper;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestMapperTest {

    private final ItemRequestMapper itemRequestMapper;
    private final LocalDateTime localDateTime = LocalDateTime.now();
    private final User user = User.builder()
            .id(1)
            .name("Mike")
            .email("ab@ya.ru")
            .build();
    private final ItemRequestDto.User userRequestDto = ItemRequestDto.User.builder()
            .id(1)
            .name("Mike")
            .email("ab@ya.ru")
            .build();
    private final ItemRequestDto.Item itemReqDto = ItemRequestDto.Item.builder()
            .id(1)
            .name("Item")
            .description("Description")
            .available(true)
            .requestId(1)
            .build();
    private final Item itemTest1 = Item.builder()
            .id(1)
            .name("Item")
            .description("Description")
            .available(true)
            .request(new ItemRequest().builder().id(1).build())
            .build();
    private final ItemRequest itemRequestTest = ItemRequest.builder()
            .id(1)
            .description("Description")
            .requester(user)
            .created(localDateTime)
            .items(List.of(itemTest1))
            .build();

    ItemRequestDto itemRequestDtoTest = ItemRequestDto.builder()
            .id(1)
            .description("Description")
            .requester(userRequestDto)
            .created(localDateTime)
            .items(List.of(itemReqDto))
            .build();

    @Test
    void testToItemRequestDto() {
        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequestTest);
        assertEquals(itemRequestDtoTest, itemRequestDto);
    }

    @Test
    void testToItemRequest() {
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDtoTest);
        assertEquals(itemRequestTest, itemRequest);
    }
}