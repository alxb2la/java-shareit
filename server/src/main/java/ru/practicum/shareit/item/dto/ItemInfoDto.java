package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data transfer object объекта Item, используемый для ответа на запросы.
 * Все поля объекта заполняются, за исключением полей lastBooking и nextBooking,
 * в зависимости от бизнес-логики и запроса.
 * Частично используются примитивные типы данных для улучшения быстродействия.
 */

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder
@Data
public class ItemInfoDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private List<CommentPartialDto> comments;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;

    public record BookingShortDto(long id, LocalDateTime start, LocalDateTime end, BookingStatus status) {
    }
}
