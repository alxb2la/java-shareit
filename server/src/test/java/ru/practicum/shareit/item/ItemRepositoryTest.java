package ru.practicum.shareit.item;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {
    private Long itemOwnerId;
    private Long itemRequestId;
    private Long itemId;
    private User owner;

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository requestRepository;

    @BeforeEach
    void setUp() {
        User itemOwner = User.builder()
                .id(null)
                .name("UserName1")
                .email("User1Email@Email123.net")
                .build();
        itemOwner = userRepository.save(itemOwner);
        itemOwnerId = itemOwner.getId();
        owner = itemOwner;

        User itemRequester = User.builder()
                .id(null)
                .name("UserName2")
                .email("User2Email@Email321.net")
                .build();
        userRepository.save(itemRequester);

        ItemRequest req1 = ItemRequest.builder()
                .id(null)
                .description("ItemRequestDescription")
                .created(LocalDateTime.of(2025, Month.MAY, 10, 12, 0))
                .requester(itemRequester)
                .build();
        req1 = requestRepository.save(req1);
        itemRequestId = req1.getId();

        Item item = Item.builder()
                .id(null)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(itemOwner)
                .request(req1)
                .build();
        item = itemRepository.save(item);
        itemId = item.getId();
    }

    @Test
    @DirtiesContext
    void findById_whenItemFound_thenReturnedItem() {
        Optional<Item> itemOpt = itemRepository.findById(itemId);

        assertTrue(itemOpt.isPresent());
        assertEquals("itemName", itemOpt.get().getName());
        assertEquals("itemDescription", itemOpt.get().getDescription());
        assertEquals(true, itemOpt.get().getAvailable());
        assertEquals(itemOwnerId, itemOpt.get().getOwner().getId());
        assertEquals(itemRequestId, itemOpt.get().getRequest().getId());
    }

    @Test
    @DirtiesContext
    void findById_whenItemNotFound_thenReturnedEmptyOptional() {
        Optional<Item> itemOpt = itemRepository.findById(10L);

        assertTrue(itemOpt.isEmpty());
    }

    @Test
    @DirtiesContext
    void findByOwnerIdOrderByIdAscTest() {
        Item item2 = Item.builder()
                .id(null)
                .name("itemName2")
                .description("itemDescription2")
                .available(true)
                .owner(owner)
                .request(null)
                .build();

        Item item3 = Item.builder()
                .id(null)
                .name("itemName3")
                .description("itemDescription3")
                .available(true)
                .owner(owner)
                .request(null)
                .build();

        itemRepository.saveAll(List.of(item2, item3));

        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(itemOwnerId);

        assertEquals(3, items.size());
        assertEquals(itemOwnerId, items.get(0).getOwner().getId());
        assertEquals(itemOwnerId, items.get(1).getOwner().getId());
        assertEquals(itemOwnerId, items.get(2).getOwner().getId());
        assertEquals(1, items.get(0).getId());
        assertEquals(2, items.get(1).getId());
        assertEquals(3, items.get(2).getId());
    }

    @Test
    @DirtiesContext
    void findByTextQuery_whenItemsFound_thenReturnedListOfItems() {
        Item item2 = Item.builder()
                .id(null)
                .name("itemName2")
                .description("itemDescription2qwerty123")
                .available(true)
                .owner(owner)
                .request(null)
                .build();
        itemRepository.save(item2);

        List<Item> items = itemRepository.findByTextQuery("qwerty");

        assertEquals(1, items.size());
        assertEquals("itemName2", items.get(0).getName());
        assertEquals("itemDescription2qwerty123", items.get(0).getDescription());
        assertEquals(true, items.get(0).getAvailable());
    }

    @Test
    @DirtiesContext
    void findByTextQuery_whenAvailableFalse_thenReturnedEmptyList() {
        Item item2 = Item.builder()
                .id(null)
                .name("itemName2")
                .description("itemDescription2qwerty123")
                .available(false)
                .owner(owner)
                .request(null)
                .build();
        itemRepository.save(item2);

        List<Item> items = itemRepository.findByTextQuery("qwerty");

        assertEquals(0, items.size());
    }

    @Test
    @DirtiesContext
    void findByTextQuery_whenTextQueryNotEqualNameOrDescription_thenReturnedEmptyList() {
        Item item2 = Item.builder()
                .id(null)
                .name("itemName2")
                .description("itemDescription2qwerty123")
                .available(true)
                .owner(owner)
                .request(null)
                .build();
        itemRepository.save(item2);

        List<Item> items = itemRepository.findByTextQuery("CVB");

        assertEquals(0, items.size());
    }

    @Test
    @DirtiesContext
    void findByRequestIdInTest() {
        List<Item> items = itemRepository.findByRequestIdIn(List.of(itemRequestId));

        assertEquals(1, items.size());
        assertEquals("itemName", items.get(0).getName());
        assertEquals("itemDescription", items.get(0).getDescription());
        assertEquals(true, items.get(0).getAvailable());
    }

    @AfterEach
    public void clear() {
        userRepository.deleteAll();
    }
}