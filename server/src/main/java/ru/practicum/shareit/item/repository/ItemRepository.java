package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findAllByOwnerId(int userId, PageRequest pages);

    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE lower(i.name) like lower(concat('%', ?1, '%')) " +
            "OR lower(i.description) like lower(concat('%', ?1, '%')) " +
            "AND i.available = TRUE")
    List<Item> getAvailableItems(String text, PageRequest pages);

    List<Item> findAllByRequestId(int requestId);

    List<Item> findAllByRequestIdIn(List<Integer> requestIds);

}