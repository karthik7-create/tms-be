// ═══ FILE: src/main/java/com/tms/backend/controller/SeatController.java ═══
package com.tms.backend.controller;

import com.tms.backend.dto.response.SeatResponse;
import com.tms.backend.service.SeatService;
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
@RequestMapping("/api/shows")
@RequiredArgsConstructor
@Tag(name = "Seats", description = "Seat layout and availability APIs")
public class SeatController {

    private final SeatService seatService;

    @GetMapping("/{id}/seats")
    @Operation(summary = "Get seat layout for a show",
               description = "Returns all active seats for the screen where the show is playing, "
                       + "along with their availability status. Seats are ordered by seat number "
                       + "for consistent layout rendering.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Seat layout retrieved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Show not found")
    })
    public ResponseEntity<ApiResponse<List<SeatResponse>>> getSeatsForShow(
            @Parameter(description = "Show ID") @PathVariable Long id) {

        List<SeatResponse> seats = seatService.getSeatsForShow(id);
        return ResponseEntity.ok(ApiResponse.success("Seat layout retrieved successfully", seats));
    }
}
