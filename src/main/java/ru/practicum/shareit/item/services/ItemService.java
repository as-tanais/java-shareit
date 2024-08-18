package ru.practicum.shareit.item.services;

import ru.practicum.shareit.item.dto.CommentDtoExport;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, int ownerId);

    ItemDto updateItem(int itemId, int ownerId, ItemDto newItemDto);

    ItemDto getItemById(int itemId, int userId);

    List<ItemDto> getAllItems(int ownerId);

    List<ItemDto> find(String text);

    void validateById(int id);

    CommentDtoExport addComment(int itemId, int userId, Comment comment);
}
