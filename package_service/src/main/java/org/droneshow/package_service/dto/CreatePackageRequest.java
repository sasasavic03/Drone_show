package org.droneshow.package_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePackageRequest {

    @NotBlank(message = "Package name is required")
    private String name;

    private String description;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal basePrice;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    @NotNull(message = "Drone count is required")
    @Min(value = 1, message = "Must have at least 1 drone")
    private Integer droneCount;
}

