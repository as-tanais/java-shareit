package ru.practicum.shareit.item.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoItemInfo;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.AccessException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnavailableToAddCommentException;
import ru.practicum.shareit.item.dto.CommentDtoExport;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInv;
import ru.practicum.shareit.item.mappers.CommentMapper;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.services.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserMapper userMapper;
    private final BookingMapper bookingMapper;

    @Override
    public ItemDto addItem(ItemDto itemDto, int ownerId) {
        userService.validateById(ownerId);
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userMapper.toUser(userService.getUserById(ownerId)));
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(int itemId, int ownerId, ItemDto newItemDto) {
        validateById(itemId);
        Item oldItem = itemRepository.findById(itemId).get();

        if (oldItem.getOwner().getId() != ownerId) {
            throw new AccessException("Only owner can update item");
        }

        if (newItemDto.getName() != null) {
            oldItem.setName(newItemDto.getName());
        }
        if (newItemDto.getDescription() != null) {
            oldItem.setDescription(newItemDto.getDescription());
        }
        if (newItemDto.getAvailable() != null) {
            oldItem.setAvailable(newItemDto.getAvailable());
        }

        Item item = itemRepository.save(oldItem);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemById(int itemId, int userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Can not find item with id %d.", itemId)));

        List<CommentDtoExport> comments = commentRepository.findAllByItemId(item.getId())
                .stream()
                .map(CommentMapper::toCommentDtoExport)
                .toList();


        ItemDto itemDto = itemMapper.toItemDto(itemRepository.findById(itemId).get());
        itemDto.setComments(comments);

        if (item.getOwner().getId() == userId) {
            Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusNotOrderByStartDesc(itemId, LocalDateTime.now(), BookingStatus.REJECTED);
            if (!lastBooking.isEmpty()) {
                itemDto.setLastBooking(bookingMapper.toBookingDto(lastBooking.get()));
            }
            Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStartAfterAndStatusNotOrderByStart(itemId, LocalDateTime.now(), BookingStatus.REJECTED);
            if (!nextBooking.isEmpty()) {
                itemDto.setNextBooking(bookingMapper.toBookingDto(nextBooking.get()));
            }
        }

        return itemDto;
    }


    @Override
    public List<ItemDtoInv> getAllItems(Long ownerId) {
        List<Item> itemsOfOwner = itemRepository.findAllByOwnerId(ownerId);
        List<ItemDtoInv> itemsWithBookingInformation = new ArrayList<>();
        for (Item item : itemsOfOwner) {
            List<CommentDtoExport> comments = commentRepository.findAllByItemId(item.getId())
                    .stream()
                    .map(CommentMapper::toCommentDtoExport)
                    .collect(Collectors.toList());
            List<Booking> bookings = new ArrayList<>();
            List<Booking> lastBookingByItem = bookingRepository.findFirst1ByItemIdAndStartIsBeforeOrderByStartDesc(item.getId(), LocalDateTime.now());
            if (!lastBookingByItem.isEmpty()) {
                bookings.add(lastBookingByItem.get(0));
            } else {
                ItemDtoInv itemDtoWithBookingInformation =
                        itemMapper.toItemDtoInv(item, null, null, comments);
                itemsWithBookingInformation.add(itemDtoWithBookingInformation);
                continue;
            }
            List<Booking> nextBookingByItem = bookingRepository.findFirst1ByItemIdAndStartIsAfterOrderByStartAsc(item.getId(), LocalDateTime.now());
            if (!nextBookingByItem.isEmpty()) {
                bookings.add(nextBookingByItem.get(0));
            }
            List<BookingDtoItemInfo> bookingsForItemInformation =
                    bookings.stream()
                            .map(BookingMapper::toBookingDtoItemInfo)
                            .collect(Collectors.toList());
            ItemDtoInv itemDtoWithBookingInformation;
            if (bookings.size() == 1) {
                itemDtoWithBookingInformation = itemMapper.toItemDtoInv(item,
                        bookingsForItemInformation.get(0), null, comments);
            } else {
                itemDtoWithBookingInformation = itemMapper.toItemDtoInv(item,
                        bookingsForItemInformation.get(0), bookingsForItemInformation.get(1), comments);
            }
            itemsWithBookingInformation.add(itemDtoWithBookingInformation);
        }
        return itemsWithBookingInformation;
    }

    @Override
    public List<ItemDto> find(String text) {
        if (text.equals("")) return new ArrayList<>();

        return itemRepository.findAll().stream()
                .filter(item -> item.getName() != null && item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription() != null && item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .map(item -> itemMapper.toItemDto(item))
                .toList();
    }

    @Override
    public CommentDtoExport addComment(int itemId, int userId, Comment comment) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Can not find user with id %d.", userId)));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Can not find item with id %d.", itemId)));

        Optional<Booking> bookingOptional = bookingRepository.findFirstByBookerIdAndEndBeforeAndStatusNot(userId, LocalDateTime.now(), BookingStatus.REJECTED);
        if (bookingOptional.isEmpty()) {
            throw new UnavailableToAddCommentException("Can not add comment, because there was no booking.");
        }
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        comment.setAuthor(author);

        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.toCommentDtoExport(savedComment);

    }

    public void validateById(int id) {
        if (!itemRepository.existsById(id)) {
            throw new NotFoundException(String.format("Item with id %d is not found.", id));
        }
    }


}