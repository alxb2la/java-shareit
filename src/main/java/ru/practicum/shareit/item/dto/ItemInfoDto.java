package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.Booking;

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
    List<CommentPartialDto> comments;
    BookingShortDto lastBooking;
    BookingShortDto nextBooking;

    public record BookingShortDto(long id, LocalDateTime start, LocalDateTime end, Booking.BookingStatus status) {
    }
}
