package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;

import java.util.Collection;

/**
 * ItemRequestController — класс-контроллер, предоставляющий REST API для работы с данными типа ItemRequest.
 * Базовый путь - /requests.
 * Обмен данными осуществляется с микросервисом shareIt Gateway
 */

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(
        path = "/requests",
        produces = "application/json"
)
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestShortDto addItemRequest(@RequestBody ItemRequestCreateDto itemRequest,
                                              @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос на добавление ItemRequest: {}, user ID: {}", itemRequest, userId);
        ItemRequestShortDto itemRequestShortDto = itemRequestService.addItemRequest(userId, itemRequest);
        log.info("Успешно добавлен ItemRequest: {}, user ID: {}", itemRequestShortDto, userId);
        return itemRequestShortDto;
    }

    @GetMapping
    public Collection<ItemRequestDto> getAllItemRequestsByOwnerId(
            @RequestHeader(name = "X-Sharer-User-Id") Long userId
    ) {
        log.info("Запрос на получение ItemRequests пользователя с ID: {}", userId);
        Collection<ItemRequestDto> itemRequests = itemRequestService.getAllItemRequestsByOwnerId(userId);
        log.info("Успешно получены ItemRequests пользователя с ID: {}", userId);
        return itemRequests;
    }

    @GetMapping(path = "/all")
    public Collection<ItemRequestShortDto> getAllItemRequestsOfOtherUsers(
            @RequestHeader(name = "X-Sharer-User-Id") Long userId
    ) {
        log.info("Запрос на получение всех ItemRequests кроме пользователя с ID: {}", userId);
        Collection<ItemRequestShortDto> itemRequests = itemRequestService.getAllItemRequestsOfOtherUsers(userId);
        log.info("Успешно получены все ItemRequests кроме пользователя с ID: {}", userId);
        return itemRequests;
    }

    @GetMapping(path = "/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable Long requestId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение ItemRequest по ID: {}, user ID: {}", requestId, userId);
        ItemRequestDto itemRequestDto = itemRequestService.getItemRequestById(userId, requestId);
        log.info("Успешно получен ItemRequest с ID: {}, user ID: {}", requestId, userId);
        return itemRequestDto;
    }
}
