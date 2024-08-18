package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class ItemRequestDto {
    private int id;
    private String description;
    private User requester;
    private LocalDateTime created;
}