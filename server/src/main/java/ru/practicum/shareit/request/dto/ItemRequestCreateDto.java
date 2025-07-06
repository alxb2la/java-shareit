package ru.practicum.shareit.request.dto;

import lombok.*;

/**
 * Data transfer object, используемый для создания объекта ItemRequest.
 * Валидации полей осуществляется в микросервисе shareIt Gateway.
 */

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder
public class ItemRequestCreateDto {

    private String description;
}
