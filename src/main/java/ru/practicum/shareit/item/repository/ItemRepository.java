package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findAllByOwnerId(int userId);

    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE lower(i.name) like lower(concat('%', ?1, '%')) " +
            "OR lower(i.description) like lower(concat('%', ?1, '%')) " +
            "AND i.available = TRUE")
    List<Item> getAvailableItems(String text);

}