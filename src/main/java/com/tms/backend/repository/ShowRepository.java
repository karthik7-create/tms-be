// ═══ FILE: src/main/java/com/tms/backend/repository/ShowRepository.java ═══
package com.tms.backend.repository;

import com.tms.backend.model.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {

    // JOIN FETCH to prevent N+1 on movie + screen + theatre
    @Query("SELECT s FROM Show s " +
           "JOIN FETCH s.movie m " +
           "JOIN FETCH s.screen sc " +
           "JOIN FETCH sc.theatre t " +
           "WHERE s.id = :id")
    Optional<Show> findByIdWithDetails(@Param("id") Long id);

    // Filter shows by movieId, city, and date — all optional
    @Query("SELECT s FROM Show s " +
           "JOIN FETCH s.movie m " +
           "JOIN FETCH s.screen sc " +
           "JOIN FETCH sc.theatre t " +
           "WHERE (:movieId IS NULL OR m.id = :movieId) " +
           "AND (:city IS NULL OR LOWER(t.city) = LOWER(:city)) " +
           "AND (:date IS NULL OR CAST(s.showTime AS localdate) = :date) " +
           "AND s.status = com.tms.backend.model.enums.ShowStatus.SCHEDULED")
    List<Show> findShowsWithFilters(@Param("movieId") Long movieId,
                                    @Param("city") String city,
                                    @Param("date") LocalDate date);

    List<Show> findByMovieId(Long movieId);

    List<Show> findByScreenId(Long screenId);
}
