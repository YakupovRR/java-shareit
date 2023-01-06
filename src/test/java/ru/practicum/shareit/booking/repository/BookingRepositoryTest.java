package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {
    private static final int PAGE = 0;
    private static final int SIZE = 20;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;


    private static final User mockUser1 = User.builder().id(1).name("User1")
            .email("User1@ya.com").build();
    private static final User mockUser2 = User.builder().id(2).name("User2")
            .email("User2@ya.com").build();

    private static final Item mockItem1 = Item.builder().id(1).name("Item").description("Description")
            .available(true).owner(mockUser1).build();
    private static final Item mockItem2 = Item.builder().name("Item").description("Description")
            .available(true).owner(mockUser2).build();

    private static final Booking mockBooking1 = Booking.builder().id(1)
            .start(LocalDate.now().atStartOfDay().plusDays(1))
            .end(LocalDate.now().atStartOfDay().plusDays(2))
            .item(mockItem1).booker(mockUser2).status(BookingStatus.WAITING).build();

    private static final Booking mockBooking2 = Booking.builder().id(2)
            .start(LocalDate.now().atStartOfDay().plusMonths(1))
            .end(LocalDate.now().atStartOfDay().plusMonths(1).plusDays(3))
            .item(mockItem1).booker(mockUser2).status(BookingStatus.APPROVED).build();

    private static final Booking mockBooking3 = Booking.builder()
            .start(LocalDate.now().atStartOfDay().plusDays(4))
            .end(LocalDate.now().atStartOfDay().plusDays(5))
            .item(mockItem2).booker(mockUser1).status(BookingStatus.WAITING).build();

    private static final Booking mockBooking4 = Booking.builder()
            .start(LocalDate.now().atStartOfDay().plusDays(6)).end(LocalDate.now().atStartOfDay().plusDays(7))
            .item(mockItem2).booker(mockUser1).status(BookingStatus.REJECTED).build();

    Pageable getPage() {
        Sort sortById = Sort.by(Sort.Direction.DESC, "start");
        return PageRequest.of(PAGE, SIZE, sortById);
    }

    @BeforeEach
    void saveData() {
        userRepository.save(mockUser1);
        userRepository.save(mockUser2);
        itemRepository.save(mockItem1);
        itemRepository.save(mockItem2);
        bookingRepository.save(mockBooking1);
        bookingRepository.save(mockBooking2);
        bookingRepository.save(mockBooking3);
        bookingRepository.save(mockBooking4);
    }

    @Test
    void testFindAllByBookerId() {

        Collection<Booking> bookings = bookingRepository.findAllByBookerId(2, getPage());

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).hasSize(2).contains(mockBooking1, mockBooking2);
    }



    @Test
    void testFindAllByBookerIdAndEndIsBefore() {

        Collection<Booking> bookings = bookingRepository.findAllByBookerIdAndEndIsBefore(2,
                LocalDateTime.now().plusDays(15), getPage());

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).hasSize(1).contains(mockBooking1);
    }

    @Test
    void testFindAllByBookerId_StartIsAfter() {

        Collection<Booking> bookings = bookingRepository.findAllByBookerAndFutureState(2, getPage());

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).hasSize(2).contains(mockBooking1, mockBooking2);
    }

    @Test
    void testFindAllByBookerId_Status() {

        Collection<Booking> bookings = bookingRepository.findAllByBookerIdAndStatus(1,
                BookingStatus.REJECTED, getPage());

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).hasSize(1).contains(mockBooking4);
    }

    @Test
    void testFindAllByItemOwnerId() {

        Collection<Booking> bookings = bookingRepository.findAllByItemOwnerId(1, getPage());

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).hasSize(2).contains(mockBooking1, mockBooking2);
    }

    @Test
    void testFindAllByItemOwnerIdEndIsAfterAndStartIsBefore() {

        Collection<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndEndIsAfterAndStartIsBefore(1,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), getPage());

        assertThat(bookings).isNotEmpty();
        assertThat(bookings).hasSize(1).contains(mockBooking1);
    }

    @Test
    void testFindAllByItemOwnerIdEndIsBefore() {

        Collection<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(1,
                LocalDateTime.now().plusMonths(1).plusDays(4), getPage());

        assertThat(bookings).hasSize(2).contains(mockBooking1, mockBooking2);
    }

    @Test
    void testFindAllByItemOwnerIdStartIsAfter() {

        Collection<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(2,
                LocalDateTime.now().plusDays(2), getPage());

        assertThat(bookings).hasSize(2).contains(mockBooking3, mockBooking4);
    }

    @Test
    void testFindAllByItemOwnerIdStatus() {

        Collection<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStatus(1,
                BookingStatus.WAITING, getPage());

        assertThat(bookings).hasSize(1).contains(mockBooking1);
    }

    @Test
    void testFindFirstByItemOwnerIdStatusOrderByEnd() {

        Optional<Booking> bookings = bookingRepository.findFirstByItemIdAndStatusOrderByEnd(1,
                BookingStatus.APPROVED);

        assertThat(bookings).isPresent();
        assertThat(bookings.get()).isEqualTo(mockBooking2);
    }

    @Test
    void testFindFirstByItemOwnerIdStatusOrderByEndDesc() {

        Optional<Booking> bookings = bookingRepository.findFirstByItemIdAndStatusOrderByEndDesc(2,
                BookingStatus.REJECTED);

        assertThat(bookings).isPresent();
        assertThat(bookings.get()).isEqualTo(mockBooking4);
    }

    @Test
    void testFindFirstByBookerIdAndItemId_StatusAndStartAreBefore() {

        Optional<Booking> bookings = bookingRepository.findFirstByBookerIdAndItemIdAndStatusAndStartBefore(1,
                2, BookingStatus.WAITING, LocalDateTime.now().plusDays(7));

        assertThat(bookings).isPresent();
        assertThat(bookings.get()).isEqualTo(mockBooking3);
    }
}