package org.droneshow.booking_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityResponse {

    private LocalDate date;
    private Boolean isAvailable;
    private Integer showsBooked;
    private Integer maxShowsPerDay;
    private String reason;
}

