package ru.practicum.shareit.user.dto;

import lombok.*;

/**
 * Data transfer object объекта User, используемый для ответа на запросы.
 * Все поля объекта заполняются полностью, отсутствовать не могут.
 * Частично используются примитивные типы данных для улучшения быстродействия.
 */

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder
public class UserFullDto {
    private long id;
    private String name;
    private String email;
}
