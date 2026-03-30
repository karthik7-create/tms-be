// ═══ FILE: src/main/java/com/tms/backend/dto/response/ShowResponse.java ═══
package com.tms.backend.dto.response;

import com.tms.backend.model.enums.ShowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowResponse {

    private Long id;

    // Movie info (denormalized for frontend convenience)
    private Long movieId;
    private String movieTitle;
    private String moviePosterUrl;

    // Screen + Theatre info
    private Long screenId;
    private String screenName;
    private Long theatreId;
    private String theatreName;
    private String theatreCity;

    private LocalDateTime showTime;
    private BigDecimal price;
    private ShowStatus status;
}
