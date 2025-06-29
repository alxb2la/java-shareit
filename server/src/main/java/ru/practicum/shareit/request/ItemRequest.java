package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * Класс—модель данных приложения, дающий описание объекту ItemRequest.
 */

@Entity
@Table(name = "requests")
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    private Long id;

    @Column(
            name = "description",
            nullable = false
    )
    private String description;

    @Column(
            name = "created_date_time",
            nullable = false
    )
    private LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "requester_id",
            nullable = false
    )
    private User requester;
}
