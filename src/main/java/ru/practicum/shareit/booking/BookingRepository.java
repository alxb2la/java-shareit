package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс, определяющий набор стандартных и дополнительных действий хранения и поиска
 * с объектом типа Booking, используя запросные методы
 */

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId,
                                                                          LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime end);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime start);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, Booking.BookingStatus status);

    List<Booking> findByItemIdInOrderByStartDesc(List<Long> ids);

    List<Booking> findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(List<Long> ids,
                                                                          LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemIdInAndEndBeforeOrderByStartDesc(List<Long> ids, LocalDateTime end);

    List<Booking> findByItemIdInAndStartAfterOrderByStartDesc(List<Long> ids, LocalDateTime start);

    List<Booking> findByItemIdInAndStatusOrderByStartDesc(List<Long> ids, Booking.BookingStatus status);

    Optional<Booking> findFirstByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime end);

    List<Booking> findByItemId(Long itemId);

    List<Booking> findByItemIdInOrderByItemIdAsc(List<Long> ids);
}
