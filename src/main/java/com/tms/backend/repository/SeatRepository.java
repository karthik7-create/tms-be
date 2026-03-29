// ═══ FILE: src/main/java/com/tms/backend/repository/SeatRepository.java ═══
package com.tms.backend.repository;

import com.tms.backend.model.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByScreenIdAndIsActiveTrue(Long screenId);

    List<Seat> findByScreenId(Long screenId);

    // Get all active seats for a screen, ordered by seat number for layout rendering
    @Query("SELECT s FROM Seat s WHERE s.screen.id = :screenId AND s.isActive = true ORDER BY s.seatNumber")
    List<Seat> findActiveSeatsForScreen(@Param("screenId") Long screenId);
}
