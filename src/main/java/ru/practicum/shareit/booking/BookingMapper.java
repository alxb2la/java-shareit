package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingPartialDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

public final class BookingMapper {

    private BookingMapper() {
        throw new UnsupportedOperationException();
    }

    public static BookingPartialDto toBookingPartialDto(Booking booking) {
        BookingPartialDto.ItemShortDto item = new BookingPartialDto.ItemShortDto(booking.getItem().getId(),
                booking.getItem().getName());
        BookingPartialDto.UserShortDto booker = new BookingPartialDto.UserShortDto(booking.getBooker().getId(),
                booking.getBooker().getName());

        return BookingPartialDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(item)
                .booker(booker)
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingCreateDto bookingCreateDto, Item item, User booker) {
        return Booking.builder()
                .id(null)
                .start(bookingCreateDto.getStart())
                .end(bookingCreateDto.getEnd())
                .item(item)
                .booker(booker)
                .status(bookingCreateDto.getStatus())
                .build();
    }
}
