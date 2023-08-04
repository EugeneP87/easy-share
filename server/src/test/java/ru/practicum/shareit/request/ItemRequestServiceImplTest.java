package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserServiceImpl userService;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequest itemRequest;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User(1, "User", "user@user.com");
        item = new Item(1, "Item", "Description", true, 1, itemRequest);
        itemRequest = new ItemRequest(1, "Request", user);
    }

    @Test
    void testCreateItemRequest() {
        when(userService.getUserById(anyInt())).thenReturn(user);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        ItemRequestDto resultDto = itemRequestService.createItemRequest(1, itemRequestDto);
        verify(userService, times(1)).getUserById(1);
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
        Assertions.assertNotNull(resultDto, "Ошибка при создании запроса на предмет: результат равен null");
    }

    @Test
    void testGetItemRequestById() {
        when(userService.getUserById(1)).thenReturn(user);
        when(itemRequestRepository.findById(1)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(itemRequest.getId())).thenReturn(Collections.singletonList(item));
        ItemRequestDto resultDto = itemRequestService.getItemRequestById(1, 1);
        verify(userService, times(1)).getUserById(1);
        verify(itemRequestRepository, times(1)).findById(1);
        verify(itemRepository, times(1)).findAllByRequestId(itemRequest.getId());
        Assertions.assertNotNull(resultDto, "Ошибка при получении запроса на предмет по ID: результат равен null");
        Assertions.assertEquals(itemRequest.getId(), resultDto.getId(), "Ошибка при получении запроса на предмет по ID: неверный ID запроса");
    }

    @Test
    void testGetItemRequestByOwnerId() {
        when(userService.getUserById(1)).thenReturn(user);
        when(itemRequestRepository.findAllByRequestorId(1)).thenReturn(Collections.singletonList(itemRequest));
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(Collections.emptyList());
        List<ItemRequestDto> resultDtos = itemRequestService.getItemRequestByOwnerId(1);
        verify(userService, times(1)).getUserById(1);
        verify(itemRequestRepository, times(1)).findAllByRequestorId(1);
        verify(itemRepository, times(1)).findAllByRequestIdIn(anyList());
        Assertions.assertNotNull(resultDtos, "Ошибка при получении запросов на предмет по ID владельца: результат равен null");
        Assertions.assertEquals(1, resultDtos.size(), "Ошибка при получении запросов на предмет по ID владельца: неверное количество запросов");
        ItemRequestDto firstItemRequestDto = resultDtos.get(0);
        Assertions.assertEquals(itemRequest.getId(), firstItemRequestDto.getId(), "Ошибка при получении запросов на предмет по ID владельца: неверный ID запроса");
    }

    @Test
    void testFindAllItemRequests() {
        when(userService.getUserById(anyInt())).thenReturn(user);
        when(itemRequestRepository.findAllByRequestorIdNot(1, PageRequest.of(0, 20)))
                .thenReturn(new ArrayList<>());
        when(itemRepository.findAllByRequestIdIn(new ArrayList<>())).thenReturn(new ArrayList<>());
        List<ItemRequestDto> resultDtos = itemRequestService.findAllItemRequests(1, 0, 20);
        verify(userService, times(1)).getUserById(1);
        verify(itemRequestRepository, times(1)).findAllByRequestorIdNot(1,
                PageRequest.of(0, 20));
        verify(itemRepository, times(1)).findAllByRequestIdIn(new ArrayList<>());
        Assertions.assertNotNull(resultDtos, "Ошибка при получении всех запросов на предмет: результат равен null");
    }

}