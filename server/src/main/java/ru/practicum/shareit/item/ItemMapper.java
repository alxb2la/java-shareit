package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public final class ItemMapper {

    private ItemMapper() {
        throw new UnsupportedOperationException();
    }

    public static ItemPartialDto toItemPartialDto(Item item) {
        return ItemPartialDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item toItem(ItemCreateDto itemCreateDto, User owner, ItemRequest itemRequest) {
        return Item.builder()
                .id(null)
                .name(itemCreateDto.getName())
                .description(itemCreateDto.getDescription())
                .available(itemCreateDto.getAvailable())
                .owner(owner)
                .request(itemRequest)
                .build();
    }

    public static ItemInfoDto toItemInfoDto(Item item, List<Booking> bookings, List<CommentPartialDto> comments) {
        ItemInfoDto.BookingShortDto lastBookingDto;
        ItemInfoDto.BookingShortDto nextBookingDto;
        if (bookings == null || bookings.isEmpty()) {
            lastBookingDto = null;
            nextBookingDto = null;
        } else {
            Booking lastBooking = getLastBooking(bookings);
            if (lastBooking == null) {
                lastBookingDto = null;
            } else {
                lastBookingDto = new ItemInfoDto.BookingShortDto(lastBooking.getId(), lastBooking.getStart(),
                        lastBooking.getEnd(), lastBooking.getStatus());
            }

            Booking nextBooking = getNextBooking(bookings);
            if (nextBooking == null) {
                nextBookingDto = null;
            } else {
                nextBookingDto = new ItemInfoDto.BookingShortDto(nextBooking.getId(), nextBooking.getStart(),
                        nextBooking.getEnd(), nextBooking.getStatus());
            }
        }

        return ItemInfoDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(comments)
                .lastBooking(lastBookingDto)
                .nextBooking(nextBookingDto)
                .build();
    }

    private static Booking getLastBooking(List<Booking> bookings) {
        return bookings.stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getStart, LocalDateTime::compareTo))
                .orElse(null);
    }

    private static Booking getNextBooking(List<Booking> bookings) {
        return bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart, LocalDateTime::compareTo))
                .orElse(null);
    }
}
