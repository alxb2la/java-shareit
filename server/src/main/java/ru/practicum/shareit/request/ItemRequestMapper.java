package ru.practicum.shareit.request;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Утилитный класс для взаимного преобразования объектов entity ItemRequest и
 * dto объектов ItemRequestCreateDto, ItemRequestShortDto, ItemRequestDto
 */

public final class ItemRequestMapper {

    private ItemRequestMapper() {
        throw new UnsupportedOperationException();
    }

    public static ItemRequestShortDto toItemRequestShortDto(ItemRequest itemRequest) {
        return ItemRequestShortDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<Item> allItems) {
        List<Item> items = allItems.stream()
                .filter(item -> item.getRequest().getId().equals(itemRequest.getId()))
                .toList();

        List<ItemRequestDto.ItemShortDto> itemShortDtos = new ArrayList<>();
        for (Item item : items) {
            ItemRequestDto.ItemShortDto itemShortDto = new ItemRequestDto.ItemShortDto(
                    item.getId(), item.getName(), item.getOwner().getId()
            );
            itemShortDtos.add(itemShortDto);
        }

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemShortDtos)
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestCreateDto itemRequestCreateDto, User requester) {
        return ItemRequest.builder()
                .id(null)
                .description(itemRequestCreateDto.getDescription())
                .created(LocalDateTime.now())
                .requester(requester)
                .build();
    }
}
