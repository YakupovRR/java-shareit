package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Collection<Booking> findAllByBookerIdOrderByStartDesc(int userId);

    Collection<Booking> findAllByItemOwnerIdOrderByStartDesc(int userId);

    List<Booking> findAllByBookerIdAndEndIsBefore(int bookerId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStatus(int bookerId, BookingStatus status);

    Collection<Booking> findAllByItemOwnerIdAndEndIsBefore(int bookerId, LocalDateTime end);

    Collection<Booking> findAllByItemOwnerIdAndStatus(int bookerId, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStatusOrderByEnd(int itemId, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStatusOrderByEndDesc(int itemId, BookingStatus status);

    Collection<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(int bookerId, LocalDateTime start);

    Optional<Booking> findFirstByBookerIdAndItemIdAndStatusAndStartBefore(int userId, int itemId,
                                                                          BookingStatus status, LocalDateTime now);

    Collection<Booking> findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore(int bookerId,
                                                                          LocalDateTime end,
                                                                          LocalDateTime start);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = :bookerId and current_timestamp > b.start" +
            " and current_timestamp < b.end")
    List<Booking> findForBookerCurrent(int bookerId);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = :id and b.start > current_timestamp " +
            "order by b.start desc")
    List<Booking> findAllByBookerAndFutureState(int id);
}
