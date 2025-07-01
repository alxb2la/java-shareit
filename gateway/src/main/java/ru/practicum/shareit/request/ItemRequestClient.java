package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

/**
 * ItemRequestClient - класс, наследующий класс BaseClient и использует
 * необходимый набор методов для взаимодействия с REST API.
 * Формирует корректные запросы и обрабатывает ответы от микросервиса shareIt Service
 * по объекту ItemRequest, путь /requests, HTTP-запросы - GET, POST
 * Для постоения RestTemplate используется реализация от HttpComponents
 */

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addItemRequest(Long userId, ItemRequestCreateDto itemRequestDto) {
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> getAllItemRequestsByOwnerId(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllItemRequestsOfOtherUsers(Long userId) {
        return get("/all", userId);
    }

    public ResponseEntity<Object> getItemRequestById(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }
}
