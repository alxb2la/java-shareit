package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;


@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping(
        path = "/users",
        produces = "application/json"
)
public class UserController {
    private final UserClient userClient;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<Object> addUser(@RequestBody @Valid UserCreateDto user) {
        log.info("Запрос на добавление User");
        ResponseEntity<Object> re = userClient.addUser(user);
        log.info("Успешно добавлен User");
        return re;
    }

    @PatchMapping(
            path = "/{userId}",
            consumes = "application/json"
    )
    public ResponseEntity<Object> updateUser(@PathVariable Long userId, @RequestBody @Valid UserUpdateDto user) {
        log.info("Запрос на обновление User c ID: {}", userId);
        ResponseEntity<Object> re = userClient.updateUser(userId, user);
        log.info("Успешно обновлен User c ID: {}", userId);
        return re;
    }

    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<Object> removeUserById(@PathVariable Long userId) {
        log.info("Запрос на удаление User по id: {}", userId);
        ResponseEntity<Object> re = userClient.removeUserById(userId);
        log.info("Успешно удален User с id: {}", userId);
        return re;
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Запрос на получение User по id: {}", userId);
        ResponseEntity<Object> re = userClient.getUserById(userId);
        log.info("Успешно получен User с id: {}", userId);
        return re;
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Запрос на получение списка всех пользователей");
        ResponseEntity<Object> re = userClient.getAllUsers();
        log.info("Список всех пользователей успешно сформирован");
        return re;
    }
}
