package org.droneshow.booking_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookingStatusRequest {

    @NotBlank(message = "Status is required")
    private String status; // PENDING, CONFIRMED, COMPLETED, CANCELLED

    private String adminNote;
}

