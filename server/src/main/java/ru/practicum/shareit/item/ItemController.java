package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.InputDataException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * // TODO .
 */
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemService itemService, ItemMapper itemMapper, CommentMapper commentMapper) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
        this.commentMapper = commentMapper;
    }

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestHeader(value = HEADER_USER_ID, required = false) Integer userId,
                                           @RequestBody ItemDto itemDto) {
        log.info("Получен запрос к эндпоинту: POST /items");
        Item itemCreated = itemService.addItem(itemMapper.fromItemDto(itemDto), userId);
        return new ResponseEntity<>(itemMapper.toItemDto(itemCreated), HttpStatus.CREATED);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(HEADER_USER_ID) int userId, @PathVariable int itemId,
                                 @RequestBody CommentDto commentDto) {
        log.info("Получен запрос к эндпоинту: POST /{itemId}/comment. Пользователь с id=" + userId);
        Comment comment = itemService.addComment(userId, itemId, commentMapper.toComment(commentDto));
        return commentMapper.toCommentDto(comment);
    }

    @GetMapping
    public Collection<ItemDto> getAllItemByUserId(@RequestHeader(value = HEADER_USER_ID, required = false) Integer userId,
                                                  @RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "20") int size) {
        log.info("Получен запрос к эндпоинту: GET /items, user id = " + userId);
        return itemService.getItemsByUserId(userId, from, size)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@RequestHeader(value = HEADER_USER_ID, required = false) Integer userId,
                               @PathVariable("id") int itemId) {
        log.info("Получен запрос к эндпоинту GET /items/{}", itemId);
        Item item = itemService.getItemById(itemId, userId);
        return itemMapper.toItemDto(item);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> getItemsBySubstring(@RequestParam String text,
                                                             @RequestParam(defaultValue = "0") int from,
                                                             @RequestParam(defaultValue = "20") int size) {
        log.info("Получен запрос к эндпоинту GET /items/search " + text);
        List<ItemDto> itemsDto = itemService.getItemsBySubString(text, from, size)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(itemsDto, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(value = HEADER_USER_ID, required = false) Integer userId,
                                           @RequestBody ItemDto itemDto, @PathVariable("id") int id) {
        log.info("Получен запрос к эндпоинту PATCH /itemDto");
        itemDto.setId(id);
        ItemDto itemFromDb = itemMapper.toItemDto(itemService.updateItem(itemDto, userId));
        return new ResponseEntity<>(itemFromDb, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable("id") int id) {
        log.info("Получен запрос к эндпоинту: DELETE /item");
        itemService.deleteItem(id);
    }

    @ExceptionHandler
    public ResponseEntity<ValidationException> handleIncorrectValidation(ValidationException exception) {
        log.warn("При обработке запроса возникло исключение: " + exception.getMessage());
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleException(Exception e) {
        log.warn("При обработке запроса возникло исключение: " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleNotFoundException(InputDataException e) {
        log.warn("При обработке запроса возникло исключение: " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

}
