package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserFullDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Collection;

/**
 * UserController — класс-контроллер, предоставляющий REST API для работы с данными типа User.
 * Базовый путь - /users.
 */

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(
        path = "/users",
        produces = "application/json"
)
public class UserController {
    private final UserService userService;

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public UserFullDto addUser(@RequestBody UserCreateDto user) {
        log.info("Запрос на добавление User");
        UserFullDto userFullDto = userService.addUser(user);
        log.info("Успешно добавлен User c ID: {}", userFullDto.getId());
        return userFullDto;
    }

    @PatchMapping(
            path = "/{userId}",
            consumes = "application/json"
    )
    public UserFullDto updateUser(@PathVariable Long userId, @RequestBody UserUpdateDto user) {
        log.info("Запрос на обновление User c ID: {}", user.getId());
        UserFullDto userFullDto = userService.updateUser(userId, user);
        log.info("Успешно обновлен User c ID: {}", userFullDto.getId());
        return userFullDto;
    }

    @DeleteMapping(path = "/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUserById(@PathVariable Long userId) {
        log.info("Запрос на удаление User по id: {}", userId);
        userService.removeUserById(userId);
        log.info("Успешно удален User с id: {}", userId);
    }

    @GetMapping(path = "/{userId}")
    public UserFullDto getUserById(@PathVariable Long userId) {
        log.info("Запрос на получение User по id: {}", userId);
        UserFullDto userFullDto = userService.getUserById(userId);
        log.info("Успешно получен User с id: {}", userId);
        return userFullDto;
    }

    @GetMapping
    public Collection<UserFullDto> getAllUsers() {
        log.info("Запрос на получение списка всех пользователей");
        Collection<UserFullDto> users = userService.getAllUsers();
        log.info("Список всех пользователей успешно сформирован");
        return users;
    }
}
