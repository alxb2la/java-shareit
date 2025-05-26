package ru.practicum.shareit.user.dto;

import lombok.*;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder
public class UserFullDto {
    private long id;
    private String name;
    private String email;
}
