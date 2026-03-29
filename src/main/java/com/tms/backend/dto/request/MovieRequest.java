// ═══ FILE: src/main/java/com/tms/backend/dto/request/MovieRequest.java ═══
package com.tms.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 100, message = "Genre must not exceed 100 characters")
    private String genre;

    @Size(max = 50, message = "Language must not exceed 50 characters")
    private String language;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMin;

    @Size(max = 500, message = "Poster URL must not exceed 500 characters")
    private String posterUrl;

    private String description;

    private LocalDate releaseDate;
}
