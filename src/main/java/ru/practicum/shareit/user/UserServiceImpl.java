package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserFullDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Collection;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserFullDto addUser(UserCreateDto userCreateDto) {
        User user = userRepository.create(userMapper.toUser(userCreateDto));
        return userMapper.toUserFullDto(user);
    }

    @Override
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
        User updatedUser = userRepository.update(user);
        return userMapper.toUserFullDto(updatedUser);
    }

    @Override
    public void removeUserById(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        userRepository.delete(userId);
    }

    @Override
    public UserFullDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + userId));
        return userMapper.toUserFullDto(user);
    }

    @Override
    public Collection<UserFullDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserFullDto)
                .toList();
    }
}
