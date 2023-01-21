package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBookerId(int userId, Pageable page);

    List<Booking> findAllByItemOwnerId(int userId, Pageable page);

    List<Booking> findAllByBookerIdAndEndIsBefore(int bookerId, LocalDateTime end,  Pageable page);

    List<Booking> findAllByBookerIdAndStatus(int bookerId, BookingStatus status,  Pageable page);

    List<Booking> findAllByItemOwnerIdAndEndIsBefore(int bookerId, LocalDateTime end, Pageable page);

    List<Booking> findAllByItemOwnerIdAndStatus(int bookerId, BookingStatus status, Pageable page);

    Optional<Booking> findFirstByItemIdAndStatusOrderByEnd(int itemId, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStatusOrderByEndDesc(int itemId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdAndStartIsAfter(int bookerId, LocalDateTime start, Pageable page);

    Optional<Booking> findFirstByBookerIdAndItemIdAndStatusAndStartBefore(int userId,int itemId, BookingStatus status,
                                                                          LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore(int bookerId,
                                                                    LocalDateTime end,
                                                                    LocalDateTime start,
                                                                    Pageable page);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = :bookerId and current_timestamp > b.start" +
            " and current_timestamp < b.end")
    List<Booking> findForBookerCurrent(int bookerId, Pageable page);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = :id and b.start > current_timestamp " +
            "order by b.start desc")
    List<Booking> findAllByBookerAndFutureState(int id, Pageable page);
}
