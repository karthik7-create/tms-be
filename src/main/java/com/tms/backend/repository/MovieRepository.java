// ═══ FILE: src/main/java/com/tms/backend/repository/MovieRepository.java ═══
package com.tms.backend.repository;

import com.tms.backend.model.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByIsActiveTrue();

    List<Movie> findByGenreIgnoreCaseAndIsActiveTrue(String genre);

    List<Movie> findByLanguageIgnoreCaseAndIsActiveTrue(String language);

    List<Movie> findByGenreIgnoreCaseAndLanguageIgnoreCaseAndIsActiveTrue(String genre, String language);

    // Full-text style search on title and genre
    @Query("SELECT m FROM Movie m WHERE m.isActive = true AND " +
           "(LOWER(m.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(m.genre) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Movie> searchByTitleOrGenre(@Param("query") String query);
}
