package com.tms.backend.repository;

import com.tms.backend.model.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {

    @Query("SELECT s FROM Show s " +
           "JOIN FETCH s.movie " +
           "JOIN FETCH s.screen sc " +
           "JOIN FETCH sc.theatre " +
           "WHERE s.id = :id")
    Optional<Show> findByIdWithDetails(@Param("id") Long id);
}
