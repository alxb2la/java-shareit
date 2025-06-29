package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;


@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping(
        path = "/requests",
        produces = "application/json"
)
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<Object> addItemRequest(@RequestBody @Valid ItemRequestCreateDto itemRequest,
                                                 @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос на добавление ItemRequest: {}, user ID: {}", itemRequest, userId);
        ResponseEntity<Object> re = itemRequestClient.addItemRequest(userId, itemRequest);
        log.info("Успешно добавлен ItemRequest: {}, user ID: {}", re.getBody(), userId);
        return re;
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemRequestsByOwnerId(
            @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение ItemRequests пользователя с ID: {}", userId);
        ResponseEntity<Object> re = itemRequestClient.getAllItemRequestsByOwnerId(userId);
        log.info("Успешно получены ItemRequests пользователя с ID: {}", userId);
        return re;
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Object> getAllItemRequestsOfOtherUsers(
            @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение всех ItemRequests кроме пользователя с ID: {}", userId);
        ResponseEntity<Object> re = itemRequestClient.getAllItemRequestsOfOtherUsers(userId);
        log.info("Успешно получены все ItemRequests кроме пользователя с ID: {}", userId);
        return re;
    }

    @GetMapping(path = "/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable Long requestId,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение ItemRequest по ID: {}, user ID: {}", requestId, userId);
        ResponseEntity<Object> re = itemRequestClient.getItemRequestById(userId, requestId);
        log.info("Успешно получен ItemRequest: {}, user ID: {}", re.getBody(), userId);
        return re;
    }
}
