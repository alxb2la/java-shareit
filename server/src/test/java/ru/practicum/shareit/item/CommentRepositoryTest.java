package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CommentRepositoryTest {
    private Long item1Id;
    private Long item2Id;

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User itemOwner = User.builder()
                .id(null)
                .name("UserName1")
                .email("User1Email@Email123.net")
                .build();
        itemOwner = userRepository.save(itemOwner);

        User commentAuthor = User.builder()
                .id(null)
                .name("UserName2")
                .email("User2Email@Email321.net")
                .build();
        userRepository.save(commentAuthor);

        Item item1 = Item.builder()
                .id(null)
                .name("item1Name")
                .description("item1Description")
                .available(true)
                .owner(itemOwner)
                .request(null)
                .build();
        item1 = itemRepository.save(item1);
        item1Id = item1.getId();

        Item item2 = Item.builder()
                .id(null)
                .name("item2Name")
                .description("item2Description")
                .available(true)
                .owner(itemOwner)
                .request(null)
                .build();
        item2 = itemRepository.save(item2);
        item2Id = item2.getId();

        Comment comment1 = Comment.builder()
                .id(null)
                .text("qwerrewrtsdfg 234sdfgd dfgdfg")
                .created(LocalDateTime.of(2025, Month.MAY, 11, 18, 0))
                .author(commentAuthor)
                .item(item1)
                .build();
        commentRepository.save(comment1);

        Comment comment2 = Comment.builder()
                .id(null)
                .text("zxcxzcbzc 654gfdhj ghjkfgj")
                .created(LocalDateTime.of(2025, Month.MAY, 20, 18, 0))
                .author(commentAuthor)
                .item(item2)
                .build();
        commentRepository.save(comment2);
    }

    @Test
    void findByItemIdTest() {
        List<Comment> comments = commentRepository.findByItemId(item1Id);

        assertEquals(1, comments.size());
        assertEquals("qwerrewrtsdfg 234sdfgd dfgdfg", comments.get(0).getText());
        assertEquals(LocalDateTime.of(2025, Month.MAY, 11, 18, 0),
                comments.get(0).getCreated());
        assertEquals("UserName2", comments.get(0).getAuthor().getName());
        assertEquals("item1Name", comments.get(0).getItem().getName());
    }

    @Test
    void findByItemIdInOrderByItemIdAscTest() {
        List<Comment> comments = commentRepository.findByItemIdInOrderByItemIdAsc(List.of(item2Id, item1Id));

        assertEquals(2, comments.size());
        assertEquals(item1Id, comments.get(0).getItem().getId());
        assertEquals(item2Id, comments.get(1).getItem().getId());
        assertEquals("qwerrewrtsdfg 234sdfgd dfgdfg", comments.get(0).getText());
        assertEquals("zxcxzcbzc 654gfdhj ghjkfgj", comments.get(1).getText());
        assertEquals("UserName2", comments.get(0).getAuthor().getName());
        assertEquals("UserName2", comments.get(1).getAuthor().getName());
    }
}
