package org.droneshow.booking_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {

    @NotNull(message = "Package ID is required")
    private String packageId;

    @NotNull(message = "Event date is required")
    @FutureOrPresent(message = "Event date must be in the future")
    private LocalDate eventDate;

    @NotNull(message = "Event time is required")
    private LocalTime eventTime;

    @NotBlank(message = "Location is required")
    private String location;

    private String city;

    @NotNull(message = "Guest count is required")
    @Min(value = 1, message = "At least 1 guest required")
    private Integer guestCount;

    private String eventType;
    private String userNote;
    private List<Long> optionIds;
}

