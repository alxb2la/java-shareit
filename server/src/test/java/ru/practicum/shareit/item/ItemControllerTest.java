package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.*;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @MockitoBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @SneakyThrows
    @Test
    void addItemTest() {
        Long userId = 1L;
        Long itemRequestId = 2L;
        Long itemId = 1L;

        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("Item")
                .description("ItemDescription")
                .available(true)
                .requestId(itemRequestId)
                .build();

        ItemPartialDto itemPartialDto = ItemPartialDto.builder()
                .id(itemId)
                .name("Item")
                .description("ItemDescription")
                .available(true)
                .build();

        Mockito
                .when(itemService.addItem(eq(userId), any(ItemCreateDto.class)))
                .thenReturn(itemPartialDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", Long.toString(userId))
                        .content(objectMapper.writeValueAsString(itemCreateDto))
                        .contentType("application/json; charset=UTF-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemPartialDto)));

        Mockito.verify(itemService, Mockito.times(1))
                .addItem(eq(userId), any(ItemCreateDto.class));
    }

    @SneakyThrows
    @Test
    void updateItemTest() {
        Long userId = 1L;
        Long itemId = 3L;

        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .id(itemId)
                .name("Item")
                .description("ItemDescription")
                .available(true)
                .build();

        ItemPartialDto itemPartialDto = ItemPartialDto.builder()
                .id(itemId)
                .name("Item")
                .description("ItemDescription")
                .available(true)
                .build();

        Mockito
                .when(itemService.updateItem(eq(userId), any(ItemUpdateDto.class)))
                .thenReturn(itemPartialDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", Long.toString(userId))
                        .content(objectMapper.writeValueAsString(itemUpdateDto))
                        .contentType("application/json; charset=UTF-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemPartialDto)));

        Mockito.verify(itemService, Mockito.times(1))
                .updateItem(eq(userId), any(ItemUpdateDto.class));
    }

    @SneakyThrows
    @Test
    void getItemByIdTest() {
        Long userId = 2L;
        Long itemId = 4L;
        Long commentId = 1L;
        long bookingLastId = 1L;
        long bookingNextId = 2L;

        CommentPartialDto commentPartialDto = CommentPartialDto.builder()
                .id(commentId)
                .text("comment text")
                .created(LocalDateTime.of(2025, Month.MAY, 10, 12, 0, 0))
                .authorName("authorName")
                .build();

        ItemInfoDto.BookingShortDto lastBooking = new ItemInfoDto.BookingShortDto(
                bookingLastId,
                LocalDateTime.of(2025, Month.MARCH, 23, 12, 0, 0),
                LocalDateTime.of(2025, Month.APRIL, 20, 12, 0, 0),
                BookingStatus.APPROVED
        );

        ItemInfoDto.BookingShortDto nextBooking = new ItemInfoDto.BookingShortDto(
                bookingNextId,
                LocalDateTime.of(2025, Month.MAY, 5, 12, 0, 0),
                LocalDateTime.of(2025, Month.MAY, 20, 12, 0, 0),
                BookingStatus.APPROVED
        );

        ItemInfoDto itemInfoDto = ItemInfoDto.builder()
                .id(itemId)
                .name("Item")
                .description("ItemDescription")
                .available(true)
                .comments(List.of(commentPartialDto))
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();

        Mockito
                .when(itemService.getItemById(eq(userId), eq(itemId)))
                .thenReturn(itemInfoDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", Long.toString(userId))
                        .contentType("application/json; charset=UTF-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemInfoDto)));

        Mockito.verify(itemService, Mockito.times(1))
                .getItemById(userId, itemId);
    }

    @SneakyThrows
    @Test
    void getAllItemsByOwnerIdTest() {
        Long userId = 2L;
        Long itemId = 4L;
        Long commentId = 1L;
        long bookingLastId = 1L;
        long bookingNextId = 2L;

        CommentPartialDto commentPartialDto = CommentPartialDto.builder()
                .id(commentId)
                .text("comment text")
                .created(LocalDateTime.of(2025, Month.MAY, 10, 12, 0, 0))
                .authorName("authorName")
                .build();

        ItemInfoDto.BookingShortDto lastBooking = new ItemInfoDto.BookingShortDto(
                bookingLastId,
                LocalDateTime.of(2025, Month.MARCH, 23, 12, 0, 0),
                LocalDateTime.of(2025, Month.APRIL, 20, 12, 0, 0),
                BookingStatus.APPROVED
        );

        ItemInfoDto.BookingShortDto nextBooking = new ItemInfoDto.BookingShortDto(
                bookingNextId,
                LocalDateTime.of(2025, Month.MAY, 5, 12, 0, 0),
                LocalDateTime.of(2025, Month.MAY, 20, 12, 0, 0),
                BookingStatus.APPROVED
        );

        ItemInfoDto itemInfoDto = ItemInfoDto.builder()
                .id(itemId)
                .name("Item")
                .description("ItemDescription")
                .available(true)
                .comments(List.of(commentPartialDto))
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();

        Mockito
                .when(itemService.getAllItemsByOwnerId(eq(userId)))
                .thenReturn(List.of(itemInfoDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", Long.toString(userId))
                        .contentType("application/json; charset=UTF-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(objectMapper.writeValueAsString(List.of(itemInfoDto))));

        Mockito.verify(itemService, Mockito.times(1))
                .getAllItemsByOwnerId(userId);
    }

    @SneakyThrows
    @Test
    void getItemsByTextQueryTest() {
        Long userId = 1L;
        Long itemId = 2L;
        final String textQuery = "qwrqwrwq 324 sdsdafsf";

        ItemPartialDto itemPartialDto = ItemPartialDto.builder()
                .id(itemId)
                .name("Item")
                .description("ItemDescription")
                .available(true)
                .build();

        Mockito
                .when(itemService.getItemsByTextQuery(eq(textQuery)))
                .thenReturn(List.of(itemPartialDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .header("X-Sharer-User-Id", Long.toString(userId))
                        .param("text", textQuery)
                        .contentType("application/json; charset=UTF-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(objectMapper.writeValueAsString(List.of(itemPartialDto))));

        Mockito.verify(itemService, Mockito.times(1))
                .getItemsByTextQuery(textQuery);
    }

    @SneakyThrows
    @Test
    void addCommentTest() {
        Long userId = 7L;
        Long commentId = 1L;
        Long itemId = 4L;

        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .text("comment text")
                .build();

        CommentPartialDto commentPartialDto = CommentPartialDto.builder()
                .id(commentId)
                .text("comment text")
                .created(LocalDateTime.of(2025, Month.MAY, 10, 12, 0, 0))
                .authorName("authorName")
                .build();

        Mockito
                .when(itemService.addComment(eq(itemId), eq(userId), any(CommentCreateDto.class)))
                .thenReturn(commentPartialDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", Long.toString(userId))
                        .content(objectMapper.writeValueAsString(commentCreateDto))
                        .contentType("application/json; charset=UTF-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(commentPartialDto)));

        Mockito.verify(itemService, Mockito.times(1))
                .addComment(eq(itemId), eq(userId), any(CommentCreateDto.class));
    }
}
