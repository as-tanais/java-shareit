package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDtoItemInfo;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDtoInv {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoItemInfo lastBooking;
    private BookingDtoItemInfo nextBooking;
    private List<CommentDtoExport> comments;


}
