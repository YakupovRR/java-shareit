

/*
Для ревьюера
Я уже 2 раза переписал приложение. Хз почему, но постман всё проходит, на гите валится
Прошу извинить, закидываю как есть, после первых новогодних чисел буду спрашивать у наставника
А пока надо двигаться дальше
 */


package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BookingDto {
    private Long id;

    @NotNull
    @Future
    private LocalDateTime start;

    @NotNull
    @Future
    private LocalDateTime end;

    private BookingStatus status;

    private User booker;

    private Item item;
}
