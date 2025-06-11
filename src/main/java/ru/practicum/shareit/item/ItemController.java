package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

/**
 * ItemController — класс-контроллер, предоставляющий REST API для работы с данными типа Item и Comment.
 * Базовый путь - /items.
 */

@RestController
@Slf4j
@RequestMapping(
        path = "/items",
        produces = "application/json"
)
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemPartialDto addItem(@RequestBody @Valid ItemCreateDto item,
                                  @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос на добавление Item");
        ItemPartialDto itemPartialDto = itemService.addItem(userId, item);
        log.info("Успешно добавлен Item c ID: {}", itemPartialDto.getId());
        return itemPartialDto;
    }

    @PatchMapping(
            path = "/{itemId}",
            consumes = "application/json"
    )
    public ItemPartialDto updateItem(@PathVariable Long itemId, @RequestBody ItemUpdateDto item,
                                     @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос на обновление Item c ID: {}", item.getId());
        item.setId(itemId);
        ItemPartialDto itemPartialDto = itemService.updateItem(userId, item);
        log.info("Успешно обновлен Item c ID: {}", itemPartialDto.getId());
        return itemPartialDto;
    }

    @GetMapping(path = "/{itemId}")
    public ItemInfoDto getItemById(@PathVariable Long itemId,
                                   @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение Item по ID: {}, user ID: {}", itemId, userId);
        ItemInfoDto itemInfoDto = itemService.getItemById(userId, itemId);
        log.info("Успешно получен Item с ID: {}", itemId);
        return itemInfoDto;
    }

    @GetMapping
    public Collection<ItemInfoDto> getAllItemsByOwnerId(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId) {
        log.info("Запрос на получение списка всех Items по owner ID: {}", ownerId);
        Collection<ItemInfoDto> items = itemService.getAllItemsByOwnerId(ownerId);
        log.info("Успешно получен список всех Items с owner ID: {}", ownerId);
        return items;
    }

    @GetMapping(path = "/search")
    public Collection<ItemPartialDto> getItemsByTextQuery(@RequestParam(name = "text") String text) {
        log.info("Запрос на получение списка Items по запросу: {}", text);
        Collection<ItemPartialDto> items = itemService.getItemsByTextQuery(text);
        log.info("Успешно получен список Items по запросу: {}", text);
        return items;
    }

    @PostMapping(
            path = "/{itemId}/comment",
            consumes = "application/json"
    )
    @ResponseStatus(HttpStatus.CREATED)
    public CommentPartialDto addComment(@PathVariable Long itemId,
                                        @RequestBody @Valid CommentCreateDto comment,
                                        @RequestHeader(name = "X-Sharer-User-Id") Long authorId) {
        log.info("Запрос на добавление Comment. Item ID: {}, author ID: {}", itemId, authorId);
        CommentPartialDto commentPartialDto = itemService.addComment(itemId, authorId, comment);
        log.info("Успешно добавлен Comment c ID: {}", commentPartialDto.getId());
        return commentPartialDto;
    }
}
