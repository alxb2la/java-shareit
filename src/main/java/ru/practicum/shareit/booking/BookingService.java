package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingPartialDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.Collection;

public interface BookingService {

    BookingPartialDto addBooking(Long userId, BookingCreateDto booking);

    BookingPartialDto updateBookingStatus(Long userId, Long bookingId, Boolean approved);

    BookingPartialDto getBookingById(Long userId, Long bookingId);

    Collection<BookingPartialDto> getUserBookings(Long userId, BookingState state);

    Collection<BookingPartialDto> getAllUserItemsBookings(Long userId, BookingState state);
}
