package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Data transfer object, используемый для обновления объекта Item.
 * Null - поля при получении не требуют обновления
 * Для этого используются только объектные типы данных
 */

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder
public class ItemUpdateDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
}
