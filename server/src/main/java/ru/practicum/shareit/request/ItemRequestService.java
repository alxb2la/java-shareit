package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;

import java.util.Collection;

/**
 * Интерфейс, определяющий набор методов для связи контроллера ItemRequest и репозитория ItemRequest,
 * и реализующий бизнес логику.
 */

public interface ItemRequestService {

    ItemRequestShortDto addItemRequest(Long userId, ItemRequestCreateDto itemRequest);

    Collection<ItemRequestDto> getAllItemRequestsByOwnerId(Long userId);

    Collection<ItemRequestShortDto> getAllItemRequestsOfOtherUsers(Long userId);

    ItemRequestDto getItemRequestById(Long userId, Long requestId);
}
