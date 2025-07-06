package ru.practicum.shareit.user.dto;

import lombok.*;

/**
 * Data transfer object, используемый для обновления объекта User.
 * Null - поля при получении не требуют обновления
 * Для этого используются только объектные типы данных
 */

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder
public class UserUpdateDto {
    private Long id;
    private String name;
    private String email;
}
