package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

/**
 * Data transfer object объекта Booking, используемый для ответа на запросы.
 * Все поля объекта заполняются полностью, отсутствовать не могут.
 * Частично используются примитивные типы данных для улучшения быстродействия.
 */

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder
public class BookingPartialDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemShortDto item;
    private UserShortDto booker;
    private BookingStatus status;

    public record ItemShortDto(long id, String name) {
    }

    public record UserShortDto(long id, String name) {
    }
}
