// ═══ FILE: src/main/java/com/tms/backend/dto/request/ScreenRequest.java ═══
package com.tms.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScreenRequest {

    @NotNull(message = "Theatre ID is required")
    private Long theatreId;

    @NotBlank(message = "Screen name is required")
    @Size(max = 100, message = "Screen name must not exceed 100 characters")
    private String screenName;

    @NotNull(message = "Total seats is required")
    @Min(value = 1, message = "Total seats must be at least 1")
    private Integer totalSeats;
}
