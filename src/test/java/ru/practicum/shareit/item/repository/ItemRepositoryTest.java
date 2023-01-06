package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {
    private static final int PAGE = 0;
    private static final int SIZE = 20;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private final User mockUser1 = User.builder().id(1).name("User1").email("FirstUser@ya.ru").build();
    private final User mockUser2 = User.builder().id(2).name("User2").email("SecondUser@ya.ru").build();

    private final Item mockItem1 = Item.builder().id(1).name("Item")
            .description("Description1").available(true).owner(mockUser1).build();

    private final Item mockItem2 = Item.builder().name("Item2")
            .description("Description2").available(true).owner(mockUser2).build();

    Pageable getPage() {
        Sort sortById = Sort.by(Sort.Direction.DESC, "id");
        return PageRequest.of(PAGE, SIZE, sortById);
    }

    @BeforeEach
    void saveData() {
        userRepository.save(mockUser1);
        userRepository.save(mockUser2);
        itemRepository.save(mockItem1);
        itemRepository.save(mockItem2);
    }

    @Test
    void testGetAllByOwnerId() {

        Collection<Item> items = itemRepository.findAllByOwnerId(1, getPage());

        assertThat(items).isNotEmpty();
        assertThat(items).hasSize(1).contains(mockItem1);
    }

    @Test
    void testSearch() {

        Collection<Item> items = itemRepository.search("Description2", getPage());

        assertThat(items).isNotEmpty();
        assertThat(items).hasSize(1).contains(mockItem2);
    }
}