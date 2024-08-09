package ru.practicum.shareit.user.services;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto updateUser(int id, UserDto userDto);

    void deleteUserById(int id);

    List<UserDto> getAllUsers();

    UserDto getUserById(int id);

    void validateById(int id);
}
