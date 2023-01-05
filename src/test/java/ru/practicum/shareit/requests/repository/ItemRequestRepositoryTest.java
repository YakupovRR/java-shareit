package ru.practicum.shareit.requests.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {
    private static final int PAGE = 0;
    private static final int SIZE = 20;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private final User user1 = User.builder().id(1).name("User1").email("User1@host.com").build();
    private  final User user2 = User.builder().id(2).name("User2").email("User2@host.com").build();
    private final ItemRequest mockItemRequest1 = ItemRequest.builder().id(1).description("Description1")
            .requester(user1).created(LocalDateTime.now()).build();
    private final ItemRequest mockItemRequest2 = ItemRequest.builder().id(2).description("Description2")
            .requester(user2).created(LocalDateTime.now().plusDays(1)).build();


    @BeforeEach
    void saveData() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRequestRepository.save(mockItemRequest1);
        itemRequestRepository.save(mockItemRequest2);
    }

    @Test
    void testFindAllByRequesterIdOrderByCreatedDesc() {

        Collection<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(1);

        assertThat(itemRequests).isNotEmpty();
        assertThat(itemRequests).hasSize(1).contains(mockItemRequest1);
    }

    @Test
    void testFindAllByRequesterIdNotOrderByCreatedDesc() {

        Sort sortById = Sort.by(Sort.Direction.DESC, "created");
        Pageable page = PageRequest.of(PAGE, SIZE, sortById);

        Collection<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequesterIdNotOrderByCreatedDesc(1,page);

        assertThat(itemRequests).isNotEmpty();
        assertThat(itemRequests).hasSize(1).contains(mockItemRequest2);
    }
}