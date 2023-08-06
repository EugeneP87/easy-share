package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByBookerIdOrderByStartDesc(int bookerId, PageRequest pages);

    List<Booking> findAllByBookerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(int bookerId, LocalDateTime start,
                                                                                LocalDateTime end, PageRequest pages);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(int bookerId, LocalDateTime end, PageRequest pages);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(int bookerId, LocalDateTime start,
                                                                              LocalDateTime end, PageRequest pages);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(int bookerId, Status status, PageRequest pages);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(int ownerId, PageRequest pages);

    List<Booking> findByItemOwnerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(int ownerId, LocalDateTime start,
                                                                                LocalDateTime end, PageRequest pages);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(int owner, LocalDateTime start,
                                                                                 LocalDateTime end, PageRequest pages);

    List<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(int ownerId, LocalDateTime end, PageRequest pages);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(int ownerId, Status status, PageRequest pages);

    @Query("SELECT b " +
            "FROM Booking b " +
            "INNER JOIN Item i ON b.item.id = i.id " +
            "WHERE i.id = :itemId " +
            "ORDER BY b.start DESC")
    List<Booking> findAllBookingsItem(int itemId);

    List<Booking> findAllByItemIdAndBookerIdAndStatusIsAndEndIsBefore(int itemId, int bookerId, Status status,
                                                                      LocalDateTime time);

}