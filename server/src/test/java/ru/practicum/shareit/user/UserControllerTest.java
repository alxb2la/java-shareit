package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserFullDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @SneakyThrows
    @Test
    void addUserTest() {
        Long userId = 1L;

        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("UserName")
                .email("UserEmail@Email123.net")
                .build();

        UserFullDto userFullDto = UserFullDto.builder()
                .id(userId)
                .name("UserName")
                .email("UserEmail@Email123.net")
                .build();

        Mockito
                .when(userService.addUser(any(UserCreateDto.class)))
                .thenReturn(userFullDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(objectMapper.writeValueAsString(userCreateDto))
                        .contentType("application/json; charset=UTF-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userFullDto.getId()))
                .andExpect(jsonPath("$.name").value(userFullDto.getName()))
                .andExpect(jsonPath("$.email").value(userFullDto.getEmail()));

        Mockito.verify(userService, Mockito.times(1)).addUser(any(UserCreateDto.class));
    }

    @SneakyThrows
    @Test
    void updateUserTest() {
        Long userId = 1L;

        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .id(userId)
                .name("UserName")
                .email("UserEmail@Email123.net")
                .build();

        UserFullDto userFullDto = UserFullDto.builder()
                .id(userId)
                .name("UserName")
                .email("UserEmail@Email123.net")
                .build();

        Mockito
                .when(userService.updateUser(anyLong(), any(UserUpdateDto.class)))
                .thenReturn(userFullDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", userId)
                        .content(objectMapper.writeValueAsString(userUpdateDto))
                        .contentType("application/json; charset=UTF-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userFullDto.getId()))
                .andExpect(jsonPath("$.name").value(userFullDto.getName()))
                .andExpect(jsonPath("$.email").value(userFullDto.getEmail()));

        Mockito.verify(userService, Mockito.times(1))
                .updateUser(anyLong(), any(UserUpdateDto.class));
    }

    @SneakyThrows
    @Test
    void removeUserByIdTest() {
        Long userId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());

        Mockito.verify(userService, Mockito.times(1)).removeUserById(anyLong());
    }

    @SneakyThrows
    @Test
    void getUserByIdTest() {
        Long userId = 1L;
        UserFullDto userDto = UserFullDto.builder()
                .id(userId)
                .name("UserName")
                .email("UserEmail@Email123.net")
                .build();

        Mockito
                .when(userService.getUserById(anyLong()))
                .thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}", userId)
                        .contentType("application/json; charset=UTF-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        Mockito.verify(userService, Mockito.times(1)).getUserById(anyLong());
    }

    @SneakyThrows
    @Test
    void getAllUsersTest() {
        Long userId = 1L;
        UserFullDto userDto1 = UserFullDto.builder()
                .id(userId)
                .name("User1Name")
                .email("User1Email@Email123.net")
                .build();
        UserFullDto userDto2 = UserFullDto.builder()
                .id(userId)
                .name("User2Name")
                .email("User2Email@Email123.net")
                .build();

        Mockito
                .when(userService.getAllUsers())
                .thenReturn(List.of(userDto1, userDto2));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .contentType("application/json; charset=UTF-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(objectMapper.writeValueAsString(List.of(userDto1, userDto2)),
                result.getResponse().getContentAsString());
        Mockito.verify(userService, Mockito.times(1)).getAllUsers();
    }
}
