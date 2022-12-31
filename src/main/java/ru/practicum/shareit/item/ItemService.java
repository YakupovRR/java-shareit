package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InputDataException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.validate.ValidateItemData;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ValidateItemData validateItemData;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, UserService userService, ValidateItemData validateItemData,
                       BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.validateItemData = validateItemData;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }


    public ItemDto addItem(ItemDto itemDto, Integer userId) {
        Item item = ItemMapper.fromItemDto(itemDto);
        if (userId == null) {
            throw new ValidationException("Отсутствует id пользователя, создавший данную вещь");
        }
        if (!userService.isContainsUser(userId)) {
            throw new InputDataException("Пользователь с id=" + userId + " не найден в БД");
        }
        if (validateItemData.checkAllData(item)) {
            User user = UserMapper.fromUserDto(userService.getUser(userId));
            item.setOwner(user);
            return ItemMapper.toItemDto(itemRepository.save(item));
        } else {
            throw new ValidationException("Ошибка во входных данных");
        }
    }

    public ItemDto getItemById(int itemId, int userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new InputDataException(
                "Вещь по id не найдена"));
        if (item.getOwner().getId() == userId) {
            setBookings(item);
        }
        return ItemMapper.toItemDto(item);
    }

    public List<Item> getItemsByUserId(int userId) {
            return itemRepository.findAllByOwnerId(userId)
                    .stream()
                    .map(this::setBookings)
                    .collect(Collectors.toList());
    }

    public List<ItemDto> getItemsBySubString(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        } else {
            return itemRepository.search(text)
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    public List<ItemDto> getAllItems(Integer userId) {
        if (userId != null) {
            return getItemsByUserId(userId)
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        } else {
            return itemRepository.findAll()
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    public ItemDto updateItem(ItemDto itemDto, Integer userId) {
        Item itemFromDb = ItemMapper.fromItemDto(getItemById(itemDto.getId(), userId));

        if (itemFromDb.getOwner().getId() != userId) {
            throw new InputDataException("Id пользователя не совпадает с id создавшего вещь пользователя");
        }

        Optional.ofNullable(itemDto.getName()).ifPresent(itemFromDb::setName);
        Optional.ofNullable(itemDto.getDescription()).ifPresent(itemFromDb::setDescription);
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(itemFromDb::setAvailable);

        return ItemMapper.toItemDto(itemRepository.save(itemFromDb));
    }

    public void deleteItem(int id) {
        isContainItem(id);
        itemRepository.deleteById(id);
    }

    public Comment addComment(int userId, int itemId, Comment comment) {
        if (comment.getText().isEmpty()) {
            throw new ValidationException("Текст отзыва пустой");
        }
        User user = UserMapper.fromUserDto(userService.getUser(userId));
        Item item = ItemMapper.fromItemDto(getItemById(itemId, userId));
        bookingRepository.findFirstByBookerIdAndItemIdAndStatusAndStartBefore(userId, itemId, BookingStatus.APPROVED,
                        LocalDateTime.now())
                .orElseThrow(() -> new ValidationException("Ошибка во входных данных"));

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
