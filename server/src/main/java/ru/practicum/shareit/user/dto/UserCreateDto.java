package ru.practicum.shareit.user.dto;

import lombok.*;

/**
 * Data transfer object, используемый для создания объекта User.
 * Валидации полей осуществляется в микросервисе shareIt Gateway.
 */

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder
public class UserCreateDto {
    private String name;
    private String email;
}
