package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Map;

/**
 * ItemClient - класс, наследующий класс BaseClient и использует
 * необходимый набор методов для взаимодействия с REST API.
 * Формирует корректные запросы и обрабатывает ответы от микросервиса shareIt Service
 * по объектам Item, Comment, путь /items, HTTP-запросы - GET, POST, PATCH
 * Для постоения RestTemplate используется реализация от HttpComponents
 */

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addItem(Long userId, ItemCreateDto itemCreateDto) {
        return post("", userId, itemCreateDto);
    }

    public ResponseEntity<Object> updateItem(long userId, Long itemId, ItemUpdateDto itemUpdateDto) {
        return patch("/" + itemId, userId, itemUpdateDto);
    }

    public ResponseEntity<Object> getItemById(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllItemsByOwnerId(Long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> getItemsByTextQuery(Long userId, String text) {
        Map<String, Object> params = Map.of("text", text);
        return get("/search" + "?text={text}", userId, params);
    }

    public ResponseEntity<Object> addComment(Long itemId, Long authorId, CommentCreateDto commentCreateDto) {
        return post("/" + itemId + "/comment", authorId, commentCreateDto);
    }
}
