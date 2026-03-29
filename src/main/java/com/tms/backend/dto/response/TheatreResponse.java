// ═══ FILE: src/main/java/com/tms/backend/dto/response/TheatreResponse.java ═══
package com.tms.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TheatreResponse {

    private Long id;
    private String name;
    private String city;
    private String address;
    private Integer totalScreens;
    private List<ScreenResponse> screens;
}
