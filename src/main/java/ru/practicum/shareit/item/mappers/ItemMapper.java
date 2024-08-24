package ru.practicum.shareit.item.mappers;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoItemInfo;
import ru.practicum.shareit.item.dto.CommentDtoExport;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInv;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Component
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest())
                .build();
    }

    public Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .request(itemDto.getRequest())
                .build();
    }

    public ItemDtoInv toItemDtoInv(Item item, BookingDtoItemInfo lastBooking, BookingDtoItemInfo nextBooking, List<CommentDtoExport> comments) {
        return ItemDtoInv.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }

}
