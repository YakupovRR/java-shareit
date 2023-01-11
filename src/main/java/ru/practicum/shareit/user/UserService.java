package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InputDataException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.validate.ValidateUserData;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ValidateUserData validateUserData;
    private final UserMapper userMapper;

    public UserDto addUser(UserDto userDto) {
        User user = userMapper.fromUserDto(userDto);
        if (validateUserData.checkAllData(user)) {
            return userMapper.toUserDto(userRepository.save(user));
        } else {
            log.warn("Запрос к эндпоинту POST /users не обработан.");
            throw new ValidationException("Одно или несколько условий не выполняются");
        }
    }

    public User getUser(int id) {
        return userRepository.findById(id).orElseThrow(() -> new InputDataException(
                "Пользователь с таким id не найден"));
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto updateUser(UserDto userDto, int id) throws InputDataException {
        User user = userMapper.fromUserDto(userDto);
        User userDb = getUser(id);
        Optional.ofNullable(user.getEmail()).ifPresent(userDb::setEmail);
        Optional.ofNullable(user.getName()).ifPresent(userDb::setName);
        return userMapper.toUserDto(userRepository.save(userDb));
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    public boolean isContainsUser(int id) {
        if (userRepository.existsById(id)) {
            return true;
        } else {
            throw new InputDataException("Пользователь не найден");
        }
    }
}
