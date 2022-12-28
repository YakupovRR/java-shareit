package ru.practicum.shareit.user.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.exception.InputDataException;
import ru.practicum.shareit.exception.InputExistDataException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.validate.ValidateUserData;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ValidateUserData validateUserData;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ValidateUserData validateUserData) {
        this.userRepository = userRepository;
        this.validateUserData = validateUserData;
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.fromUserDto(userDto);
        if (validateUserData.checkAllData(user)) {
            if (isExistEmail(user.getEmail())) {
                throw new InputExistDataException("Пользователь с таким email уже существует");
            }
            return UserMapper.toUserDto(userRepository.addUser(user));
        } else {
            log.warn("Запрос к эндпоинту POST /users не обработан.");
            throw new ValidationException("Одно или несколько условий не выполняются");
        }
    }

    @Override
    public UserDto getUser(int id) {
        if (isContainsUser(id)) {
            return UserMapper.toUserDto(userRepository.getUser(id));
        } else {
            log.warn("Запрос к эндпоинту GET /users/{} не обработан", id);
            throw new InputDataException("Пользователь с таким id не найден");
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(UserDto userDto, int id) {
        User user = UserMapper.fromUserDto(userDto);
        if (!isContainsUser(id)) {
            throw new InputDataException("Пользователь с " + id + " не найден");
        }
        if (user.getEmail() != null && isExistEmail(user.getEmail())) {
            throw new InputExistDataException("Пользователь с таким email уже существует");
        }
        if (id > 0) {
            user.setId(id);
            return UserMapper.toUserDto(userRepository.updateUser(user));
        } else {
            log.warn("Запрос к эндпоинту PATCH /users не обработан.");
            throw new ValidationException("Одно или несколько условий не выполняются");
        }
    }

    @Override
    public void deleteUser(int id) {
        userRepository.deleteUser(id);
    }

    @Override
    public boolean isContainsUser(int id) {
        return userRepository.isContainUser(id);
    }

    @Override
    public boolean isExistEmail(String email) {
        return userRepository.isExistEmail(email);
    }

}
