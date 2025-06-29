package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс, определяющий набор стандартных и дополнительных действий хранения и поиска
 * с объектом типа Item, используя запросные методы и jpql
 */

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Override
    @EntityGraph(attributePaths = {"owner"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Item> findById(Long id);

    List<Item> findByOwnerIdOrderByIdAsc(Long ownerId);

    @Query("SELECT it " +
            "FROM Item as it " +
            "WHERE it.available = true " +
            "AND ((lower(it.name) LIKE lower(concat('%', :text, '%'))) " +
            "OR (lower(it.description) LIKE lower(concat('%', :text, '%'))))"
    )
    List<Item> findByTextQuery(@Param("text") String textQuery);

    @EntityGraph(attributePaths = {"owner", "request"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Item> findByRequestIdIn(List<Long> ids);
}
