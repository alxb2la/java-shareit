package ru.practicum.shareit.user;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Интерфейс, определяющий набор стандартных и дополнительных действий хранения и поиска
 * с объектом типа Booking, используя методы интерфейса ListCrudRepository
 */

@Repository
public interface UserRepository extends ListCrudRepository<User, Long> {
}
