package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.PartialBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    private BookingMapper bookingMapper;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserServiceImpl userService;
    private ItemServiceImpl itemService;
    private ItemRepository itemRepository;
    private ItemRequestRepository itemRequestRepository;
    private UserServiceImpl userServiceImpl;
    private CommentRepository commentRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        userServiceImpl = mock(UserServiceImpl.class);
        itemService = new ItemServiceImpl(bookingRepository, commentRepository, itemRepository, itemRequestRepository, userServiceImpl);
    }

    @Test
    public void findBookingByIdTest() {
        int userId = 1;
        int bookingId = 100;
        User booker = new User();
        booker.setId(userId);
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        Booking result = BookingMapper.toBooking(bookingService.findBookingById(userId, bookingId));
        assertNotNull(result, "Ошибка при поиске бронирования по ID: результат не должен быть null");
        assertEquals(bookingId, result.getId(),
                "Ошибка при поиске бронирования по ID: ID бронирования не соответствует ожидаемому значению");
        assertEquals(booking, result,
                "Ошибка при поиске бронирования по ID: объекты бронирования не соответствуют ожидаемым");
    }

    @Test
    public void testFindBookingByIdNotFound() {
        int userId = 1;
        int bookingId = 100;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> bookingService.findBookingById(userId, bookingId), "Ожидалось исключение NotFoundException.");
    }

    @Test
    public void testFindBookingByIdNotOwner() {
        int userId = 1;
        int bookingId = 100;
        User booker = new User();
        booker.setId(userId);
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        assertDoesNotThrow(() -> bookingService.findBookingById(userId, bookingId),
                "При поиске бронирования пользователем с идентификатором " + userId
                        + " не должно возникать исключения.");
    }

    @Test
    public void testToBookingDto() {
        int id = 1;
        LocalDateTime start = LocalDateTime.of(2023, 7, 31, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 8, 1, 12, 0);
        Item item = new Item();
        User booker = new User();
        Status status = Status.APPROVED;
        Booking booking = new Booking(id, start, end, item, booker, status);
        BookingDto result = BookingMapper.toBookingDto(booking);
        assertEquals(id, result.getId(), "Неправильный идентификатор бронирования.");
        assertEquals(start, result.getStart(), "Неправильная дата начала бронирования.");
        assertEquals(end, result.getEnd(), "Неправильная дата окончания бронирования.");
        assertEquals(item, result.getItem(), "Неправильный предмет бронирования.");
        assertEquals(booker, result.getBooker(), "Неправильный пользователь-бронировщик.");
        assertEquals(status, result.getStatus(), "Неправильный статус бронирования.");
    }

    @Test
    public void testToPartialBookingDto() {
        int id = 1;
        LocalDateTime start = LocalDateTime.of(2023, 7, 31, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 8, 1, 12, 0);
        Item item = new Item(1, "item", "description", true);
        User booker = new User(1, "user", "user@user.com");
        Booking booking = new Booking(id, start, end, item, booker, Status.APPROVED);
        PartialBookingDto result = BookingMapper.toPartialBookingDto(booking);
        assertEquals(id, result.getId(), "Неправильный идентификатор бронирования.");
        assertEquals(start, result.getStart(), "Неправильная дата начала бронирования.");
        assertEquals(end, result.getEnd(), "Неправильная дата окончания бронирования.");
        assertEquals(item.getId(), result.getItemId(), "Неправильный идентификатор предмета бронирования.");
        assertEquals(booker.getId(), result.getBookerId(), "Неправильный идентификатор пользователя-бронировщика.");
    }

    @Test
    public void testToBooking() {
        int id = 1;
        LocalDateTime start = LocalDateTime.of(2023, 7, 31, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 8, 1, 12, 0);
        Item item = new Item();
        User booker = new User();
        Status status = Status.APPROVED;
        BookingDto bookingDto = new BookingDto(id, start, end, item, booker, status);
        Booking result = BookingMapper.toBooking(bookingDto);
        assertEquals(id, result.getId(), "Неправильный идентификатор бронирования.");
        assertEquals(start, result.getStart(), "Неправильная дата начала бронирования.");
        assertEquals(end, result.getEnd(), "Неправильная дата окончания бронирования.");
        assertEquals(item, result.getItem(), "Неправильный предмет бронирования.");
        assertEquals(booker, result.getBooker(), "Неправильный пользователь-бронировщик.");
        assertEquals(status, result.getStatus(), "Неправильный статус бронирования.");
    }

    @Test
    public void testToBookingDtoList() {
        int id1 = 1;
        LocalDateTime start1 = LocalDateTime.of(2023, 7, 31, 12, 0);
        LocalDateTime end1 = LocalDateTime.of(2023, 8, 1, 12, 0);
        Item item1 = new Item();
        User booker1 = new User();
        Status status1 = Status.APPROVED;
        Booking booking1 = new Booking(id1, start1, end1, item1, booker1, status1);
        int id2 = 2;
        LocalDateTime start2 = LocalDateTime.of(2023, 8, 1, 12, 0);
        LocalDateTime end2 = LocalDateTime.of(2023, 8, 2, 12, 0);
        Item item2 = new Item();
        User booker2 = new User();
        Status status2 = Status.REJECTED;
        Booking booking2 = new Booking(id2, start2, end2, item2, booker2, status2);
        List<Booking> bookings = Arrays.asList(booking1, booking2);
        List<BookingDto> result = BookingMapper.toBookingDto(bookings);
        assertEquals(2, result.size(), "Неправильный размер списка бронирований.");
        assertEquals(id1, result.get(0).getId(), "Неправильный идентификатор бронирования.");
        assertEquals(start1, result.get(0).getStart(), "Неправильная дата начала бронирования.");
        assertEquals(end1, result.get(0).getEnd(), "Неправильная дата окончания бронирования.");
        assertEquals(item1, result.get(0).getItem(), "Неправильный предмет бронирования.");
        assertEquals(booker1, result.get(0).getBooker(), "Неправильный пользователь-бронировщик.");
        assertEquals(status1, result.get(0).getStatus(), "Неправильный статус бронирования.");
        assertEquals(id2, result.get(1).getId(), "Неправильный идентификатор бронирования.");
        assertEquals(start2, result.get(1).getStart(), "Неправильная дата начала бронирования.");
        assertEquals(end2, result.get(1).getEnd(), "Неправильная дата окончания бронирования.");
        assertEquals(item2, result.get(1).getItem(), "Неправильный предмет бронирования.");
        assertEquals(booker2, result.get(1).getBooker(), "Неправильный пользователь-бронировщик.");
        assertEquals(status2, result.get(1).getStatus(), "Неправильный статус бронирования.");
    }

    @Test
    public void testBookingItemRelationship() {
        Item item = mock(Item.class);
        Booking booking = new Booking(LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, null, Status.WAITING);
        assertEquals(item, booking.getItem(), "Неправильный предмет бронирования.");
    }

    @Test
    public void testBookingUserRelationship() {
        User user = mock(User.class);
        Booking booking = new Booking(LocalDateTime.now(), LocalDateTime.now().plusHours(1), null, user, Status.WAITING);
        assertEquals(user, booking.getBooker(), "Неправильный пользователь-бронировщик.");
    }

    @Test
    public void testBookingStatus() {
        Status status = Status.APPROVED;
        Booking booking = new Booking(LocalDateTime.now(), LocalDateTime.now().plusHours(1), null, null, status);
        assertEquals(status, booking.getStatus(), "Неправильный статус бронирования.");
    }

    @Test
    public void testEnumValues() {
        assertEquals(6, State.values().length, "Неправильное количество значений в enum State.");
        assertArrayEquals(new State[]{State.ALL, State.CURRENT, State.PAST, State.FUTURE, State.WAITING, State.REJECTED},
                State.values(), "Неправильные значения в enum State.");
    }

    @Test
    public void testEnumValue() {
        assertEquals(State.ALL, State.valueOf("ALL"), "Неправильное значение enum State для ALL.");
        assertEquals(State.CURRENT, State.valueOf("CURRENT"), "Неправильное значение enum State для CURRENT.");
        assertEquals(State.PAST, State.valueOf("PAST"), "Неправильное значение enum State для PAST.");
        assertEquals(State.FUTURE, State.valueOf("FUTURE"), "Неправильное значение enum State для FUTURE.");
        assertEquals(State.WAITING, State.valueOf("WAITING"), "Неправильное значение enum State для WAITING.");
        assertEquals(State.REJECTED, State.valueOf("REJECTED"), "Неправильное значение enum State для REJECTED.");
    }

    @Test
    public void testInvalidEnumValueTest() {
        assertThrows(IllegalArgumentException.class, () -> State.valueOf("INVALID_STATE"),
                "Неправильное исключение при некорректном значении enum State.");
    }

    @Test
    public void testFindAllBookingsByOwnerIdAll() {
        int ownerId = 1;
        int from = 0;
        int size = 10;
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking(1, now.minusHours(2), now.minusHours(1), new Item(), new User(), Status.APPROVED));
        bookings.add(new Booking(2, now.plusHours(1), now.plusHours(2), new Item(), new User(), Status.WAITING));
        PageRequest pages = PageRequest.of(from / size, size);
        when(userService.getUserById(ownerId)).thenReturn(new User());
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId, pages)).thenReturn(bookings);
        List<BookingDto> result = bookingService.findAllBookingsByOwnerId("ALL", ownerId, from, size);
        assertEquals(2, result.size(), "Неправильное количество бронирований, найденных по ownerId.");
    }

    @Test
    public void testFindAllBookingsByOwnerIdUnknownState() {
        int ownerId = 1;
        int from = 0;
        int size = 10;
        when(userService.getUserById(ownerId)).thenReturn(new User());
        IncorrectParameterException exception = assertThrows(IncorrectParameterException.class,
                () -> bookingService.findAllBookingsByOwnerId("UNKNOWN", ownerId, from, size),
                "Неправильное исключение при использовании неизвестного состояния.");
        assertEquals("Unknown state: UNKNOWN", exception.getMessage(),
                "Неправильное сообщение об ошибке при использовании неизвестного состояния.");
    }

    @Test
    public void testFindAllBookingsByUserIdAllStateAll() {
        int userId = 1;
        String state = "ALL";
        int from = 0;
        int size = 10;
        List<Booking> bookings = testCreateBookingList(userId, 5);
        when(userService.getUserById(userId)).thenReturn(new User(userId, "User", "user@user.com"));
        when(bookingRepository.findByBookerIdOrderByStartDesc(eq(userId), any(PageRequest.class))).thenReturn(bookings);
        List<BookingDto> result = bookingService.findAllBookingsByUserId(userId, state, from, size);
        assertEquals(bookings.size(), result.size(),
                "Неправильное количество бронирований, найденных по userId и state ALL.");
    }

    @Test
    public void testFindAllBookingsByUserIdAllStateCurrent() {
        int userId = 1;
        String state = "CURRENT";
        int from = 0;
        int size = 10;
        List<Booking> bookings = testCreateBookingList(userId, 0);
        when(userService.getUserById(userId)).thenReturn(new User(userId, "User", "user@user.com"));
        lenient().when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                eq(userId),
                eq(LocalDateTime.now().minusDays(2)),
                eq(LocalDateTime.now().plusDays(2)),
                any(PageRequest.class)
        )).thenReturn(bookings);
        List<BookingDto> result = bookingService.findAllBookingsByUserId(userId, state, from, size);
        assertEquals(bookings.size(), result.size(),
                "Неправильное количество бронирований, найденных по userId и state CURRENT.");
    }

    @Test
    public void testFindAllBookingsByUserIdAllStatePast() {
        int userId = 1;
        String state = "PAST";
        int from = 0;
        int size = 10;
        List<Booking> bookings = testCreateBookingList(userId, 0);
        when(userService.getUserById(userId)).thenReturn(new User(userId, "User", "user@user.com"));
        lenient().when(bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(
                eq(userId),
                eq(LocalDateTime.now().minusDays(2)),
                any(PageRequest.class)
        )).thenReturn(bookings);
        List<BookingDto> result = bookingService.findAllBookingsByUserId(userId, state, from, size);
        assertEquals(bookings.size(), result.size(),
                "Неправильное количество бронирований, найденных по userId и state PAST.");
    }

    @Test
    public void testFindAllBookingsByUserIdAllStateFuture() {
        int userId = 1;
        String state = "FUTURE";
        int from = 0;
        int size = 10;
        List<Booking> bookings = testCreateBookingList(userId, 0);
        when(userService.getUserById(userId)).thenReturn(new User(userId, "User", "user@user.com"));
        lenient().when(bookingRepository.findAllByBookerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(
                eq(userId),
                eq(LocalDateTime.now().plusDays(1)),
                eq(LocalDateTime.now().plusDays(2)),
                any(PageRequest.class)
        )).thenReturn(bookings);
        List<BookingDto> result = bookingService.findAllBookingsByUserId(userId, state, from, size);
        assertEquals(bookings.size(), result.size(),
                "Неправильное количество бронирований, найденных по userId и state FUTURE.");
    }

    @Test
    public void testFindAllBookingsByUserIdAllStateWaiting() {
        int userId = 1;
        String state = "WAITING";
        int from = 0;
        int size = 10;
        List<Booking> bookings = testCreateBookingList(userId, 0);
        when(userService.getUserById(userId)).thenReturn(new User(userId, "User", "user@user.com"));
        lenient().when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                eq(userId),
                eq(Status.WAITING),
                any(PageRequest.class)
        )).thenReturn(bookings);
        List<BookingDto> result = bookingService.findAllBookingsByUserId(userId, state, from, size);
        assertEquals(bookings.size(), result.size(),
                "Неправильное количество бронирований, найденных по userId и state WAITING.");
    }

    @Test
    public void testFindAllBookingsByUserIdAllStateRejected() {
        int userId = 1;
        String state = "REJECTED";
        int from = 0;
        int size = 10;
        List<Booking> bookings = testCreateBookingList(userId, 0);
        when(userService.getUserById(userId)).thenReturn(new User(userId, "User", "user@user.com"));
        lenient().when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                eq(userId),
                eq(Status.REJECTED),
                any(PageRequest.class)
        )).thenReturn(bookings);
        List<BookingDto> result = bookingService.findAllBookingsByUserId(userId, state, from, size);
        assertEquals(bookings.size(), result.size(),
                "Неправильное количество бронирований, найденных по userId и state REJECTED.");
    }

    @Test
    public void testFindAllBookingsByOwnerIdAllStateAll() {
        int userId = 1;
        String state = "ALL";
        int from = 0;
        int size = 10;
        List<Booking> bookings = testCreateBookingList(userId, 5);
        when(userService.getUserById(userId)).thenReturn(new User(userId, "User", "user@user.com"));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(eq(userId), any(PageRequest.class))).thenReturn(bookings);
        List<BookingDto> result = bookingService.findAllBookingsByOwnerId(state, userId, from, size);
        assertEquals(bookings.size(), result.size(),
                "Неправильное количество бронирований, найденных по ownerId и state ALL.");
    }

    @Test
    public void testFindAllBookingsByOwnerIdAllStateCurrent() {
        int userId = 1;
        String state = "CURRENT";
        int from = 0;
        int size = 10;
        List<Booking> bookings = testCreateBookingList(userId, 0);
        when(userService.getUserById(userId)).thenReturn(new User(userId, "User", "user@user.com"));
        lenient().when(bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                eq(userId),
                eq(LocalDateTime.now().minusDays(2)),
                eq(LocalDateTime.now().plusDays(2)),
                any(PageRequest.class)
        )).thenReturn(bookings);
        List<BookingDto> result = bookingService.findAllBookingsByOwnerId(state, userId, from, size);
        assertEquals(bookings.size(), result.size(),
                "Неправильное количество бронирований, найденных по ownerId и state CURRENT.");
    }

    @Test
    public void testFindAllBookingsByOwnerIdAllStatePast() {
        int userId = 1;
        String state = "PAST";
        int from = 0;
        int size = 10;
        List<Booking> bookings = testCreateBookingList(userId, 0);
        when(userService.getUserById(userId)).thenReturn(new User(userId, "User", "user@user.com"));
        lenient().when(bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(
                eq(userId),
                eq(LocalDateTime.now().minusDays(2)),
                any(PageRequest.class)
        )).thenReturn(bookings);
        List<BookingDto> result = bookingService.findAllBookingsByOwnerId(state, userId, from, size);
        assertEquals(bookings.size(), result.size(),
                "Неправильное количество бронирований, найденных по ownerId и state PAST.");
    }

    @Test
    public void testFindAllBookingsByOwnerIdAllStateFuture() {
        int userId = 1;
        String state = "FUTURE";
        int from = 0;
        int size = 10;
        List<Booking> bookings = testCreateBookingList(userId, 0);
        when(userService.getUserById(userId)).thenReturn(new User(userId, "User", "user@user.com"));
        lenient().when(bookingRepository.findByItemOwnerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(
                eq(userId),
                eq(LocalDateTime.now().plusDays(1)),
                eq(LocalDateTime.now().plusDays(2)),
                any(PageRequest.class)
        )).thenReturn(bookings);
        List<BookingDto> result = bookingService.findAllBookingsByOwnerId(state, userId, from, size);
        assertEquals(bookings.size(), result.size(),
                "Неправильное количество бронирований, найденных по ownerId и state FUTURE.");
    }

    @Test
    public void testFindAllBookingsByOwnerIdAllStateWaiting() {
        int userId = 1;
        String state = "WAITING";
        int from = 0;
        int size = 10;
        List<Booking> bookings = testCreateBookingList(userId, 0);
        when(userService.getUserById(userId)).thenReturn(new User(userId, "User", "user@user.com"));
        lenient().when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                eq(userId),
                eq(Status.WAITING),
                any(PageRequest.class)
        )).thenReturn(bookings);
        List<BookingDto> result = bookingService.findAllBookingsByOwnerId(state, userId, from, size);
        assertEquals(bookings.size(), result.size(),
                "Неправильное количество бронирований, найденных по ownerId и state WAITING.");
    }

    @Test
    public void testFindAllBookingsByOwnerIdAllStateRejected() {
        int userId = 1;
        String state = "REJECTED";
        int from = 0;
        int size = 10;
        List<Booking> bookings = testCreateBookingList(userId, 0);
        when(userService.getUserById(userId)).thenReturn(new User(userId, "User", "user@user.com"));
        lenient().when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                eq(userId),
                eq(Status.REJECTED),
                any(PageRequest.class)
        )).thenReturn(bookings);
        List<BookingDto> result = bookingService.findAllBookingsByOwnerId(state, userId, from, size);
        assertEquals(bookings.size(), result.size(), "Неправильный размер списка бронирований.");
    }

    @Test
    public void testFindAllBookingsByUserIdUnknownState() {
        int userId = 1;
        String state = "UNKNOWN";
        int from = 0;
        int size = 10;
        when(userService.getUserById(userId)).thenReturn(new User(userId, "User", "user@user.com"));
        assertThrows(IncorrectParameterException.class,
                () -> bookingService.findAllBookingsByUserId(userId, state, from, size),
                "Ожидалось исключение IncorrectParameterException.");
    }

    @Test
    public void testFindAllBookingsByInvalidUserId() {
        int userId = 999;
        String state = "ALL";
        int from = 0;
        int size = 10;
        when(userService.getUserById(userId)).thenThrow(new NotFoundException("Пользователь не найден."));
        assertThrows(NotFoundException.class,
                () -> bookingService.findAllBookingsByUserId(userId, state, from, size),
                "Ожидалось исключение NotFoundException.");
    }

    private List<Booking> testCreateBookingList(int userId, int numBookings) {
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < numBookings; i++) {
            bookings.add(new Booking(i + 1, now.minusDays(i), now.minusDays(i).plusHours(1),
                    new Item(i + 1, "Item " + i, "Description " + i, true),
                    new User(userId, "User " + userId, "user" + userId + "@user.com"), Status.APPROVED));
        }
        return bookings;
    }

}