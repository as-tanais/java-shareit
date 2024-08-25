package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
public class ItemDto {
    private int id;
    @NotBlank(message = "Item name should not be empty.")
    private String name;
    @NotBlank(message = "Item description should not be empty.")
    private String description;
    @NotNull
    private Boolean available;
    private ItemRequest request;
    private List<CommentDtoExport> comments;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
}
