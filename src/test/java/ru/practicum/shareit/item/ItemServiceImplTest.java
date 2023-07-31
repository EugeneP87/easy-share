package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @InjectMocks
    private ItemServiceImpl itemService;

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

}