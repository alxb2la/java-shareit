package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Data transfer object объекта Comment, используемый для ответа на запросы.
 * Все поля объекта заполняются полностью, отсутствовать не могут.
 * Частично используются примитивные типы данных для улучшения быстродействия.
 */

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder
public class CommentPartialDto {
    private long id;
    private String text;
    private LocalDateTime created;
    private String authorName;
}
