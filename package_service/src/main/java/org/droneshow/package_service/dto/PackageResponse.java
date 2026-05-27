package org.droneshow.package_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private Integer durationMinutes;
    private Integer droneCount;
    private Boolean isActive;
    private List<PackageOptionResponse> options;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PackageOptionResponse {
        private Long id;
        private String name;
        private String description;
        private BigDecimal extraPrice;
        private String optionType;
    }
}

