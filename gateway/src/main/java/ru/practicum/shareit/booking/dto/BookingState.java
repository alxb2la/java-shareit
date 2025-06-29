package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<BookingState> getBookingState(String stateParam) {
        try {
            return Optional.of(BookingState.valueOf(stateParam.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
