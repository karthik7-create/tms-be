// ═══ FILE: src/main/java/com/tms/backend/dto/response/MovieResponse.java ═══
package com.tms.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponse {

    private Long id;
    private String title;
    private String genre;
    private String language;
    private Integer durationMin;
    private String posterUrl;
    private String description;
    private LocalDate releaseDate;
    private Boolean isActive;
}
