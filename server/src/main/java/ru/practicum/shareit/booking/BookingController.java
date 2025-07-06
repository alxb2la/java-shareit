package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingPartialDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Collection;

/**
 * BookingController — класс-контроллер, предоставляющий REST API для работы с данными типа Booking.
 * Базовый путь - /bookings.
 * Обмен данными осуществляется с микросервисом shareIt Gateway
 */

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(
        path = "/bookings",
        produces = "application/json"
)
public class BookingController {
    private final BookingService bookingService;

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingPartialDto addBooking(@RequestBody BookingCreateDto booking,
                                        @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос на добавление Booking: {}, user ID: {}", booking, userId);
        BookingPartialDto bookingPartialDto = bookingService.addBooking(userId, booking);
        log.info("Успешно добавлен Booking: {}, user ID: {}", bookingPartialDto, userId);
        return bookingPartialDto;
    }

    @PatchMapping(path = "/{bookingId}")
    public BookingPartialDto updateBookingStatus(@PathVariable Long bookingId,
                                                 @RequestParam(name = "approved") Boolean approved,
                                                 @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос на обновление Booking по ID: {}, user ID: {}", bookingId, userId);
        BookingPartialDto bookingPartialDto = bookingService.updateBookingStatus(userId, bookingId, approved);
        log.info("Успешно обновлен Booking c ID: {}, user ID: {}", bookingPartialDto.getId(), userId);
        return bookingPartialDto;
    }

    @GetMapping(path = "/{bookingId}")
    public BookingPartialDto getBookingById(@PathVariable Long bookingId,
                                            @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение Booking по ID: {}, user ID: {}", bookingId, userId);
        BookingPartialDto bookingPartialDto = bookingService.getBookingById(userId, bookingId);
        log.info("Успешно получен Booking с ID: {}, user ID: {}", bookingId, userId);
        return bookingPartialDto;
    }

    @GetMapping
    public Collection<BookingPartialDto> getAllUserBookings(
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        BookingState state = BookingState.getBookingState(stateParam)
                .orElseThrow(() -> new ValidationException("Bad booking state param: " + stateParam));
        log.info("Запрос на получение Bookings пользователя с ID: {}, и параметром state: {}", userId, state);
        Collection<BookingPartialDto> bookings = bookingService.getUserBookings(userId, state);
        log.info("Успешно получены Bookings пользователя с ID: {}, и параметром state: {}", userId, state);
        return bookings;
    }

    @GetMapping(path = "/owner")
    public Collection<BookingPartialDto> getAllUserItemsBookings(
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        BookingState state = BookingState.getBookingState(stateParam)
                .orElseThrow(() -> new ValidationException("Bad booking state param: " + stateParam));
        log.info("Запрос на получение Bookings вещей пользователя с ID: {}, и параметром state: {}", userId, state);
        Collection<BookingPartialDto> bookings = bookingService.getAllUserItemsBookings(userId, state);
        log.info("Успешно получены Bookings вещей пользователя с ID: {}, и параметром state: {}", userId, state);
        return bookings;
    }
}
