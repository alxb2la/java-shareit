package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserFullDto;

/**
 * Утилитный класс для взаимного преобразования объектов entity User и
 * dto объектов UserCreateDto, UserFullDto
 */

public final class UserMapper {

    private UserMapper() {
        throw new UnsupportedOperationException();
    }

    public static UserFullDto toUserFullDto(User user) {
        return UserFullDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(UserCreateDto userCreateDto) {
        return User.builder()
                .id(null)
                .name(userCreateDto.getName())
                .email(userCreateDto.getEmail())
                .build();
    }
}
