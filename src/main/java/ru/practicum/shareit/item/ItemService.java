package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemPartialDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;

public interface ItemService {

    ItemPartialDto addItem(Long userId, ItemCreateDto item);

    ItemPartialDto updateItem(Long userId, ItemUpdateDto item);

    ItemPartialDto getItemById(Long itemId);

    Collection<ItemPartialDto> getAllItemsByOwnerId(Long ownerId);

    Collection<ItemPartialDto> getItemsByTextQuery(String textQuery);
}
