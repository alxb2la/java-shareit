package ru.practicum.shareit.user.dto;

import lombok.*;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder
public class UserUpdateDto {
    private Long id;
    private String name;
    private String email;
}
