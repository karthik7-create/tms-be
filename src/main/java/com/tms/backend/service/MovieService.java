// ═══ FILE: src/main/java/com/tms/backend/service/MovieService.java ═══
package com.tms.backend.service;

import com.tms.backend.dto.request.MovieRequest;
import com.tms.backend.dto.response.MovieResponse;
import com.tms.backend.exception.ResourceNotFoundException;
import com.tms.backend.model.entity.Movie;
import com.tms.backend.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    // ── Public: list active movies with optional genre/language filter ──
    @Transactional(readOnly = true)
    public List<MovieResponse> getAllMovies(String genre, String language) {
        List<Movie> movies;

        if (genre != null && language != null) {
            movies = movieRepository.findByGenreIgnoreCaseAndLanguageIgnoreCaseAndIsActiveTrue(genre, language);
        } else if (genre != null) {
            movies = movieRepository.findByGenreIgnoreCaseAndIsActiveTrue(genre);
        } else if (language != null) {
            movies = movieRepository.findByLanguageIgnoreCaseAndIsActiveTrue(language);
        } else {
            movies = movieRepository.findByIsActiveTrue();
        }

        return movies.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Public: get single movie by ID ──
    @Transactional(readOnly = true)
    public MovieResponse getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", id));
        return toResponse(movie);
    }

    // ── Public: search movies by title or genre ──
    @Transactional(readOnly = true)
    public List<MovieResponse> searchMovies(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllMovies(null, null);
        }
        return movieRepository.searchByTitleOrGenre(query.trim())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Admin: add a new movie ──
    @Transactional
    public MovieResponse createMovie(MovieRequest request) {
        Movie movie = Movie.builder()
                .title(request.getTitle())
                .genre(request.getGenre())
                .language(request.getLanguage())
                .durationMin(request.getDurationMin())
                .posterUrl(request.getPosterUrl())
                .description(request.getDescription())
                .releaseDate(request.getReleaseDate())
                .isActive(true)
                .build();
        return toResponse(movieRepository.save(movie));
    }

    // ── Admin: update existing movie ──
    @Transactional
    public MovieResponse updateMovie(Long id, MovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", id));

        movie.setTitle(request.getTitle());
        movie.setGenre(request.getGenre());
        movie.setLanguage(request.getLanguage());
        movie.setDurationMin(request.getDurationMin());
        movie.setPosterUrl(request.getPosterUrl());
        movie.setDescription(request.getDescription());
        movie.setReleaseDate(request.getReleaseDate());

        return toResponse(movieRepository.save(movie));
    }

    // ── Admin: soft-delete movie (set isActive = false) ──
    @Transactional
    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", id));
        movie.setIsActive(false);
        movieRepository.save(movie);
    }

    // ── Entity → Response DTO mapper ──
    private MovieResponse toResponse(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .genre(movie.getGenre())
                .language(movie.getLanguage())
                .durationMin(movie.getDurationMin())
                .posterUrl(movie.getPosterUrl())
                .description(movie.getDescription())
                .releaseDate(movie.getReleaseDate())
                .isActive(movie.getIsActive())
                .build();
    }
}
