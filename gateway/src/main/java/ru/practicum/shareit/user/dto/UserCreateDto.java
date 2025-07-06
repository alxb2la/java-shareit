package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Data transfer object, используемый для создания объекта User.
 * Определены поля, требующие валидации при получении.
 * Для этого используются только объектные типы данных
 */

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder
public class UserCreateDto {
    @NotBlank
    private String name;

    @NotNull
    @Email
    private String email;
}
