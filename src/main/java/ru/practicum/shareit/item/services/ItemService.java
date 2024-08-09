package ru.practicum.shareit.item.services;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, int ownerId);

    ItemDto updateItem(int itemId, int ownerId, ItemDto newItemDto);

    ItemDto getItemById(int itemId);

    List<ItemDto> getAllItems(int ownerId);

    List<ItemDto> find(String text);
}
