package ru.practicum.shareit.booking;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

/**
 * BookingClient - класс, наследующий класс BaseClient и использует
 * необходимый набор методов для взаимодействия с REST API.
 * Формирует корректные запросы и обрабатывает ответы от микросервиса shareIt Service
 * по объекту Booking, путь /bookings, HTTP-запросы - GET, POST, PATCH
 * Для постоения RestTemplate используется реализация от HttpComponents
 */

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addBooking(Long userId, BookingCreateDto bookingCreateDto) {
        return post("", userId, bookingCreateDto);
    }

    public ResponseEntity<Object> updateBookingStatus(long userId, Long bookingId, Boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, userId);
    }

    public ResponseEntity<Object> getBookingById(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getUserBookings(Long userId, BookingState state) {
        Map<String, Object> parameters = Map.of("state", state.name());
        return get("", userId, parameters);
    }

    public ResponseEntity<Object> getAllUserItemsBookings(Long userId, BookingState state) {
        Map<String, Object> params = Map.of("state", state.name());
        return get("/owner", userId, params);
    }
}
