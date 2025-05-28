package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserFullDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Component
public class UserMapper {

    public UserFullDto toUserFullDto(User user) {
        return UserFullDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User toUser(UserCreateDto userCreateDto) {
        return User.builder()
                .id(null)
                .name(userCreateDto.getName())
                .email(userCreateDto.getEmail())
                .build();
    }

    public User toUser(Long userId, UserUpdateDto userUpdateDto) {
        return User.builder()
                .id(userId)
                .name(userUpdateDto.getName())
                .email(userUpdateDto.getEmail())
                .build();
    }
}
