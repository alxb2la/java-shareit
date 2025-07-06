package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

/**
 * Data transfer object, используемый для создания объекта Booking.
 * Определены поля, требующие валидации при получении.
 * Для этого используются только объектные типы данных
 */

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder
public class BookingCreateDto {
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull
    @Future
    private LocalDateTime end;

    @NotNull
    private Long itemId;

    private BookingStatus status;


    @AssertTrue
    boolean isStartBeforeEnd() {
        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }
}
