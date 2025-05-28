package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemPartialDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;

/**
 * ItemController — класс-контроллер, предоставляющий REST API для работы с данными типа Item.
 * Базовый путь - /items.
 */

@RestController
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemPartialDto addItem(@RequestBody @Valid ItemCreateDto item,
                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на добавление Item: {}", item);
        ItemPartialDto itemPartialDto = itemService.addItem(userId, item);
        log.info("Успешно добавлен Item: {}", itemPartialDto);
        return itemPartialDto;
    }

    @PatchMapping("/{itemId}")
    public ItemPartialDto updateItem(@PathVariable Long itemId, @RequestBody ItemUpdateDto item,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на обновление Item: {}", item);
        item.setId(itemId);
        ItemPartialDto itemPartialDto = itemService.updateItem(userId, item);
        log.info("Успешно обновлен Item: {}", itemPartialDto);
        return itemPartialDto;
    }

    @GetMapping("/{itemId}")
    public ItemPartialDto getItemById(@PathVariable Long itemId) {
        log.info("Запрос на получение Item по id: {}", itemId);
        ItemPartialDto itemPartialDto = itemService.getItemById(itemId);
        log.info("Успешно получен Item с id: {}", itemId);
        return itemPartialDto;
    }

    @GetMapping
    public Collection<ItemPartialDto> getAllItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Запрос на получение списка всех Items по owner id: {}", ownerId);
        Collection<ItemPartialDto> items = itemService.getAllItemsByOwnerId(ownerId);
        log.info("Успешно получен список всех Items с owner id: {}", ownerId);
        return items;
    }

    @GetMapping("/search")
    public Collection<ItemPartialDto> getItemsByTextQuery(@RequestParam String text) {
        log.info("Запрос на получение списка Items по запросу: {}", text);
        Collection<ItemPartialDto> items = itemService.getItemsByTextQuery(text);
        log.info("Успешно получен список Items по запросу: {}", text);
        return items;
    }
}
