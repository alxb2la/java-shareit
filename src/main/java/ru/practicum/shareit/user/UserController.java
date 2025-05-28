package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserFullDto addUser(@RequestBody @Valid UserCreateDto user) {
        log.info("Запрос на добавление User: {}", user);
        UserFullDto userFullDto = userService.addUser(user);
        log.info("Успешно добавлен User: {}", userFullDto);
        return userFullDto;
    }

    @PatchMapping("/{userId}")
    public UserFullDto updateUser(@PathVariable Long userId, @RequestBody @Valid UserUpdateDto user) {
        log.info("Запрос на обновление User: {}", user);
        UserFullDto userFullDto = userService.updateUser(userId, user);
        log.info("Успешно обновлен User: {}", userFullDto);
        return userFullDto;
    }

    @DeleteMapping("/{userId}")
    public void removeUserById(@PathVariable Long userId) {
        log.info("Запрос на удаление User по id: {}", userId);
        userService.removeUserById(userId);
        log.info("Успешно удален User с id: {}", userId);
    }

    @GetMapping("/{userId}")
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
