package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.PartialBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class BookingServiceImpl {

    private final UserServiceImpl userServiceImpl;
    private final ItemServiceImpl itemServiceImpl;
    private final BookingRepository bookingRepository;

    @Transactional
    public BookingDto create(int userId, PartialBookingDto partialBookingDto) {
        Item item = ItemMapper.toItem(itemServiceImpl.getItemById(partialBookingDto.getItemId(), userId));
        if (Objects.equals(itemServiceImpl.getOwnerId(item.getId()), userId)) {
            throw new NotFoundException("Владелец вещи не может ее забронировать");
        }
        if (partialBookingDto.getEnd().isBefore(partialBookingDto.getStart()) ||
                partialBookingDto.getEnd().equals(partialBookingDto.getStart())) {
            throw new IncorrectParameterException("Ошибка бронирования: некорректно указано время");
        }
        User booker = userServiceImpl.getUserById(userId);
        if (item.getAvailable()) {
            return BookingMapper.toBookingDto(bookingRepository.save(
                    new Booking(
                            partialBookingDto.getStart(),
                            partialBookingDto.getEnd(),
                            item,
                            booker,
                            Status.WAITING
                    )));
        } else {
            throw new IncorrectParameterException("Вещь не найдена");
        }
    }

    @Transactional
    public BookingDto findBookingById(int userId, int bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Бронирование не найдено"));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwnerId().equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotFoundException("Пользователь не является собственником вещи");
        }
    }

    @Transactional
    public List<BookingDto> findAllBookingsByUserId(int userId, String state, int from, int size) {
        userServiceImpl.getUserById(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> booking;
        PageRequest pages = PageRequest.of(from / size, size);
        switch (state) {
            case "ALL":
                booking = bookingRepository.findByBookerIdOrderByStartDesc(userId, pages);
                break;
            case "CURRENT":
                booking = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                        userId, now, now, pages);
                break;
            case "PAST":
                booking = bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(
                        userId, now, pages);
                break;
            case "FUTURE":
                booking = bookingRepository.findAllByBookerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(
                        userId, now, now, pages);
                break;
            case "WAITING":
            case "REJECTED":
                booking = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.valueOf(state), pages);
                break;
            default:
                throw new IncorrectParameterException(String.format("Unknown state: %s", state));
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Transactional
    public List<BookingDto> findAllBookingsByOwnerId(String state, int ownerId, int from, int size) {
        userServiceImpl.getUserById(ownerId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> booking;
        PageRequest pages = PageRequest.of(from / size, size);
        switch (state) {
            case "ALL":
                booking = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId, pages);
                break;
            case "CURRENT":
                booking = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                        ownerId, now, now, pages);
                break;
            case "PAST":
                booking = bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(
                        ownerId, now, pages);
                break;
            case "FUTURE":
                booking = bookingRepository.findByItemOwnerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(
                        ownerId, now, now, pages);
                break;
            case "WAITING":
            case "REJECTED":
                booking = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                        ownerId, Status.valueOf(state), pages);
                break;
            default:
                throw new IncorrectParameterException(String.format("Unknown state: %s", state));
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Transactional
    public BookingDto updateBooking(int userId, int bookingId, Boolean approved) {
        Booking booking = BookingMapper.toBooking(findBookingById(userId, bookingId));
        if (itemServiceImpl.getOwnerId(booking.getItem().getId()).equals(userId)
                && booking.getStatus().equals(Status.APPROVED)) {
            throw new AlreadyExistException("Бронирование уже одобрено");
        }
        if (!booking.getItem().getOwnerId().equals(userId)) {
            throw new NotFoundException("Только владелец вещи может одобрить или отклонить запрос на бронирование");
        }
        if ((!booking.getStatus().equals(Status.WAITING))) {
            throw new NotFoundException("Обновление статуса недоступно");
        }
        if (approved != null && approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

}