package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    List<ItemRequest> findAllByRequestorId(int requestorId);

    List<ItemRequest> findAllByRequestorIdNot(int requestorId, PageRequest page);

}