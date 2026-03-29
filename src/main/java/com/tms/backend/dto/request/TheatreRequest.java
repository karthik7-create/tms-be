// ═══ FILE: src/main/java/com/tms/backend/dto/request/TheatreRequest.java ═══
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
public class TheatreRequest {

    @NotBlank(message = "Theatre name is required")
    @Size(max = 200, message = "Theatre name must not exceed 200 characters")
    private String name;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    private String address;

    @Min(value = 1, message = "Total screens must be at least 1")
    private Integer totalScreens;
}
