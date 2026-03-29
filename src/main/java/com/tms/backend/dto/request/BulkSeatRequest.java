// ═══ FILE: src/main/java/com/tms/backend/dto/request/BulkSeatRequest.java ═══
package com.tms.backend.dto.request;

import com.tms.backend.model.enums.SeatType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request body for POST /api/admin/screens/{id}/seats — bulk seat creation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkSeatRequest {

    @NotEmpty(message = "Seat list must not be empty")
    @Valid
    private List<SeatEntry> seats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatEntry {

        @NotBlank(message = "Seat number is required")
        @Size(max = 10, message = "Seat number must not exceed 10 characters")
        private String seatNumber;

        @NotNull(message = "Seat type is required")
        private SeatType seatType;
    }
}
