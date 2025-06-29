package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@Builder
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
