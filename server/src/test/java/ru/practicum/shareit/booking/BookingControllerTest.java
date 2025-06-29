package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingPartialDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @MockitoBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    void addBookingTest() {
        Long userId = 23L;
        Long bookingId = 5L;
        Long itemId = 2L;

        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.of(2025, Month.MARCH, 23, 12, 0, 0))
                .end(LocalDateTime.of(2025, Month.APRIL, 1, 12, 0, 0))
                .status(BookingStatus.APPROVED)
                .itemId(itemId)
                .build();

        BookingPartialDto.UserShortDto userShortDto = new BookingPartialDto.UserShortDto(
                userId,
                "booker name"
        );

        BookingPartialDto.ItemShortDto itemShortDto = new BookingPartialDto.ItemShortDto(
                itemId,
                "booking item name "
        );

        BookingPartialDto bookingPartialDto = BookingPartialDto.builder()
                .id(bookingId)
                .start(LocalDateTime.of(2025, Month.MARCH, 23, 12, 0, 0))
                .end(LocalDateTime.of(2025, Month.APRIL, 1, 12, 0, 0))
                .booker(userShortDto)
                .item(itemShortDto)
                .status(BookingStatus.APPROVED)
                .build();

        Mockito
                .when(bookingService.addBooking(eq(userId), any(BookingCreateDto.class)))
                .thenReturn(bookingPartialDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header("X-Sharer-User-Id", Long.toString(userId))
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .contentType("application/json; charset=UTF-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookingPartialDto)));

        Mockito.verify(bookingService, Mockito.times(1))
                .addBooking(eq(userId), any(BookingCreateDto.class));
    }

    @SneakyThrows
    @Test
    void updateBookingStatusTest() {
        Long userId = 23L;
        Long bookingId = 5L;
        Long itemId = 2L;
        boolean approvedParam = true;

        BookingPartialDto.UserShortDto userShortDto = new BookingPartialDto.UserShortDto(
                userId,
                "booker name"
        );

        BookingPartialDto.ItemShortDto itemShortDto = new BookingPartialDto.ItemShortDto(
                itemId,
                "booking item name "
        );

        BookingPartialDto bookingPartialDto = BookingPartialDto.builder()
                .id(bookingId)
                .start(LocalDateTime.of(2025, Month.MARCH, 23, 12, 0, 0))
                .end(LocalDateTime.of(2025, Month.APRIL, 1, 12, 0, 0))
                .booker(userShortDto)
                .item(itemShortDto)
                .status(BookingStatus.APPROVED)
                .build();

        Mockito
                .when(bookingService.updateBookingStatus(eq(userId), eq(bookingId), eq(approvedParam)))
                .thenReturn(bookingPartialDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", Long.toString(userId))
                        .param("approved", String.valueOf(approvedParam))
                        .contentType("application/json; charset=UTF-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookingPartialDto)));

        Mockito.verify(bookingService, Mockito.times(1))
                .updateBookingStatus(userId, bookingId, approvedParam);
    }

    @SneakyThrows
    @Test
    void getBookingByIdTest() {
        Long userId = 15L;
        Long bookingId = 3L;
        Long itemId = 4L;

        BookingPartialDto.UserShortDto userShortDto = new BookingPartialDto.UserShortDto(
                userId,
                "booker name"
        );

        BookingPartialDto.ItemShortDto itemShortDto = new BookingPartialDto.ItemShortDto(
                itemId,
                "booking item name "
        );

        BookingPartialDto bookingPartialDto = BookingPartialDto.builder()
                .id(bookingId)
                .start(LocalDateTime.of(2025, Month.MARCH, 23, 12, 0, 0))
                .end(LocalDateTime.of(2025, Month.APRIL, 1, 12, 0, 0))
                .booker(userShortDto)
                .item(itemShortDto)
                .status(BookingStatus.APPROVED)
                .build();

        Mockito
                .when(bookingService.getBookingById(eq(userId), eq(bookingId)))
                .thenReturn(bookingPartialDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", Long.toString(userId))
                        .contentType("application/json; charset=UTF-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookingPartialDto)));

        Mockito.verify(bookingService, Mockito.times(1))
                .getBookingById(userId, bookingId);
    }

    @SneakyThrows
    @Test
    void getAllUserBookingsTest_whenParamsIsOk_thenReturnedCorrect() {
        Long userId = 12L;
        Long bookingId = 3L;
        Long itemId = 4L;
        BookingState state = BookingState.ALL;

        BookingPartialDto.UserShortDto userShortDto = new BookingPartialDto.UserShortDto(
                userId,
                "booker name"
        );

        BookingPartialDto.ItemShortDto itemShortDto = new BookingPartialDto.ItemShortDto(
                itemId,
                "booking item name "
        );

        BookingPartialDto bookingPartialDto = BookingPartialDto.builder()
                .id(bookingId)
                .start(LocalDateTime.of(2025, Month.MARCH, 23, 12, 0, 0))
                .end(LocalDateTime.of(2025, Month.APRIL, 1, 12, 0, 0))
                .booker(userShortDto)
                .item(itemShortDto)
                .status(BookingStatus.APPROVED)
                .build();

        Mockito
                .when(bookingService.getUserBookings(eq(userId), eq(state)))
                .thenReturn(List.of(bookingPartialDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", Long.toString(userId))
                        .param("state", String.valueOf(state))
                        .contentType("application/json; charset=UTF-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(objectMapper.writeValueAsString(List.of(bookingPartialDto))));

        Mockito.verify(bookingService, Mockito.times(1))
                .getUserBookings(userId, state);
    }

    @SneakyThrows
    @Test
    void getAllUserBookings_whenStateParamIsWrong_thenThrowsValidationException() {
        Long userId = 4L;
        String wrongState = "Qwerty";

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                                .header("X-Sharer-User-Id", Long.toString(userId))
                                .param("state", wrongState)
                                .contentType("application/json; charset=UTF-8")
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());

        Mockito.verify(bookingService, Mockito.never()).getUserBookings(userId, BookingState.ALL);
    }

    @SneakyThrows
    @Test
    void getAllUserItemsBookingsTest_whenParamsIsOk_thenReturnedCorrect() {
        Long userId = 15L;
        Long bookingId = 3L;
        Long itemId = 4L;
        BookingState state = BookingState.ALL;

        BookingPartialDto.UserShortDto userShortDto = new BookingPartialDto.UserShortDto(
                userId,
                "booker name"
        );

        BookingPartialDto.ItemShortDto itemShortDto = new BookingPartialDto.ItemShortDto(
                itemId,
                "booking item name "
        );

        BookingPartialDto bookingPartialDto = BookingPartialDto.builder()
                .id(bookingId)
                .start(LocalDateTime.of(2025, Month.MARCH, 23, 12, 0, 0))
                .end(LocalDateTime.of(2025, Month.APRIL, 1, 12, 0, 0))
                .booker(userShortDto)
                .item(itemShortDto)
                .status(BookingStatus.APPROVED)
                .build();

        Mockito
                .when(bookingService.getAllUserItemsBookings(eq(userId), eq(state)))
                .thenReturn(List.of(bookingPartialDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", Long.toString(userId))
                        .param("state", String.valueOf(state))
                        .contentType("application/json; charset=UTF-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(objectMapper.writeValueAsString(List.of(bookingPartialDto))));

        Mockito.verify(bookingService, Mockito.times(1))
                .getAllUserItemsBookings(userId, state);
    }

    @SneakyThrows
    @Test
    void getAllUserItemsBookingsTest_whenStateParamIsWrong_thenThrowsValidationException() {
        Long userId = 3L;
        String wrongState = "Qwerty";

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", Long.toString(userId))
                        .param("state", wrongState)
                        .contentType("application/json; charset=UTF-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verify(bookingService, Mockito.never()).getAllUserItemsBookings(userId, BookingState.ALL);
    }
}
