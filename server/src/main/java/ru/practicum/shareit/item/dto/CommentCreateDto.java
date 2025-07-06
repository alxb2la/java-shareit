package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Data transfer object, используемый для создания объекта Comment.
 * Валидации полей осуществляется в микросервисе shareIt Gateway.
 */

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder
public class CommentCreateDto {
    private String text;
}
