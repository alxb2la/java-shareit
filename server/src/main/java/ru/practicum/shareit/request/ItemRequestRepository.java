package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Интерфейс, определяющий набор стандартных и дополнительных действий хранения и поиска
 * с объектом типа ItemRequest, используя запросные методы и jpql
 */

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequesterIdOrderByCreatedDesc(Long requesterId);

    List<ItemRequest> findByRequesterIdNotOrderByCreatedDesc(Long requesterId);
}
