package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Override
    @EntityGraph("item.owner")
    Optional<Item> findById(Long id);

    List<Item> findByOwnerIdOrderByIdAsc(Long ownerId);

    @Query("SELECT it " +
            "FROM Item as it " +
            "WHERE it.available = true " +
            "AND ((lower(it.name) LIKE lower(concat('%', :text, '%'))) " +
            "OR (lower(it.description) LIKE lower(concat('%', :text, '%'))))"
    )
    List<Item> findByTextQuery(@Param("text") String textQuery);
}
