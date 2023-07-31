package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl {

    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserServiceImpl userServiceImpl;

    @Transactional
    public ItemDto create(int userId, ItemDto itemDto) {
        User user = userServiceImpl.getUserById(userId);
        Item item = ItemMapper.toItem(itemDto);
        ItemRequest itemRequest;
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElse(null);
        } else {
            itemRequest = null;
        }
        item.setOwnerId(user.getId());
        item.setRequest(itemRequest);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    public ItemDto update(int itemId, int userId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        userServiceImpl.getUserById(userId);
        if (item.getOwnerId() == null) {
            throw new NotFoundException("ID владельца вещи отсутствует");
        }
        if (!item.getOwnerId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    public void deleteById(Integer itemId) {
        itemRepository.deleteById(itemId);
    }

    @Transactional
    public ItemDto getItemById(int itemId, int userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (Objects.equals(item.getOwnerId(), userId)) {
            updateBooking(itemDto);
        }
        List<Comment> comments = commentRepository.findAllByItemId(itemDto.getId());
        itemDto.setComments(CommentMapper.toDtoList(comments));
        return itemDto;
    }

    @Transactional
    public List<ItemDto> findAllUserItems(int userId, int from, int size) {
        PageRequest pages = PageRequest.of(from / size, size);
        List<ItemDto> item = itemRepository.findAllByOwnerId(userId, pages).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return item.stream()
                .flatMap(i -> {
                    ItemDto updatedItem = updateBooking(i);
                    List<CommentDto> comments = CommentMapper.toDtoList(commentRepository.findAllByItemId(updatedItem.getId()));
                    updatedItem.setComments(comments);
                    return Stream.of(updatedItem);
                })
                .collect(Collectors.toList());
    }

    public ItemDto updateBooking(ItemDto itemDto) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findAllBookingsItem(itemDto.getId());
        Booking lastBooking = null;
        Booking nextBooking = null;
        for (Booking booking : bookings) {
            if (booking.getStatus() != Status.REJECTED && booking.getStart().isBefore(now)
                    && (lastBooking == null || booking.getStart().isAfter(lastBooking.getStart()))) {
                lastBooking = booking;
            }
            if (!booking.getStatus().equals(Status.REJECTED) && booking.getStart().isAfter(now)
                    && (nextBooking == null || booking.getStart().isBefore(nextBooking.getStart()))) {
                nextBooking = booking;
            }
        }
        if (lastBooking != null) {
            itemDto.setLastBooking(BookingMapper.toPartialBookingDto(lastBooking));
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(BookingMapper.toPartialBookingDto(nextBooking));
        }
        return itemDto;
    }

    @Transactional
    public List<ItemDto> search(String text, int from, int size) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        PageRequest pages = PageRequest.of(from / size, size);
        return itemRepository.getAvailableItems(text.toLowerCase(), pages).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Integer getOwnerId(int itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена")).getOwnerId();
    }

    @Transactional
    public CommentDto addComment(int itemId, int userId, CommentDto commentDto) {
        Item item = ItemMapper.toItem(getItemById(itemId, userId));
        User user = userServiceImpl.getUserById(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerIdAndStatusIsAndEndIsBefore(itemId, userId,
                Status.APPROVED, now);
        if (!bookings.isEmpty()) {
            Comment comment = CommentMapper.toComment(commentDto);
            comment.setItem(item);
            comment.setAuthor(user);
            comment.setCreated(now);
            return CommentMapper.toCommentDto(commentRepository.save(comment));
        } else {
            throw new IncorrectParameterException("Бронирование не найдено");
        }
    }

}