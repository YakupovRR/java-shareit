package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Booking {
    private int id;
    private int idItem;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private int userId;
    private int ownerId;
    private Status status;
}
