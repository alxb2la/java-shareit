package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserFullDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Collection;

public interface UserService {

    UserFullDto addUser(UserCreateDto user);

    UserFullDto updateUser(Long userId, UserUpdateDto user);

    void removeUserById(Long userId);

    UserFullDto getUserById(Long userId);

    Collection<UserFullDto> getAllUsers();
}
