package ru.practicum.shareit.user.services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailIsNotUniqueException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Primary
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto addUser(UserDto userDto) {
        validateUserDtoEmail(userDto);
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(int id, UserDto newUserDto) {
        validateUserById(id);
        User oldUser = userRepository.findById(id).get();

        String newEmail = newUserDto.getEmail();
        if (newEmail != null && !newEmail.equals(oldUser.getEmail())) {
            validateUserDtoEmail(newUserDto);
            oldUser.setEmail(newEmail);
        }

        String newName = newUserDto.getName();
        if (newName != null) {
            oldUser.setName(newName);
        }

        User user = userRepository.save(oldUser);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUserById(int id) {
        validateUserById(id);
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(user -> UserMapper.toUserDto(user)).toList();
    }

    @Override
    public UserDto getUserById(int id) {
        validateUserById(id);
        return UserMapper.toUserDto(userRepository.findById(id).get());
    }

    private void validateUserDtoEmail(UserDto userDto) {
        if (userRepository.findAll().stream().anyMatch(user -> user.getEmail().equals(userDto.getEmail()))) {
            throw new EmailIsNotUniqueException(String.format("Failed to add user with email %s, email is not unique.", userDto.getEmail()));
        }
    }

    public void validateUserById(int id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(String.format("User with id %d is not found.", id));
        }
    }
}