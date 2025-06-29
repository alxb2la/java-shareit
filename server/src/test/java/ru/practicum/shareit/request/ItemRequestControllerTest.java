package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @MockitoBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @SneakyThrows
    @Test
    void addItemRequestTest() {
        Long userId = 1L;
        Long itemRequestId = 1L;

        ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
                .description("ItemRequestDescription")
                .build();

        ItemRequestShortDto itemRequestShortDto = ItemRequestShortDto.builder()
                .id(itemRequestId)
                .description("ItemRequestDescription")
                .created(LocalDateTime.of(2025, Month.MAY, 10, 12, 0, 0))
                .build();

        Mockito
                .when(itemRequestService.addItemRequest(anyLong(), any(ItemRequestCreateDto.class)))
                .thenReturn(itemRequestShortDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", Long.toString(userId))
                        .content(objectMapper.writeValueAsString(itemRequestCreateDto))
                        .contentType("application/json; charset=UTF-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemRequestShortDto)));

        Mockito.verify(itemRequestService, Mockito.times(1))
                .addItemRequest(anyLong(), any(ItemRequestCreateDto.class));
    }

    @SneakyThrows
    @Test
    void getItemRequestByIdTest() {
        Long userId = 1L;
        Long itemRequestId = 1L;

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(itemRequestId)
                .description("ItemRequestDescription")
                .created(LocalDateTime.of(2025, Month.MAY, 10, 12, 0, 0))
                .items(List.of())
                .build();

        Mockito
                .when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/{requestId}", itemRequestId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json; charset=UTF-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                //.andExpect(MockMvcResultMatchers.jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.description", is(itemRequestDto.getDescription())))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.created", is(itemRequestDto.getCreated())))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.items", is(itemRequestDto.getItems())));
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemRequestDto)));

        Mockito.verify(itemRequestService, Mockito.times(1))
                .getItemRequestById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void getAllItemRequestsByOwnerIdTest() {
        Long userId = 1L;

        ItemRequestDto itemRequestDto1 = ItemRequestDto.builder()
                .id(1L)
                .description("ItemRequest1Description")
                .created(LocalDateTime.of(2025, Month.MAY, 10, 12, 0, 0))
                .items(List.of())
                .build();

        ItemRequestDto itemRequestDto2 = ItemRequestDto.builder()
                .id(2L)
                .description("ItemRequest2Description")
                .created(LocalDateTime.of(2025, Month.MAY, 20, 12, 0, 0))
                .items(List.of())
                .build();

        List<ItemRequestDto> requests = new ArrayList<>(List.of(itemRequestDto1, itemRequestDto2));

        Mockito
                .when(itemRequestService.getAllItemRequestsByOwnerId(anyLong()))
                .thenReturn(requests);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json; charset=UTF-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(requests)));

        Mockito.verify(itemRequestService, Mockito.times(1))
                .getAllItemRequestsByOwnerId(anyLong());
    }

    @SneakyThrows
    @Test
    void getAllItemRequestsOfOtherUsersTest() {
        Long userId = 1L;

        ItemRequestShortDto itemRequestShortDto1 = ItemRequestShortDto.builder()
                .id(1L)
                .description("ItemRequest1Description")
                .created(LocalDateTime.of(2025, Month.MAY, 10, 12, 0))
                .build();

        ItemRequestShortDto itemRequestShortDto2 = ItemRequestShortDto.builder()
                .id(2L)
                .description("ItemRequest2Description")
                .created(LocalDateTime.of(2025, Month.MAY, 20, 12, 0))
                .build();

        List<ItemRequestShortDto> requests = new ArrayList<>(List.of(itemRequestShortDto1, itemRequestShortDto2));

        Mockito
                .when(itemRequestService.getAllItemRequestsOfOtherUsers(anyLong()))
                .thenReturn(requests);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json; charset=UTF-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(requests)));

        Mockito.verify(itemRequestService, Mockito.times(1))
                .getAllItemRequestsOfOtherUsers(anyLong());
    }
}
