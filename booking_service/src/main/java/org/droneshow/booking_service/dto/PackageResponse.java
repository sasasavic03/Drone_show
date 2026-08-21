package org.droneshow.booking_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
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
    public static class PackageOptionResponse {

        private Long id;
        private String name;
        private String description;
        private BigDecimal extraPrice;
        private String optionType;
    }
}
