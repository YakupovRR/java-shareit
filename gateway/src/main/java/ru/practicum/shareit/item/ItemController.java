package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(HEADER_USER_ID) long userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        log.info("Creating item {}, userId={}", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@RequestHeader(HEADER_USER_ID) long userId, @PathVariable long itemId) {
        log.info("Get item {}, userId={}", itemId, userId);
        return itemClient.findItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader(HEADER_USER_ID) long userId,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Get items by userId={}, from={}, size={}", userId, from, size);
        return itemClient.findAllByUserId(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(HEADER_USER_ID) long userId,
                                             @PathVariable long itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Updating item {}", itemId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(HEADER_USER_ID) long userId, @PathVariable long itemId) {
        log.info("Deleting item {}", itemId);
        itemClient.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemByText(@RequestParam String text,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                   @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Search items by text={}, from={}, size={}", text, from, size);
        return itemClient.searchItemByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(HEADER_USER_ID) long userId,
                                                @PathVariable long itemId,
                                                @Valid @RequestBody CommentDto commentDto) {
        log.info("Creating comment {}, userId={}, itemId={}", commentDto, userId, itemId);
        return itemClient.createComment(userId, itemId, commentDto);
    }
}
