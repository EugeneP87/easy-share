package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl {

    private final InMemoryItemStorage inMemoryItemStorage;

    public ItemDto create(int userId, ItemDto itemDto) {
        return inMemoryItemStorage.create(userId, itemDto);

    }

    public ItemDto update(int itemId, int userId, ItemDto itemDto) {
        return inMemoryItemStorage.update(itemId, userId, itemDto);
    }

    public ItemDto getItemById(int itemId) {
        return ItemMapper.toItemDto(inMemoryItemStorage.getItemById(itemId));
    }

    public Collection<ItemDto> findAllUserItems(int userId) {
        return inMemoryItemStorage.findAllUserItems(userId);
    }

    public Collection<ItemDto> search(String text) {
        if (!text.isEmpty()) {
            log.info("Поиск вещи по слову " + text);
            return inMemoryItemStorage.getItemsDto().stream().filter(item -> item.getAvailable()
                            && (item.getName().toLowerCase().contains(text.toLowerCase())
                            || item.getDescription().toLowerCase().contains(text.toLowerCase())))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

}