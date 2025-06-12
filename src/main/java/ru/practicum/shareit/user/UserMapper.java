package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserFullDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

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

    public static User toUser(Long userId, UserUpdateDto userUpdateDto) {
        return User.builder()
                .id(userId)
                .name(userUpdateDto.getName())
                .email(userUpdateDto.getEmail())
                .build();
    }
}
