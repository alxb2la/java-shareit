package ru.practicum.shareit.item;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * Класс—модель данных приложения, дающий описание объекту Comment.
 */

@Entity
@Table(name = "comments")
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    private Long id;

    @Column(
            name = "text",
            nullable = false
    )
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "item_id",
            nullable = false
    )
    private Item item;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "author_id",
            nullable = false
    )
    private User author;

    @Column(
            name = "created_date_time",
            nullable = false
    )
    private LocalDateTime created;
}
