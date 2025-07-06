package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Data transfer object, используемый для создания объекта ItemRequest.
 * Определены поля, требующие валидации при получении.
 * Для этого используются только объектные типы данных
 */

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder
public class ItemRequestCreateDto {

    @NotBlank
    private String description;
}
