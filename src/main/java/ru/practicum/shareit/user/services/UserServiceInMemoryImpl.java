package ru.practicum.shareit.user.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailIsNotUniqueException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceInMemoryImpl implements UserService {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;
    private final UserMapper userMapper;


    @Override
    public UserDto addUser(UserDto userDto) {
        validateUserDtoEmail(userDto);
        User user =  userMapper.toUser(userDto);
        user.setId(id++);
        users.put(user.getId(),user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(int id, UserDto newUserDto) {
        validateById(id);
        User oldUser = users.get(id);
        String newEmail = newUserDto.getEmail();
        String newName = newUserDto.getName();
        if (newEmail != null && !newEmail.equals(oldUser.getEmail()) && !newEmail.isEmpty()) {
            validateUserDtoEmail(newUserDto);
            users.get(id).setEmail(newEmail);
        }
        if (newName != null) {
            users.get(id).setName(newName);
        }
        return userMapper.toUserDto(users.get(id));
    }

    @Override
    public void deleteUserById(int id) {
        validateById(id);
        users.remove(id);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return users.values().stream().map(user -> userMapper.toUserDto(user)).toList();
    }

    @Override
    public UserDto getUserById(int id) {
        validateById(id);
        return userMapper.toUserDto(users.get(id));
    }

    private void validateUserDtoEmail(UserDto userDto) {
        if (users.values().stream().anyMatch(user -> user.getEmail().equals(userDto.getEmail()))) {
            throw new EmailIsNotUniqueException(String.format("Error add user with this email %s. Email already used", userDto.getEmail()));
        }
    }

    public void validateById(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("User with id %d is not found.", id));
        }
    }

}
