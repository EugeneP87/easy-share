package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl {

    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserServiceImpl userRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRepository itemRepository, ItemRequestRepository itemRequestRepository,
                                  UserServiceImpl userRepository) {
        this.itemRepository = itemRepository;
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
    }

    public ItemRequestDto createItemRequest(int userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.getUserById(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest), new ArrayList<>());
    }

    public ItemRequestDto getItemRequestById(int userId, int requestId) {
        userRepository.getUserById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с таким ID не найден"));
        List<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());
        List<ItemDto> itemDtos = items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return ItemRequestMapper.toItemRequestDto(itemRequest, itemDtos);
    }

    public List<ItemRequestDto> getItemRequestByOwnerId(int userId) {
        userRepository.getUserById(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(userId);
        List<Integer> requestIds = itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        Map<Integer, List<Item>> itemsByRequestId = itemRepository
                .findAllByRequestIdIn(requestIds)
                .stream()
                .collect(Collectors.groupingBy((Item item) -> item.getRequest().getId()));
        return itemRequests
                .stream()
                .map((ItemRequest itemRequest) -> {
                    List<ItemDto> itemDtos = itemsByRequestId.getOrDefault(itemRequest.getId(), Collections.emptyList())
                            .stream()
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList());
                    return ItemRequestMapper.toItemRequestDto(itemRequest, itemDtos);
                })
                .collect(Collectors.toList());
    }

    public List<ItemRequestDto> findAllItemRequests(int userId, int from, int size) {
        userRepository.getUserById(userId);
        PageRequest pages = PageRequest.of(from / size, size);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdNot(userId, pages);
        List<Integer> requestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<ItemDto> items = itemRepository.findAllByRequestIdIn(requestIds)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return itemRequests.stream()
                .map((ItemRequest itemRequest) -> ItemRequestMapper.toItemRequestDto(
                        itemRequest,
                        items.stream()
                                .filter((ItemDto itemDto) -> itemDto.getRequestId() == itemRequest.getId())
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

}