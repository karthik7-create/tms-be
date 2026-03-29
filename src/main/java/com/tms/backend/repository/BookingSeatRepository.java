package com.tms.backend.repository;

import com.tms.backend.model.entity.BookingSeat;
import com.tms.backend.model.enums.BookingSeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {

    @Query("SELECT bs FROM BookingSeat bs " +
           "WHERE bs.seat.id IN :seatIds " +
           "AND bs.booking.show.id = :showId " +
           "AND bs.status IN ('LOCKED', 'CONFIRMED')")
    List<BookingSeat> findLockedOrConfirmedSeats(
            @Param("showId") Long showId,
            @Param("seatIds") List<Long> seatIds);

    List<BookingSeat> findByBookingId(Long bookingId);
}
