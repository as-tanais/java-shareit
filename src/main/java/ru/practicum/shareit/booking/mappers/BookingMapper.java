package ru.practicum.shareit.booking.mappers;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoItemInfo;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingMapper {
    public  BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public  Booking toBooking(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(bookingDto.getItem())
                .booker(bookingDto.getBooker())
                .status(bookingDto.getStatus())
                .build();
    }

    public  Booking toBookingFromBookingRequest(BookingDtoRequest bookingDtoRequest,
                                                      Item item, User booker, BookingStatus status) {
        return Booking.builder()
                .id(bookingDtoRequest.getItemId())
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .item(item)
                .booker(booker)
                .status(status)
                .build();
    }

    public static BookingDtoItemInfo toBookingDtoItemInfo (Booking booking) {
        return BookingDtoItemInfo.builder()
                .id(booking.getId())
                .ownerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }
}