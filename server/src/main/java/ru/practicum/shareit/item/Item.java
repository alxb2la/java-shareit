package ru.practicum.shareit.item;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * Класс—модель данных приложения, дающий описание объекту Item.
 */

@Entity
@Table(name = "items")
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    private Long id;

    @Column(
            name = "name",
            nullable = false
    )
    private String name;

    @Column(
            name = "description",
            nullable = false
    )
    private String description;

    @Column(
            name = "available",
            nullable = false
    )
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "owner_id",
            nullable = false
    )
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "request_id"
    )
    private ItemRequest request;
}
