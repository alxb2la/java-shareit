package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;


@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping(
        path = "/bookings",
        produces = "application/json"
)
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<Object> addBooking(@RequestBody @Valid BookingCreateDto booking,
                                             @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос на добавление Booking: {}, user ID: {}", booking, userId);
        ResponseEntity<Object> re = bookingClient.addBooking(userId, booking);
        log.info("Успешно добавлен Booking: {}, user ID: {}", re.getBody(), userId);
        return re;
    }

    @PatchMapping(path = "/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(@PathVariable Long bookingId,
                                                      @RequestParam(name = "approved") Boolean approved,
                                                      @RequestHeader(name = "X-Sharer-User-Id") long userId) {
        log.info("Запрос на обновление Booking по ID: {}, user ID: {}", bookingId, userId);
        ResponseEntity<Object> re = bookingClient.updateBookingStatus(userId, bookingId, approved);
        log.info("Успешно обновлен Booking: {}, user ID: {}", re.getBody(), userId);
        return re;
    }

    @GetMapping(path = "/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable Long bookingId,
                                                 @RequestHeader(name = "X-Sharer-User-Id") long userId) {
        log.info("Запрос на получение Booking по ID: {}, user ID: {}", bookingId, userId);
        ResponseEntity<Object> re = bookingClient.getBookingById(userId, bookingId);
        log.info("Успешно получен Booking: {}, user ID: {}", re.getBody(), userId);
        return re;
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserBookings(
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @RequestHeader(name = "X-Sharer-User-Id") long userId) {
        BookingState state = BookingState.getBookingState(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Bad booking state param: " + stateParam));
        log.info("Запрос на получение Bookings пользователя с ID: {}, и параметром state: {}", userId, state);
        ResponseEntity<Object> re = bookingClient.getUserBookings(userId, state);
        log.info("Успешно получены Bookings пользователя с ID: {}, и параметром state: {}", userId, state);
        return re;
    }

    @GetMapping(path = "/owner")
    public ResponseEntity<Object> getAllUserItemsBookings(
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @RequestHeader(name = "X-Sharer-User-Id") long userId) {
        BookingState state = BookingState.getBookingState(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Bad booking state param: " + stateParam));
        log.info("Запрос на получение Bookings вещей пользователя с ID: {}, и параметром state: {}", userId, state);
        ResponseEntity<Object> re = bookingClient.getAllUserItemsBookings(userId, state);
        log.info("Успешно получены Bookings вещей пользователя с ID: {}, и параметром state: {}", userId, state);
        return re;
    }
}
