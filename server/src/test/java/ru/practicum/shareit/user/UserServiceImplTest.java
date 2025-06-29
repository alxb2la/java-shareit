package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserFullDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private final User user1 = User.builder()
            .id(1L)
            .name("User1Name")
            .email("User1Email@Email123.net")
            .build();


    @Test
    void addUserTest() {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("UserName")
                .email("UserEmail@Email123.net")
                .build();
        UserFullDto userFullDto = UserMapper.toUserFullDto(user1);
        Mockito
                .when(userRepository.save(any(User.class)))
                .thenReturn(user1);

        UserFullDto createdUser = userService.addUser(userCreateDto);

        assertEquals(createdUser.getId(), userFullDto.getId());
        assertEquals(createdUser.getName(), userFullDto.getName());
        assertEquals(createdUser.getEmail(), userFullDto.getEmail());
        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));

    }

    @Test
    void updateUser_whenUpdateParamsIsPresentAndUserFound_thenUpdateAndReturnUpdatedUser() {
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .id(user1.getId())
                .name("User1UpdatedName")
                .email("User1EmailUpdated@Email123.net")
                .build();
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        Mockito
                .when(userRepository.save(any(User.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, User.class));

        UserFullDto updatedUser = userService.updateUser(user1.getId(), userUpdateDto);

        assertEquals(updatedUser.getId(), userUpdateDto.getId());
        assertEquals(updatedUser.getName(), userUpdateDto.getName());
        assertEquals(updatedUser.getEmail(), userUpdateDto.getEmail());
        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
    }

    @Test
    void updateUser_whenUserNotFound_thenThrowNotFoundException() {
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .id(user1.getId())
                .name("User1UpdateName")
                .email("User1Email@Email123.net")
                .build();
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.updateUser(user1.getId(), userUpdateDto));

        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
    }

    @Test
    void updateUser_whenOnlyNameParamToUpdate_thenReturnPartialUpdatedUser() {
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .id(user1.getId())
                .name("User1UpdateName")
                .email(null)
                .build();
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        Mockito
                .when(userRepository.save(any(User.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, User.class));

        UserFullDto updatedUser = userService.updateUser(user1.getId(), userUpdateDto);

        assertEquals(updatedUser.getId(), userUpdateDto.getId());
        assertEquals(updatedUser.getName(), userUpdateDto.getName());
        assertEquals(updatedUser.getEmail(), user1.getEmail());
        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
    }

    @Test
    void updateUser_whenOnlyEmailParamToUpdate_thenReturnPartialUpdatedUser() {
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .id(user1.getId())
                .name(null)
                .email("User1Email@Email123.net")
                .build();
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        Mockito
                .when(userRepository.save(any(User.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, User.class));

        UserFullDto updatedUser = userService.updateUser(user1.getId(), userUpdateDto);

        assertEquals(updatedUser.getId(), userUpdateDto.getId());
        assertEquals(updatedUser.getName(), user1.getName());
        assertEquals(updatedUser.getEmail(), userUpdateDto.getEmail());
        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
    }


    @Test
    void removeUserById_whenUserFound_thenDelete() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));

        userService.removeUserById(user1.getId());

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(anyLong());
    }

    @Test
    void removeUserById_whenUserNotFound_thenThrowNotFoundException() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.removeUserById(user1.getId()));

        Mockito.verify(userRepository, Mockito.never()).deleteById(anyLong());
    }

    @Test
    void getUserById_whenUserFound_thenReturnUser() {
        Mockito
                .when(userRepository.findById(eq(user1.getId())))
                .thenReturn(Optional.of(user1));

        UserFullDto findUser = userService.getUserById(user1.getId());

        assertEquals(findUser.getId(), user1.getId());
        assertEquals(findUser.getName(), user1.getName());
        assertEquals(findUser.getEmail(), user1.getEmail());
        Mockito.verify(userRepository, Mockito.times(1)).findById(user1.getId());
    }

    @Test
    void getUserById_whenUserNotFound_thenThrowNotFoundException() {
        Mockito
                .when(userRepository.findById(eq(user1.getId())))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.getUserById(user1.getId()));
    }

    @Test
    void getAllUsers() {
        User user2 = User.builder()
                .id(2L)
                .name("User2Name")
                .email("User2Email@Email123.net")
                .build();
        List<UserFullDto> testUsers = Stream.of(user1, user2)
                .map(UserMapper::toUserFullDto)
                .toList();
        Mockito
                .when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));

        List<UserFullDto> getUsers = new ArrayList<>(userService.getAllUsers());

        assertEquals(2, getUsers.size());
        assertEquals(testUsers.get(0).getId(), getUsers.get(0).getId());
        assertEquals(testUsers.get(1).getId(), getUsers.get(1).getId());
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
    }
}
