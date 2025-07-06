package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRequestRepositoryTest {
    private Long requesterId;

    @Autowired
    ItemRequestRepository requestRepository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(null)
                .name("UserName")
                .email("UserEmail@Email.earth")
                .build();

        user = userRepository.save(user);
        requesterId = user.getId();

        ItemRequest req1 = ItemRequest.builder()
                .id(null)
                .description("qwerwerqweeryrty")
                .created(LocalDateTime.of(2025, Month.MAY, 10, 12, 0))
                .requester(user)
                .build();

        ItemRequest req2 = ItemRequest.builder()
                .id(null)
                .description("asdfsadfsadfsa")
                .created(LocalDateTime.of(2025, Month.MAY, 11, 12, 0))
                .requester(user)
                .build();

        ItemRequest req3 = ItemRequest.builder()
                .id(null)
                .description("zxcvnbvczZcvzxcvb")
                .created(LocalDateTime.of(2025, Month.MAY, 12, 12, 0))
                .requester(user)
                .build();

        requestRepository.saveAll(List.of(req1, req2, req3));
    }

    @Test
    @DirtiesContext
    void findByRequesterIdOrderByCreatedDescTest() {
        List<ItemRequest> requests = requestRepository.findByRequesterIdOrderByCreatedDesc(requesterId);

        assertEquals(3, requests.size());
        assertEquals(3, requests.get(0).getId());
        assertEquals(LocalDateTime.of(2025, Month.MAY, 12, 12, 0),
                requests.get(0).getCreated());
        assertEquals(2, requests.get(1).getId());
        assertEquals("asdfsadfsadfsa", requests.get(1).getDescription());
        assertEquals(1, requests.get(2).getId());
        assertEquals("qwerwerqweeryrty", requests.get(2).getDescription());
    }

    @Test
    @DirtiesContext
    void findByRequesterIdNotOrderByCreatedDescTest() {
        List<ItemRequest> requests = requestRepository.findByRequesterIdNotOrderByCreatedDesc(requesterId);

        assertEquals(0, requests.size());

        User user = User.builder()
                .id(null)
                .name("UserName1")
                .email("User1Email@Email123.earth")
                .build();

        user = userRepository.save(user);

        ItemRequest req4 = ItemRequest.builder()
                .id(null)
                .description("vrvfvthgthg")
                .created(LocalDateTime.of(2025, Month.MAY, 13, 12, 0))
                .requester(user)
                .build();

        requestRepository.save(req4);

        requests = requestRepository.findByRequesterIdNotOrderByCreatedDesc(requesterId);

        assertEquals(1, requests.size());
    }

    @AfterEach
    public void clear() {
        userRepository.deleteAll();
    }
}
