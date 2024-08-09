package ru.practicum.shareit.item.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.exceptions.AccessException;
import ru.practicum.shareit.exceptions.NotFoundException;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.services.UserService;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemServiceInMemoryImpl implements ItemService {
    private final Map<Integer, Item> items = new HashMap<>();
    private int id = 1;
    private final ItemMapper itemMapper;

    private final UserService userService;

    @Override
    public ItemDto addItem(ItemDto itemDto, int ownerId) {
        userService.validateById(ownerId);
        Item item = itemMapper.toItem(itemDto);
        item.setId(id++);
        item.setOwner(ownerId);
        items.put(item.getId(), item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(int itemId, int ownerId, ItemDto newItemDto) {
        validateById(itemId);
        Item item = items.get(itemId);
        if (item.getOwner() != ownerId) {
            throw new AccessException("Can't update item. Only owner can update");
        }

        if (newItemDto.getName() != null) {
            item.setName(newItemDto.getName());
        }
        if (newItemDto.getDescription() != null) {
            item.setDescription(newItemDto.getDescription());
        }
        if (newItemDto.getAvailable() != null) {
            item.setAvailable(newItemDto.getAvailable());
        }

        return itemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public ItemDto getItemById(int itemId) {
        validateById(itemId);
        return itemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public List<ItemDto> getAllItems(int ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner() == ownerId)
                .map(item -> itemMapper.toItemDto(item))
                .toList();
    }

    @Override
    public List<ItemDto> find(String text) {
        if (text.isEmpty()) return new ArrayList<>();

        return items.values().stream()
                .filter(item -> item.getName() != null && item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription() != null && item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .map(item -> itemMapper.toItemDto(item))
                .toList();
    }

    private void validateById(int id) {
        if (!items.containsKey(id)) {
            throw new NotFoundException(String.format("Item with id %d is not found.", id));
        }
    }


}
