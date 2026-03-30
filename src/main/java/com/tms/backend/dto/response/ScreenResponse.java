// ═══ FILE: src/main/java/com/tms/backend/dto/response/ScreenResponse.java ═══
package com.tms.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScreenResponse {

    private Long id;
    private Long theatreId;
    private String theatreName;
    private String screenName;
    private Integer totalSeats;
}
