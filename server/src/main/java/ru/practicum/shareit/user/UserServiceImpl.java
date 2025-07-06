package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserFullDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Collection;

/**
 * UserServiceImpl — класс, реализующий интерфейс UserService.
 * Содержит всю бизнес-логику по работе с объектами User:
 * добавление нового User, обновление данных по User,
 * получение User по ID, удаление User по ID,
 * получение списка всех User в приложении.
 * Выполняет запросы к базе данных
 * Определяет транзакционные методы.
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserFullDto addUser(UserCreateDto userCreateDto) {
        User user = userRepository.save(UserMapper.toUser(userCreateDto));
        return UserMapper.toUserFullDto(user);
    }

    @Override
    @Transactional
    public UserFullDto updateUser(Long userId, UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        userUpdateDto.setId(userId);
        if (userUpdateDto.getEmail() != null) {
            user.setEmail(userUpdateDto.getEmail());
        }
        if (userUpdateDto.getName() != null) {
            user.setName(userUpdateDto.getName());
        }
        User updatedUser = userRepository.save(user);
        return UserMapper.toUserFullDto(updatedUser);
    }

    @Override
    @Transactional
    public void removeUserById(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        userRepository.deleteById(userId);
    }

    @Override
    public UserFullDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        return UserMapper.toUserFullDto(user);
    }

    @Override
    public Collection<UserFullDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserFullDto)
                .toList();
    }
}
