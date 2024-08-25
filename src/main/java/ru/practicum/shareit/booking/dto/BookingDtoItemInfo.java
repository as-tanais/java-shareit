package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class BookingDtoItemInfo {
    private int id;
    private int ownerId;
    private LocalDateTime start;
    private LocalDateTime end;
}
