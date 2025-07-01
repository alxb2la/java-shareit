package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.*;

/**
 * Data transfer object, используемый для обновления объекта User.
 * Определены поля, требующие валидации при получении.
 * Null - поля при получении не требуют обновления
 * Для этого используются только объектные типы данных
 */

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder
public class UserUpdateDto {
    private Long id;
    private String name;

    @Email
    private String email;
}
