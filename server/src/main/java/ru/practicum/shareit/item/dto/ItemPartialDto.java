package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Data transfer object объекта Item, используемый для ответа на запросы.
 * Все поля объекта заполняются полностью, отсутствовать не могут.
 * Частично используются примитивные типы данных для улучшения быстродействия.
 */

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder
public class ItemPartialDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
}
