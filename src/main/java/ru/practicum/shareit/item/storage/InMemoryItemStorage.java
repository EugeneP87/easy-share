package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Integer, Item> items = new HashMap<>();
    private final InMemoryUserStorage inMemoryUserStorage;
    private static int itemId = 1;

    private static int generateItemId() {
        return itemId++;
    }

    @Override
    public ItemDto create(Integer userId, ItemDto itemDto) {
        checkUserExistence(userId);
        checkItemEmptyNameAndDescription(itemDto);
        checkItemAvailability(itemDto);
        Item createItem = ItemMapper.toItem(itemDto);
        createItem.setId(generateItemId());
        createItem.setOwner(userId);
        items.put(createItem.getId(), createItem);
        log.info("Создание вещи " + createItem + " пользователем с ID " + userId);
        return ItemMapper.toItemDto(createItem);
    }

    @Override
    public ItemDto update(int itemId, int userId, ItemDto itemDto) {
        Item item = getItemById(itemId);
        if (item.getOwner() != userId) {
            throw new NotFoundException("Вещь принадлежит другому пользователю и не может быть обновлена");
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
        items.put(item.getId(), item);
        log.info("Обновление вещи " + items.get(item.getId()) + " пользователем с ID " + userId);
        return ItemMapper.toItemDto(items.get(item.getId()));
    }

    @Override
    public Collection<ItemDto> findAllUserItems(int userId) {
        log.info("Получение перечня всех вещей пользователя с ID " + userId);
        return items.values().stream().filter(item -> item.getOwner() == userId)
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public Item getItemById(int itemId) {
        Item item = items.get(itemId);
        if (item != null) {
            log.info("Получение вещи с ID " + itemId);
            return item;
        } else {
            throw new NotFoundException("Вещь для отображения не найдена");
        }
    }

    public Collection<ItemDto> getItemsDto() {
        return items.values().stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    private void checkUserExistence(Integer userId) {
        String message;
        if (userId == null || inMemoryUserStorage.getUserById(userId) == null) {
            message = "Пользователь с ID " + userId + " не существует";
            log.info(message);
            throw new NotFoundException(message);
        }
    }

    private void checkItemEmptyNameAndDescription(ItemDto itemDto) {
        String message;
        if (itemDto.getName().isEmpty()) {
            message = "У вещи обязательно должно быть название";
            log.info(message);
            throw new IncorrectParameterException(message);
        }
        if (itemDto.getDescription() == null) {
            message = "У вещи обязательно должно быть описание";
            log.info(message);
            throw new IncorrectParameterException(message);
        }
    }

    private void checkItemAvailability(ItemDto itemDto) {
        String message;
        if (itemDto.getAvailable() == null) {
            message = "У вещи обязательно должен быть статус доступности";
            log.info(message);
            throw new IncorrectParameterException(message);
        }
    }

}