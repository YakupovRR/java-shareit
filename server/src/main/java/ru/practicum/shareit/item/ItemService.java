package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InputDataException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.validate.ValidateItemData;
import ru.practicum.shareit.requests.ItemRequestService;
import ru.practicum.shareit.trait.PageTool;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemService implements PageTool {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ValidateItemData validateItemData;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestService itemRequestService;
    private final ItemMapper itemMapper;

    public Item addItem(Item item, Integer userId) {
        if (userId == null) {
            throw new ValidationException("Отсутствует id пользователя, создавший данную вещь");
        }
        if (!userService.isContainsUser(userId)) {
            throw new InputDataException("Пользователь с id=" + userId + " не найден в БД");
        }
        if (item.getRequest() != null) {
            System.out.println(item.getRequest());
            itemRequestService.checkItemRequestExistsById(item.getRequest().getId());
        }
        if (validateItemData.checkAllData(userId, item, userService)) {
            User user = userService.getUser(userId);
            item.setOwner(user);
            return itemRepository.save(item);
        } else {
            throw new ValidationException("Ошибка во входных данных");
        }
    }

    public Item getItemById(int itemId, int userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new InputDataException(
                "Вещь по id не найдена"));
        if (item.getOwner().getId() == userId) {
            setBookings(item);
        }
        return item;
    }

    public Collection<Item> getItemsByUserId(int userId, int from, int size) {
        userService.isContainsUser(userId);
        Pageable page = getPage(from, size, "id", Sort.Direction.ASC);
            return itemRepository.findAllByOwnerId(userId, page)
                    .stream()
                    .map(this::setBookings)
                    .collect(Collectors.toList());
    }

    public List<Item> getItemsBySubString(String text, int from, int size) {
        Pageable page = getPage(from, size, "id", Sort.Direction.ASC);
        if (text.isEmpty()) {
            return Collections.emptyList();
        } else {
            return itemRepository.search(text, page);
        }
    }

    public List<ItemDto> getAllItems(Integer userId, int from, int size) {
        if (userId != null) {
            return getItemsByUserId(userId, from, size)
                    .stream()
                    .map(itemMapper::toItemDto)
                    .collect(Collectors.toList());
        } else {
            return itemRepository.findAll()
                    .stream()
                    .map(itemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    public Item updateItem(ItemDto itemDto, Integer userId) {
        Item itemFromDb = getItemById(itemDto.getId(), userId);

        if (itemFromDb.getOwner().getId() != userId) {
            throw new InputDataException("Id пользователя не совпадает с id создавшего вещь пользователя");
        }

        Optional.ofNullable(itemDto.getName()).ifPresent(itemFromDb::setName);
        Optional.ofNullable(itemDto.getDescription()).ifPresent(itemFromDb::setDescription);
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(itemFromDb::setAvailable);

        return itemRepository.save(itemFromDb);
    }

    public void deleteItem(int id) {
        isContainItem(id);
        itemRepository.deleteById(id);
    }

    public Comment addComment(int userId, int itemId, Comment comment) {
        if (comment.getText().isEmpty()) {
            throw new ValidationException("Текст отзыва пустой");
        }
        User user = userService.getUser(userId);
        Item item = getItemById(itemId, userId);
        bookingRepository.findFirstByBookerIdAndItemIdAndStatusAndStartBefore(userId, itemId, BookingStatus.APPROVED,
                LocalDateTime.now()).orElseThrow(() -> new ValidationException("Пользователь не может оставить комментарий"));

        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public boolean isContainItem(int id) {
        if (!itemRepository.existsById(id)) {
            throw new InputDataException("Вещь не найдена");
        } else {
            return true;
        }
    }

    private Item setBookings(Item item) {
        Optional<Booking> lastBooking = getLastBookingForItem(item.getId());
        Optional<Booking> nextBooking = getNextBookingForItem(item.getId());

        item.setLastBooking(lastBooking.orElse(null));
        item.setNextBooking(nextBooking.orElse(null));

        return item;
    }

    private Optional<Booking> getLastBookingForItem(int itemId) {
        return bookingRepository.findFirstByItemIdAndStatusOrderByEnd(itemId,
                BookingStatus.APPROVED);
    }

    private Optional<Booking> getNextBookingForItem(int itemId) {
        return bookingRepository.findFirstByItemIdAndStatusOrderByEndDesc(itemId,
                BookingStatus.APPROVED);
    }
}
