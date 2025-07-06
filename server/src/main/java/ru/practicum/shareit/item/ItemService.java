package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

/**
 * Интерфейс, определяющий набор методов для связи контроллера Item и репозитория Item,
 * и реализующий бизнес логику.
 */

public interface ItemService {

    ItemPartialDto addItem(Long userId, ItemCreateDto item);

    ItemPartialDto updateItem(Long userId, ItemUpdateDto item);

    ItemInfoDto getItemById(Long userId, Long itemId);

    Collection<ItemInfoDto> getAllItemsByOwnerId(Long ownerId);

    Collection<ItemPartialDto> getItemsByTextQuery(String textQuery);

    CommentPartialDto addComment(Long itemId, Long authorId, CommentCreateDto comment);
}
