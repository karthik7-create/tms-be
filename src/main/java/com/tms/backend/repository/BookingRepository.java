package com.tms.backend.repository;

import com.tms.backend.model.entity.Booking;
import com.tms.backend.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b " +
           "JOIN FETCH b.show s " +
           "JOIN FETCH s.movie " +
           "JOIN FETCH s.screen sc " +
           "JOIN FETCH sc.theatre " +
           "WHERE b.user.id = :userId " +
           "ORDER BY b.bookedAt DESC")
    List<Booking> findByUserIdWithDetails(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b " +
           "JOIN FETCH b.show s " +
           "JOIN FETCH s.movie " +
           "JOIN FETCH s.screen sc " +
           "JOIN FETCH sc.theatre " +
           "LEFT JOIN FETCH b.bookingSeats bs " +
           "LEFT JOIN FETCH bs.seat " +
           "WHERE b.id = :id")
    Optional<Booking> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT b FROM Booking b " +
           "JOIN FETCH b.user " +
           "JOIN FETCH b.show s " +
           "JOIN FETCH s.movie " +
           "ORDER BY b.bookedAt DESC")
    List<Booking> findAllWithDetails();

    List<Booking> findByShowIdAndStatus(Long showId, BookingStatus status);
}
