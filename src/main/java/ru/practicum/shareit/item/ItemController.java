package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.services.ItemService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto add(@RequestBody @Valid ItemDto itemDto,
                       @RequestHeader(USER_ID_HEADER) int ownerId) {
        return itemService.addItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto newItemDto,
                          @PathVariable int itemId,
                          @RequestHeader(USER_ID_HEADER) int ownerId) {
        return itemService.updateItem(itemId, ownerId, newItemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable int itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader(USER_ID_HEADER) int ownerId) {
        return itemService.getAllItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> find(@RequestParam("text") String text) {
        return itemService.find(text);
    }
}
