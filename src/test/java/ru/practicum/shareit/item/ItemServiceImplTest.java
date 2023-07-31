package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserServiceImpl userServiceImpl;
    @Captor
    ArgumentCaptor<Item> itemArgumentCaptor;
    @InjectMocks
    private ItemServiceImpl itemService;

    @BeforeEach
    public void setUp() {
        itemRepository = mock(ItemRepository.class);
        userServiceImpl = mock(UserServiceImpl.class);
        itemService = new ItemServiceImpl(bookingRepository, commentRepository, itemRepository, itemRequestRepository, userServiceImpl);
    }


    @Test
    public void createItem() {
        int userId = 1;
        ItemDto itemDto = new ItemDto(1, "Item1", "Description1", true, 1);
        User user = new User(userId, "User", "user@user.com");
        when(userServiceImpl.getUserById(userId)).thenReturn(user);
        when(itemRepository.save(any(Item.class))).thenReturn(
                new Item(1, "Item1", "Description1", true));
        ItemDto result = itemService.create(userId, itemDto);
        verify(userServiceImpl, times(1)).getUserById(userId);
        verify(itemRepository, times(1)).save(any(Item.class));
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
    }

    @Test
    public void deleteItem() {
        int itemId = 1;
        itemService.deleteById(itemId);
        verify(itemRepository, times(1)).deleteById(itemId);
    }

    @Test
    public void getItemById() {
        int itemId = 1;
        int userId = 1;
        Item item = new Item(1, "Item1", "Description1", true);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        ItemDto result = itemService.getItemById(itemId, userId);
        verify(itemRepository, times(1)).findById(itemId);
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
    }

    @Test
    public void findAllUserItems() {
        int userId = 1;
        int from = 0;
        int size = 10;
        PageRequest pages = PageRequest.of(from / size, size);
        List<Item> items = new ArrayList<>();
        items.add(new Item(1, "Item1", "Description1", true));
        items.add(new Item(2, "Item2", "Description2", false));
        when(itemRepository.findAllByOwnerId(userId, pages)).thenReturn(items);
        when(commentRepository.findAllByItemId(anyInt())).thenReturn(Collections.emptyList());
        when(itemRepository.findAllByOwnerId(userId, pages)).thenReturn(items);
        when(commentRepository.findAllByItemId(anyInt())).thenReturn(Collections.emptyList());
        List<ItemDto> result = itemService.findAllUserItems(userId, from, size);
        verify(itemRepository, times(1)).findAllByOwnerId(userId, pages);
        verify(commentRepository, times(2)).findAllByItemId(anyInt());
        assertEquals(2, result.size());
        assertEquals(items.get(0).getName(), result.get(0).getName());
        assertEquals(items.get(1).getDescription(), result.get(1).getDescription());
    }

    @Test
    public void searchItems() {
        int from = 0;
        int size = 20;
        String searchText = "Item";
        PageRequest pages = PageRequest.of(from / size, size);
        List<Item> items = new ArrayList<>();
        items.add(new Item(1, "Item1", "Description1", true));
        when(itemRepository.getAvailableItems(searchText.toLowerCase(), pages)).thenReturn(items);
        List<ItemDto> result = itemService.search(searchText, from, size);
        verify(itemRepository, times(1)).getAvailableItems(searchText.toLowerCase(), pages);
        assertEquals(1, result.size());
        assertEquals(items.get(0).getName(), result.get(0).getName());
        assertEquals(items.get(0).getDescription(), result.get(0).getDescription());
    }

    @Test
    public void getOwnerId() {
        int itemId = 1;
        int ownerId = 1;
        Item item = new Item(1, "Item1", "Description1", true);
        item.setOwnerId(ownerId);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Integer result = itemService.getOwnerId(itemId);
        verify(itemRepository, times(1)).findById(itemId);
        assertEquals(ownerId, result);
    }

    @Test
    public void updateBooking() {
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        ItemServiceImpl itemService = new ItemServiceImpl(bookingRepository, commentRepository, itemRepository, itemRequestRepository, userServiceImpl);
        int itemId = 1;
        LocalDateTime now = LocalDateTime.now();
        Item item = new Item(itemId, "Laptop", "Laptop", true);
        User user = new User(2, "User", "user@user.com");
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking(now.minusHours(2), now.minusHours(1), item, user, Status.APPROVED));
        bookings.add(new Booking(now.plusHours(1), now.plusHours(2), item, user, Status.APPROVED));
        bookings.add(new Booking(now.minusHours(3), now.minusHours(2), item, user, Status.REJECTED));
        bookings.add(new Booking(now.plusHours(2), now.plusHours(3), item, user, Status.WAITING));
        when(bookingRepository.findAllBookingsItem(anyInt())).thenReturn(bookings);
        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);
        ItemDto result = itemService.updateBooking(itemDto);
        assertEquals(0, result.getLastBooking().getId());
        assertEquals(0, result.getNextBooking().getId());
    }

    @Test
    void addCommentTest() {
        Item item = new Item(1, "Item1", "Description1", true);
        UserDto userDto = new UserDto(1, "User", "user@user.com");
        List<Booking> bookingList = List.of(new Booking(), new Booking());
        Comment comment = new Comment(1, "Comment", LocalDateTime.now());
        CommentDto commentDto = new CommentDto(1, "Text", new ItemDto(), "User", LocalDateTime.now());
        Mockito.when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        Mockito.when(userServiceImpl.getUserById(anyInt()))
                .thenReturn(UserMapper.toUser(userDto));
        Mockito.when(bookingRepository.findAllByItemIdAndBookerIdAndStatusIsAndEndIsBefore(anyInt(), anyInt(), any(), any()))
                .thenReturn(bookingList);
        Mockito.when(commentRepository.save(any()))
                .thenReturn(comment);
        Mockito.when(commentRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));
        CommentDto testComment = itemService.addComment(1, 1, commentDto);
        assertEquals(testComment.getId(), commentDto.getId());
        assertNotEquals(testComment.getItem(), commentDto.getItem());
        assertEquals(testComment.getText(), commentDto.getText());
        assertEquals(testComment.getAuthorName(), commentDto.getAuthorName());
    }

    @Test
    public void searchWithEmptyText() {
        ItemRepository itemRepositoryMock = mock(ItemRepository.class);
        ItemServiceImpl itemService = new ItemServiceImpl(bookingRepository, commentRepository, itemRepository, itemRequestRepository, userServiceImpl);
        List<ItemDto> result = itemService.search("", 0, 10);
        assertTrue(result.isEmpty());
        verifyNoInteractions(itemRepositoryMock);
    }

    @Test
    void updateItem() {
        User user = new User(1, "User", "user@user.com");
        Item item = new Item(1, "User", "Description", true, 1,
                new ItemRequest(2,
                        "Description", new User(1, "User", "user@user.com"),
                        LocalDateTime.of(2023, 7, 31, 12, 0, 0)));
        ItemDto itemDto = new ItemDto(1, "Name", "Description", true, 2);
        item.setOwnerId(1);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);
        ItemDto actualItem = itemService.update(item.getId(), user.getId(), itemDto);
        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();
        assertEquals(itemDto.getName(), savedItem.getName());
        assertEquals(itemDto.getDescription(), savedItem.getDescription());
    }

    @Test
    void updateItemNotOwner() {
        int itemId = 1;
        int userId = 1;
        Item existingItem = new Item(itemId, "User", "Description", true, 2,
                new ItemRequest(2, "Description", new User(2, "OtherUser", "otheruser@user.com"),
                        LocalDateTime.of(2023, 7, 31, 12, 0, 0)));
        ItemDto itemDto = new ItemDto(itemId, "Name", "Description", true, userId);
        existingItem.setOwnerId(2);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.update(itemId, userId, itemDto));
        assertEquals("Пользователь не является владельцем вещи", exception.getMessage());
        verify(itemRepository, times(1)).findById(itemId);
        verify(userServiceImpl, times(1)).getUserById(userId);
        verifyNoMoreInteractions(itemRepository, userServiceImpl);
    }

    @Test
    public void testUpdateItemWhenItemNotFound() {
        int itemId = 1;
        int userId = 1;
        ItemDto itemDto = new ItemDto(itemId, "Name", "Description", true, userId);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.update(itemId, userId, itemDto));
        assertEquals("Вещь не найдена", exception.getMessage());
        verify(itemRepository, times(1)).findById(itemId);
        verifyNoMoreInteractions(itemRepository, userServiceImpl);
    }

    @Test
    public void testUpdateItemWhenNoOwnerId() {
        int itemId = 1;
        int userId = 1;
        Item existingItem = new Item(itemId, "User", "Description", true, 2,
                new ItemRequest(2, "Description",
                        new User(2, "OtherUser", "otheruser@user.com"),
                        LocalDateTime.of(2023, 7, 31, 12, 0, 0)));
        ItemDto itemDto = new ItemDto(itemId, "Name", "Description", true, userId);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.update(itemId, userId, itemDto));
        assertEquals("ID владельца вещи отсутствует", exception.getMessage());
        verify(itemRepository, times(1)).findById(itemId);
        verify(userServiceImpl, times(1)).getUserById(userId);
        verifyNoMoreInteractions(itemRepository, userServiceImpl);
    }

}