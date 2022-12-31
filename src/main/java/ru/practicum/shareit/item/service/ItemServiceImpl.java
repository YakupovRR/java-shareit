package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingMapper.toBookingShortDto;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.item.model.CommentMapper.toComment;
import static ru.practicum.shareit.item.model.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.model.ItemMapper.toItem;
import static ru.practicum.shareit.item.model.ItemMapper.toItemDto;

@Component
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getAll(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<ItemDto> itemDtoList = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        itemDtoList.forEach(itemDto -> {
            itemDto.setLastBooking(bookingRepository.findAllByItemIdOrderByStartAsc(itemDto.getId()).isEmpty() ?
                    null : toBookingShortDto(bookingRepository.findAllByItemIdOrderByStartAsc(itemDto.getId()).get(0)));
            itemDto.setNextBooking(itemDto.getLastBooking() == null ?
                    null : toBookingShortDto(bookingRepository.findAllByItemIdOrderByStartDesc(itemDto.getId()).get(0)));
            itemDto.setComments(commentRepository.findAllByItemId(itemDto.getId())
                    .stream().map(CommentMapper::toCommentDto).collect(Collectors.toList()));
        });

        return itemDtoList;
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getById(Long id, Long ownerId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id: " + id));
        ItemDto itemDto = toItemDto(item);
        itemDto.setComments(commentRepository.findAllByItemId(id)
                .stream().map(CommentMapper::toCommentDto).collect(Collectors.toList()));
        if (item.getOwner().getId().equals(ownerId)) {
            itemDto.setLastBooking(bookingRepository.findAllByItemIdOrderByStartAsc(id).isEmpty() ? null :
                    toBookingShortDto(bookingRepository.findAllByItemIdOrderByStartAsc(id).get(0)));
            itemDto.setNextBooking(itemDto.getLastBooking() == null ?
                    null : toBookingShortDto(bookingRepository.findAllByItemIdOrderByStartDesc(itemDto.getId())
                    .get(0)));
        }

        return itemDto;
    }

    @Transactional
    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Невозможно создать вещь - " +
                        "не найден пользователь с id: " + userId));
        Item item = toItem(itemDto);
        item.setOwner(user);
        itemRepository.save(item);

        return toItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto update(ItemDto itemDto, Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id: " + id));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Невозможно обновить вещь - у пользователя с id: " + userId + "нет такой вещи");
        }
        Optional.ofNullable(itemDto.getName()).ifPresent(item::setName);
        Optional.ofNullable(itemDto.getDescription()).ifPresent(item::setDescription);
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(item::setAvailable);

        return toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> search(String text) {
        List<ItemDto> searchedItems = new ArrayList<>();
        if (text.isBlank()) {
            return searchedItems;
        }
        for (Item item : itemRepository.findAll()) {
            if (isSearched(text, item)) {
                searchedItems.add(toItemDto(item));
            }
        }

        return searchedItems;
    }

    @Transactional
    @Override
    public CommentDto createComment(Long itemId, Long userId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Невозможно создать комментарий - " +
                        "не существует пользователя с id " + userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Невозможно создать комментарий - " +
                        "не существует вещи с id " + itemId));
        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(userId, itemId, APPROVED,
                LocalDateTime.now()).isEmpty()) {
            throw new BadRequestException("Невозможно создать комментарий - " +
                    "вещь не бралась пользователем в аренду или аренда вещи еще не завершена");
        }
        Comment comment = toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        return toCommentDto(comment);
    }

    private Boolean isSearched(String text, Item item) {
        return item.getName().toLowerCase().contains(text.toLowerCase()) ||
                item.getDescription().toLowerCase().contains(text.toLowerCase()) && item.getAvailable();
    }

}