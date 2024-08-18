package ru.practicum.shareit.user.services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.mappers.UserMapper;
import java.util.List;
import ru.practicum.shareit.exceptions.EmailIsNotUniqueException;

@Service
@RequiredArgsConstructor
@Primary
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto addUser(UserDto userDto) {
        validateUserDtoEmail(userDto);
        User user = userRepository.save(userMapper.toUser(userDto));
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(int id, UserDto newUserDto) {
        validateById(id);
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
        return userMapper.toUserDto(user);
    }

    @Override
    public void deleteUserById(int id) {
        validateById(id);
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(user -> userMapper.toUserDto(user)).toList();
    }

    @Override
    public UserDto getUserById(int id) {
        validateById(id);
        return userMapper.toUserDto(userRepository.findById(id).get());
    }

    private void validateUserDtoEmail(UserDto userDto) {
        if (userRepository.findAll().stream().anyMatch(user -> user.getEmail().equals(userDto.getEmail()))) {
            throw new EmailIsNotUniqueException(
                    String.format("Failed to add user with email %s, email is not unique.", userDto.getEmail()));
        }
    }

    public void validateById(int id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(String.format("User with id %d is not found.", id));
        }
    }
}
