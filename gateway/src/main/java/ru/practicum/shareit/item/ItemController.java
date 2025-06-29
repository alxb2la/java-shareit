package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import java.util.List;


@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestBody @Valid ItemCreateDto item,
                                          @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос на добавление Item, User ID: {}", userId);
        ResponseEntity<Object> re = itemClient.addItem(userId, item);
        log.info("Успешно добавлен Item: {}", re.getBody());
        return re;
    }

    @PatchMapping(path = "/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId, @RequestBody ItemUpdateDto item,
                                             @RequestHeader(name = "X-Sharer-User-Id") long userId) {
        log.info("Запрос на обновление Item c ID: {}", itemId);
        ResponseEntity<Object> re = itemClient.updateItem(userId, itemId, item);
        log.info("Успешно обновлен Item: {}", re.getBody());
        return re;
    }

    @GetMapping(path = "/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId,
                                              @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение Item по ID: {}, user ID: {}", itemId, userId);
        ResponseEntity<Object> re = itemClient.getItemById(userId, itemId);
        log.info("Успешно получен Item: {}", re.getBody());
        return re;
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwnerId(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId) {
        log.info("Запрос на получение списка всех Items по owner ID: {}", ownerId);
        ResponseEntity<Object> re = itemClient.getAllItemsByOwnerId(ownerId);
        log.info("Успешно получен список всех Items по owner ID: {}", ownerId);
        return re;
    }

    @GetMapping(path = "/search")
    public ResponseEntity<Object> getItemsByTextQuery(@RequestParam(name = "text") String text,
                                                      @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение списка Items по запросу: {}", text);
        if (text.isBlank()) {
            return new ResponseEntity<>(List.of(), HttpStatus.OK);
        }
        ResponseEntity<Object> re = itemClient.getItemsByTextQuery(userId, text);
        log.info("Успешно получен список Items по запросу: {}", text);
        return re;
    }

    @PostMapping(path = "/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @RequestBody @Valid CommentCreateDto comment,
                                             @RequestHeader(name = "X-Sharer-User-Id") Long authorId) {
        log.info("Запрос на добавление Comment. Item ID: {}, author ID: {}", itemId, authorId);
        ResponseEntity<Object> re = itemClient.addComment(itemId, authorId, comment);
        log.info("Успешно добавлен Comment: {}", re.getBody());
        return re;
    }
}
