package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.State;
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
    public List<BookingDto> findAllBookingsByUserId(int userId, String state) {
        userServiceImpl.getUserById(userId);
        List<Booking> booking;
        if (State.ALL.name().equals(state)) {
            booking = bookingRepository.findByBookerIdOrderByStartDesc(userId);
            return BookingMapper.toBookingDto(booking);
        }
        if (State.CURRENT.name().equals(state)) {
            booking = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                    userId, LocalDateTime.now(), LocalDateTime.now());
            return BookingMapper.toBookingDto(booking);
        }
        if (State.PAST.name().equals(state)) {
            booking = bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(
                    userId, LocalDateTime.now());
            return BookingMapper.toBookingDto(booking);
        }
        if (State.FUTURE.name().equals(state)) {
            booking = bookingRepository.findAllByBookerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(
                    userId, LocalDateTime.now(), LocalDateTime.now());
            return BookingMapper.toBookingDto(booking);
        }
        if (State.WAITING.name().equals(state)) {
            booking = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.valueOf(state));
            return BookingMapper.toBookingDto(booking);
        }
        if (State.REJECTED.name().equals(state)) {
            booking = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.valueOf(state));
            return BookingMapper.toBookingDto(booking);
        }
        throw new IncorrectParameterException(String.format("Unknown state: %s", state));
    }

    @Transactional
    public List<BookingDto> findAllBookingsByOwnerId(String state, int ownerId) {
        userServiceImpl.getUserById(ownerId);
        List<Booking> booking;
        if (State.ALL.name().equals(state)) {
            booking = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
            return BookingMapper.toBookingDto(booking);
        }
        if (State.CURRENT.name().equals(state)) {
            booking = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(ownerId, LocalDateTime.now(), LocalDateTime.now());
            return BookingMapper.toBookingDto(booking);
        }
        if (State.PAST.name().equals(state)) {
            booking = bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(ownerId, LocalDateTime.now());
            return BookingMapper.toBookingDto(booking);
        }
        if (State.FUTURE.name().equals(state)) {
            booking = bookingRepository.findByItemOwnerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(ownerId, LocalDateTime.now(), LocalDateTime.now());
            return BookingMapper.toBookingDto(booking);
        }
        if (State.WAITING.name().equals(state)) {
            booking = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.valueOf(state));
            return BookingMapper.toBookingDto(booking);
        }
        if (State.REJECTED.name().equals(state)) {
            booking = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.valueOf(state));
            return BookingMapper.toBookingDto(booking);
        }
        throw new IncorrectParameterException(String.format("Unknown state: %s", state));
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
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

}