// ═══ FILE: src/main/java/com/tms/backend/controller/AdminController.java ═══
package com.tms.backend.controller;

import com.tms.backend.dto.request.*;
import com.tms.backend.dto.response.*;
import com.tms.backend.service.MovieService;
import com.tms.backend.service.SeatService;
import com.tms.backend.service.ShowService;
import com.tms.backend.service.TheatreService;
import com.tms.backend.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin-only controller for managing movies, theatres, shows, and seats.
 * All endpoints require ADMIN role (enforced via SecurityConfig when Auth module is integrated).
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin management APIs — requires ADMIN role")
@SecurityRequirement(name = "Bearer JWT")
public class AdminController {

    private final MovieService movieService;
    private final TheatreService theatreService;
    private final ShowService showService;
    private final SeatService seatService;

    // ═══════════════════════════════════════════
    // MOVIE MANAGEMENT
    // ═══════════════════════════════════════════

    @PostMapping("/movies")
    @Operation(summary = "Add a new movie",
               description = "Creates a new movie entry. Requires ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Movie created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<MovieResponse>> createMovie(
            @Valid @RequestBody MovieRequest request) {

        MovieResponse movie = movieService.createMovie(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Movie created successfully", movie));
    }

    @PutMapping("/movies/{id}")
    @Operation(summary = "Update a movie",
               description = "Updates an existing movie by ID. Requires ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Movie updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Movie not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<ApiResponse<MovieResponse>> updateMovie(
            @Parameter(description = "Movie ID") @PathVariable Long id,
            @Valid @RequestBody MovieRequest request) {

        MovieResponse movie = movieService.updateMovie(id, request);
        return ResponseEntity.ok(ApiResponse.success("Movie updated successfully", movie));
    }

    @DeleteMapping("/movies/{id}")
    @Operation(summary = "Delete a movie (soft delete)",
               description = "Soft-deletes a movie by setting isActive to false. Requires ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Movie deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteMovie(
            @Parameter(description = "Movie ID") @PathVariable Long id) {

        movieService.deleteMovie(id);
        return ResponseEntity.ok(ApiResponse.success("Movie deleted successfully", null));
    }

    // ═══════════════════════════════════════════
    // THEATRE MANAGEMENT
    // ═══════════════════════════════════════════

    @PostMapping("/theatres")
    @Operation(summary = "Add a new theatre",
               description = "Creates a new theatre entry. Requires ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Theatre created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<ApiResponse<TheatreResponse>> createTheatre(
            @Valid @RequestBody TheatreRequest request) {

        TheatreResponse theatre = theatreService.createTheatre(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Theatre created successfully", theatre));
    }

    // ═══════════════════════════════════════════
    // SCREEN MANAGEMENT
    // ═══════════════════════════════════════════

    @PostMapping("/screens")
    @Operation(summary = "Add a new screen to a theatre",
               description = "Creates a new screen under a specified theatre. Requires ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Screen created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Theatre not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<ApiResponse<ScreenResponse>> createScreen(
            @Valid @RequestBody ScreenRequest request) {

        ScreenResponse screen = theatreService.createScreen(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Screen created successfully", screen));
    }

    // ═══════════════════════════════════════════
    // SHOW MANAGEMENT
    // ═══════════════════════════════════════════

    @PostMapping("/shows")
    @Operation(summary = "Create a new show",
               description = "Schedules a new show linking a movie to a screen at a specific time. Requires ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Show created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Movie or Screen not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<ApiResponse<ShowResponse>> createShow(
            @Valid @RequestBody ShowRequest request) {

        ShowResponse show = showService.createShow(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Show created successfully", show));
    }

    @PutMapping("/shows/{id}")
    @Operation(summary = "Update a show",
               description = "Updates an existing show's time, price, movie, or screen. Requires ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Show updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Show, Movie, or Screen not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<ApiResponse<ShowResponse>> updateShow(
            @Parameter(description = "Show ID") @PathVariable Long id,
            @Valid @RequestBody ShowRequest request) {

        ShowResponse show = showService.updateShow(id, request);
        return ResponseEntity.ok(ApiResponse.success("Show updated successfully", show));
    }

    // ═══════════════════════════════════════════
    // SEAT MANAGEMENT
    // ═══════════════════════════════════════════

    @PostMapping("/screens/{id}/seats")
    @Operation(summary = "Bulk create seats for a screen",
               description = "Creates multiple seats at once for a given screen. Requires ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Seats created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Screen not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<ApiResponse<List<SeatResponse>>> bulkCreateSeats(
            @Parameter(description = "Screen ID") @PathVariable Long id,
            @Valid @RequestBody BulkSeatRequest request) {

        List<SeatResponse> seats = seatService.bulkCreateSeats(id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Seats created successfully", seats));
    }
}
