package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.InputDataException;
import ru.practicum.shareit.exception.InputExistDataException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * // TODO .
 */
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestHeader(value = HEADER_USER_ID, required = false) Integer userId,
                                           @RequestBody ItemDto itemDto) {
        log.info("Получен запрос к эндпоинту: POST /items");
        return new ResponseEntity<>(itemService.addItem(itemDto, userId), HttpStatus.CREATED);
    }

    @GetMapping
    public List<ItemDto> getAllItem(@RequestHeader(value = HEADER_USER_ID, required = false) Integer userId) {
        log.info("Получен запрос к эндпоинту: GET /items");
        return itemService.getAllItems(userId);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable("id") int id) {
        log.info("Получен запрос к эндпоинту GET /items/{}", id);
        return itemService.getItemById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> getItemsBySubstring(@RequestParam String text) {
        log.info("Получен запрос к эндпоинту GET /items/search " + text);
        return new ResponseEntity<>(itemService.getItemsBySubString(text), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(value = HEADER_USER_ID, required = false) Integer userId,
                                              @RequestBody ItemDto itemDto, @PathVariable("id") int id) {
        log.info("Получен запрос к эндпоинту PATCH /itemDto");
        itemDto.setId(id);
        return new ResponseEntity<>(itemService.updateItem(itemDto, userId), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable("id") int id) {
        log.info("Получен запрос к эндпоинту: DELETE /item");
        itemService.deleteItem(id);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleIncorrectValidation(ValidationException e) {
        log.warn("При обработке запроса возникло исключение: " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleException(Exception e) {
        log.warn("При обработке запроса возникло исключение " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleNotFoundException(InputDataException e) {
        log.warn("При обработке запроса возникло исключение: " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleConflictDataException(InputExistDataException e) {
        log.warn("При обработке запроса возникло исключение: " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

}
