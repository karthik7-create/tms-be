// ═══ FILE: src/main/java/com/tms/backend/controller/ShowController.java ═══
package com.tms.backend.controller;

import com.tms.backend.dto.response.ShowResponse;
import com.tms.backend.dto.response.TheatreResponse;
import com.tms.backend.service.ShowService;
import com.tms.backend.service.TheatreService;
import com.tms.backend.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Shows & Theatres", description = "Public APIs for browsing shows and theatres")
public class ShowController {

    private final ShowService showService;
    private final TheatreService theatreService;

    // ═══════════════════════════════════════════
    // THEATRE ENDPOINTS
    // ═══════════════════════════════════════════

    @GetMapping("/api/theatres")
    @Operation(summary = "List all theatres",
               description = "Returns all theatres. Optionally filter by city.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Theatres retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<TheatreResponse>>> getAllTheatres(
            @Parameter(description = "Filter by city (case-insensitive)")
            @RequestParam(required = false) String city) {

        List<TheatreResponse> theatres = theatreService.getAllTheatres(city);
        return ResponseEntity.ok(ApiResponse.success("Theatres retrieved successfully", theatres));
    }

    // ═══════════════════════════════════════════
    // SHOW ENDPOINTS
    // ═══════════════════════════════════════════

    @GetMapping("/api/shows")
    @Operation(summary = "List shows with filters",
               description = "Returns scheduled shows. Filter by movieId, city, and/or date. All filters are optional.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Shows retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<ShowResponse>>> getShows(
            @Parameter(description = "Filter by movie ID")
            @RequestParam(required = false) Long movieId,
            @Parameter(description = "Filter by city (case-insensitive)")
            @RequestParam(required = false) String city,
            @Parameter(description = "Filter by date (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<ShowResponse> shows = showService.getShows(movieId, city, date);
        return ResponseEntity.ok(ApiResponse.success("Shows retrieved successfully", shows));
    }

    @GetMapping("/api/shows/{id}")
    @Operation(summary = "Get show details",
               description = "Returns detailed information about a specific show including movie, screen, and theatre data.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Show found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Show not found")
    })
    public ResponseEntity<ApiResponse<ShowResponse>> getShowById(
            @Parameter(description = "Show ID") @PathVariable Long id) {

        ShowResponse show = showService.getShowById(id);
        return ResponseEntity.ok(ApiResponse.success("Show retrieved successfully", show));
    }
}
