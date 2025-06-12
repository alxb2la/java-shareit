package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Интерфейс, определяющий набор стандартных и дополнительных действий хранения и поиска
 * с объектом типа Comment, используя запросные методы
 */

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByItemId(Long itemId);

    @EntityGraph("comment.item")
    List<Comment> findByItemIdInOrderByItemIdAsc(List<Long> ids);
}
