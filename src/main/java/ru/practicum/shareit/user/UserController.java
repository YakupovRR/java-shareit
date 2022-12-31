package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * // TODO .
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос к эндпоинту POST /users");
        return userService.create(userDto);
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        log.info("Получен запрос к эндпоинту GET /users/{}", id);
        return userService.getById(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Получен запрос к эндпоинту: GET /users");
        return userService.getAll();
    }


    @PatchMapping("/{id}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable Long id) {
        log.info("Получен запрос к эндпоинту: PATCH /users");
        return userService.update(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Получен запрос к эндпоинту: DELETE /users/{}", id);
        userService.delete(id);
    }

}
