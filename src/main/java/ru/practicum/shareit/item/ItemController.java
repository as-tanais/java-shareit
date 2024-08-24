package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoExport;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInv;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.services.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

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
    public ItemDto getById(@PathVariable int itemId,
                           @RequestHeader(USER_ID_HEADER) int userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoInv> getAll(@RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemService.getAllItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> find(@RequestParam("text") String text) {
        return itemService.find(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoExport addComment(@PathVariable int itemId,
                                       @RequestHeader(USER_ID_HEADER) int userId,
                                       @Valid @RequestBody Comment comment) {
        return itemService.addComment(itemId, userId, comment);
    }
}