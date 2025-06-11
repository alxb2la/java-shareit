package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * Класс—модель данных приложения, дающий описание объекту Booking.
 */

@Entity
@Table(name = "bookings")
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    private Long id;

    @Column(
            name = "start_date_time",
            nullable = false
    )
    private LocalDateTime start;

    @Column(
            name = "end_date_time",
            nullable = false
    )
    private LocalDateTime end;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "item_id",
            nullable = false
    )
    private Item item;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "booker_id",
            nullable = false
    )
    private User booker;

    @Column(
            name = "status",
            nullable = false
    )
    @Enumerated(EnumType.STRING)
    private BookingStatus status;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;
        return id != null && id.equals(((Booking) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public enum BookingStatus {
        WAITING,
        APPROVED,
        REJECTED,
        CANCELED
    }
}
