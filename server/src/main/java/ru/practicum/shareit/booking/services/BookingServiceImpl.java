package ru.practicum.shareit.booking.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.services.UserService;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;



    @Override
    public BookingDto create(BookingDtoRequest bookingDtoRequest, int bookerId) {
        User booker =  userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Can not find booker."));

        Item item = itemRepository.findById(bookingDtoRequest.getItemId())
                .orElseThrow(() -> new NotFoundException("Can not find item."));

        if (!item.getAvailable()) {
            throw new BookingUnavailableItemException(String.format("Can not book item with id %d. It is unavailable.", item.getId()));
        }

        Booking booking = BookingMapper.toBookingFromBookingRequest(bookingDtoRequest, item,booker, BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    public BookingDto approve(int bookingId, boolean approved, int userId) {
        validateById(bookingId);
        Booking booking = bookingRepository.findById(bookingId).get();
        int ownerId = booking.getItem().getOwner().getId();
        if (ownerId != userId) {
            throw new BookingApproveAccessException("Only item owner can approve booking.");
        }
        BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto get(int bookingId, int userId) {
        validateById(bookingId);
        Booking booking = bookingRepository.findById(bookingId).get();
        int bookingBookerId = booking.getBooker().getId();
        int itemOwnerId = booking.getItem().getOwner().getId();
        if (userId != bookingBookerId && userId != itemOwnerId) {
            throw new AccessException("Only booker or item owner can booking details.");
        }
        return BookingMapper.toBookingDto(bookingRepository.findById(bookingId).get());
    }

    @Override
    public List<BookingDto> getAll(int userId, BookingState bookingState) {
        userService.validateUserById(userId);
        List<Booking> bookingList = new ArrayList<>();
        LocalDateTime currentTime = LocalDateTime.now();
        bookingList = switch (bookingState) {
            case ALL -> bookingRepository.findAllByBookerId(userId);
            case CURRENT -> bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                    currentTime, currentTime);
            case PAST -> bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, currentTime);
            case FUTURE -> bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, currentTime);
            case WAITING -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> throw new WrongStateParameterException("State parameter is wrong.");
        };
        return bookingList.stream().map(BookingMapper::toBookingDto).toList();
    }

    public List<BookingDto> getAllByOwner(int userId, BookingState bookingState) {
        userService.validateUserById(userId);


        List<Booking> bookingList = new ArrayList<>();
        LocalDateTime currentTime = LocalDateTime.now();
        bookingList = switch (bookingState) {
            case ALL -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                    currentTime, currentTime);
            case PAST -> bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, currentTime);
            case FUTURE -> bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, currentTime);
            case WAITING ->
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> throw new WrongStateParameterException("State parameter is wrong.");
        };
        return bookingList.stream().map(BookingMapper::toBookingDto).toList();
    }

    private void validateById(int id) {
        if (!bookingRepository.existsById(id)) {
            throw new NotFoundException(String.format("Booking with id %d is not found.", id));
        }
    }
}