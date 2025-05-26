package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item create(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Collection<Item> findAllByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(i -> i.getOwner().getId().longValue() == ownerId.longValue())
                .toList();
    }

    @Override
    public Collection<Item> findByTextQuery(String textQuery) {
        return items.values().stream()
                .filter(i -> i.getAvailable() && (i.getName().toLowerCase().contains(textQuery.toLowerCase()) ||
                        i.getDescription().toLowerCase().contains(textQuery.toLowerCase())))
                .toList();
    }

    private long generateId() {
        long prevId = items.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
        return ++prevId;
    }
}
