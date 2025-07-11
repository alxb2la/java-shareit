package ru.practicum.shareit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Класс для описания универсального формата ошибки.
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final String error;
    private final String description;
}
