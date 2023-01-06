package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.InputDataException;
import ru.practicum.shareit.exception.ValidationErrorResponse;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * // TODO .
 */
@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    private final ItemRequestService requestService;
    private final ItemRequestMapper itemRequestMapper;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(HEADER_USER_ID) int userId,
                                     @Valid @RequestBody ItemRequestDto requestDto) {
        log.info("Получен запрос к эндпоинту POST /requests");
        ItemRequest itemRequest = requestService.createRequest(userId,  itemRequestMapper.toItemRequest(requestDto));
        return itemRequestMapper.toItemRequestDto(itemRequest);
    }

    @GetMapping
    public Collection<ItemRequestDto> getAllRequestByUserId(@RequestHeader(HEADER_USER_ID) int userId) {
        log.info("Получен запрос к эндпоинту GET /requests");
        Collection<ItemRequest> allRequestsByUserId = requestService.getAllRequestByUserId(userId);
        return allRequestsByUserId.stream().map(itemRequestMapper::toItemRequestDto).collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequest(@RequestHeader(HEADER_USER_ID) int userId,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "20") int size) {
        log.info("Получен запрос к эндпоинту GET /requests/all");
        List<ItemRequest> list = requestService.getAllRequest(userId, from, size);
        return list.stream().map(itemRequestMapper::toItemRequestDto).collect(Collectors.toList());
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(HEADER_USER_ID) int userId,
                                      @PathVariable int requestId) {
        log.info("Получен запрос к эндпоинту GET /requests/{}", requestId);
        return itemRequestMapper.toItemRequestDto(requestService.getRequestById(userId, requestId));
    }

    @ExceptionHandler
    public ResponseEntity<ValidationErrorResponse> handleIncorrectValidation(ValidationException e) {
        log.warn("При обработке запроса возникло исключение: " + e.getMessage());
        return new ResponseEntity<>(new ValidationErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
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
}
