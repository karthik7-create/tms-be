// ═══ FILE: src/main/java/com/tms/backend/controller/MovieController.java ═══
package com.tms.backend.controller;

import com.tms.backend.dto.response.MovieResponse;
import com.tms.backend.service.MovieService;
import com.tms.backend.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Tag(name = "Movies", description = "Public movie browsing APIs")
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    @Operation(summary = "List all active movies",
               description = "Returns all active movies. Optionally filter by genre and/or language.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Movies retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getAllMovies(
            @Parameter(description = "Filter by genre (case-insensitive)")
            @RequestParam(required = false) String genre,
            @Parameter(description = "Filter by language (case-insensitive)")
            @RequestParam(required = false) String language) {

        List<MovieResponse> movies = movieService.getAllMovies(genre, language);
        return ResponseEntity.ok(ApiResponse.success("Movies retrieved successfully", movies));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get movie details",
               description = "Returns detailed information for a single movie by its ID.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Movie found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<ApiResponse<MovieResponse>> getMovieById(
            @Parameter(description = "Movie ID") @PathVariable Long id) {

        MovieResponse movie = movieService.getMovieById(id);
        return ResponseEntity.ok(ApiResponse.success("Movie retrieved successfully", movie));
    }

    @GetMapping("/search")
    @Operation(summary = "Search movies",
               description = "Search movies by title or genre using a keyword query.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search results returned")
    })
    public ResponseEntity<ApiResponse<List<MovieResponse>>> searchMovies(
            @Parameter(description = "Search query (matches title and genre)")
            @RequestParam String q) {

        List<MovieResponse> results = movieService.searchMovies(q);
        return ResponseEntity.ok(ApiResponse.success("Search results", results));
    }
}
