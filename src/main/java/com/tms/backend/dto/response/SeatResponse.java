// ═══ FILE: src/main/java/com/tms/backend/dto/response/SeatResponse.java ═══
package com.tms.backend.dto.response;

import com.tms.backend.model.enums.SeatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponse {

    private Long id;
    private String seatNumber;
    private SeatType seatType;
    private Boolean isActive;

    // Availability status for a specific show
    // true = available for booking, false = already locked/booked
    @Builder.Default
    private Boolean isAvailable = true;
}
