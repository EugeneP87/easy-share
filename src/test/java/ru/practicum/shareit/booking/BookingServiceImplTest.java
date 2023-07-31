package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.PartialBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private ItemServiceImpl itemService;

    @Mock
    private BookingRepository bookingRepository;

    @Test
    public void findBookingById() {
        int userId = 1;
        int bookingId = 100;
        User booker = new User();
        booker.setId(userId);
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        Booking result = BookingMapper.toBooking(bookingService.findBookingById(userId, bookingId));
        assertNotNull(result);
        assertEquals(bookingId, result.getId());
        assertEquals(booking, result);
    }

    @Test
    public void findBookingById_NotFound() {
        int userId = 1;
        int bookingId = 100;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> bookingService.findBookingById(userId, bookingId));
    }

    @Test
    public void findBookingById_NotOwner() {
        int userId = 1;
        int bookingId = 100;
        User booker = new User();
        booker.setId(userId);
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        assertDoesNotThrow(() -> bookingService.findBookingById(userId, bookingId));
    }

    @Test
    public void toBookingDto() {
        int id = 1;
        LocalDateTime start = LocalDateTime.of(2023, 7, 31, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 8, 1, 12, 0);
        Item item = new Item();
        User booker = new User();
        Status status = Status.APPROVED;
        Booking booking = new Booking(id, start, end, item, booker, status);
        BookingDto result = BookingMapper.toBookingDto(booking);
        assertEquals(id, result.getId());
        assertEquals(start, result.getStart());
        assertEquals(end, result.getEnd());
        assertEquals(item, result.getItem());
        assertEquals(booker, result.getBooker());
        assertEquals(status, result.getStatus());
    }

    @Test
    public void toPartialBookingDto() {
        int id = 1;
        LocalDateTime start = LocalDateTime.of(2023, 7, 31, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 8, 1, 12, 0);
        Item item = new Item(1, "item", "description", true);
        User booker = new User(1, "user", "user@user.com");
        Booking booking = new Booking(id, start, end, item, booker, Status.APPROVED);
        PartialBookingDto result = BookingMapper.toPartialBookingDto(booking);
        assertEquals(id, result.getId());
        assertEquals(start, result.getStart());
        assertEquals(end, result.getEnd());
        assertEquals(item.getId(), result.getItemId());
        assertEquals(booker.getId(), result.getBookerId());
    }

    @Test
    public void toBooking() {
        int id = 1;
        LocalDateTime start = LocalDateTime.of(2023, 7, 31, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 8, 1, 12, 0);
        Item item = new Item();
        User booker = new User();
        Status status = Status.APPROVED;
        BookingDto bookingDto = new BookingDto(id, start, end, item, booker, status);
        Booking result = BookingMapper.toBooking(bookingDto);
        assertEquals(id, result.getId());
        assertEquals(start, result.getStart());
        assertEquals(end, result.getEnd());
        assertEquals(item, result.getItem());
        assertEquals(booker, result.getBooker());
        assertEquals(status, result.getStatus());
    }

    @Test
    public void toBookingDtoList() {
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
        assertEquals(2, result.size());
        assertEquals(id1, result.get(0).getId());
        assertEquals(start1, result.get(0).getStart());
        assertEquals(end1, result.get(0).getEnd());
        assertEquals(item1, result.get(0).getItem());
        assertEquals(booker1, result.get(0).getBooker());
        assertEquals(status1, result.get(0).getStatus());
        assertEquals(id2, result.get(1).getId());
        assertEquals(start2, result.get(1).getStart());
        assertEquals(end2, result.get(1).getEnd());
        assertEquals(item2, result.get(1).getItem());
        assertEquals(booker2, result.get(1).getBooker());
        assertEquals(status2, result.get(1).getStatus());
    }

    @Test
    public void bookingItemRelationship() {
        Item item = mock(Item.class);
        Booking booking = new Booking(LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, null, Status.WAITING);
        assertEquals(item, booking.getItem());
    }

    @Test
    public void bookingUserRelationship() {
        User user = mock(User.class);
        Booking booking = new Booking(LocalDateTime.now(), LocalDateTime.now().plusHours(1), null, user, Status.WAITING);
        assertEquals(user, booking.getBooker());
    }

    @Test
    public void bookingStatus() {
        Status status = Status.APPROVED;
        Booking booking = new Booking(LocalDateTime.now(), LocalDateTime.now().plusHours(1), null, null, status);
        assertEquals(status, booking.getStatus());
    }

    @Test
    public void enumValues() {
        assertEquals(6, State.values().length);
        assertArrayEquals(new State[]{State.ALL, State.CURRENT, State.PAST, State.FUTURE, State.WAITING, State.REJECTED}, State.values());
    }

    @Test
    public void enumValue() {
        assertEquals(State.ALL, State.valueOf("ALL"));
        assertEquals(State.CURRENT, State.valueOf("CURRENT"));
        assertEquals(State.PAST, State.valueOf("PAST"));
        assertEquals(State.FUTURE, State.valueOf("FUTURE"));
        assertEquals(State.WAITING, State.valueOf("WAITING"));
        assertEquals(State.REJECTED, State.valueOf("REJECTED"));
    }

    @Test
    public void invalidEnumValue() {
        assertThrows(IllegalArgumentException.class, () -> State.valueOf("INVALID_STATE"));
    }

}