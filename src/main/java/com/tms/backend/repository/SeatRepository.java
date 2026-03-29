package com.tms.backend.repository;

import com.tms.backend.model.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Query("SELECT s FROM Seat s WHERE s.id IN :ids AND s.isActive = true")
    List<Seat> findAllActiveByIds(@Param("ids") List<Long> ids);

    List<Seat> findByScreenId(Long screenId);
}
