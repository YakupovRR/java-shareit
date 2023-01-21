package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Creating user {}", userDto);
        return userClient.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findUserById(@PathVariable long userId) {
        log.info("Get user {}", userId);
        return userClient.findUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Get users");
        return userClient.findAll();
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId, @RequestBody UserDto userDto) {
        log.info("Updating user {}", userId);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Deleting user {}", userId);
        userClient.deleteUser(userId);
    }
}
