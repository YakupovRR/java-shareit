package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InputDataException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import ru.practicum.shareit.trait.PageTool;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestService implements PageTool {

    private final ItemRequestRepository requestRepository;
    private final UserService userService;
    private final ItemRequestMapper itemRequestMapper;

    public ItemRequest createRequest(int userId, ItemRequest request) {
        userService.isContainsUser(userId);
        checkInputRequestData(request);
        User user = userService.getUser(userId);
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        return requestRepository.save(request);
    }

    public Collection<ItemRequest> getAllRequestByUserId(int userId) {
        userService.isContainsUser(userId);
        return requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
    }

    public List<ItemRequest> getAllRequest(int userId, int from, int size) {
        userService.isContainsUser(userId);
        if (from < 0) {
            throw new ValidationException("Ошибка во входных данных страницы");
        }

        Pageable page = getPage(from, size, "created", Sort.Direction.ASC);
        return requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId, page);
    }

    public ItemRequest getRequestById(int userId, int requestId) {
        userService.isContainsUser(userId);
        return requestRepository.findById(
                requestId).orElseThrow(() -> new InputDataException("Не найден запрос по id = " + requestId)
        );
    }

    public void checkItemRequestExistsById(int requestId) {
        if (!requestRepository.existsById(requestId)) {
            throw new InputDataException("Запрос вещи по id = " + requestId + " не найден в базе данных");
        }
    }

    private void checkInputRequestData(ItemRequest requestDto) {
        if (requestDto.getDescription() == null || requestDto.getDescription().isEmpty()) {
            throw new ValidationException("Отсутствует описание запрашиваемой вещи");
        }
    }
}
