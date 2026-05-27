package org.droneshow.booking_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingResponse {

    private Long id;
    private Long userId;
    private Long packageId;
    private LocalDate eventDate;
    private LocalTime eventTime;
    private String location;
    private String city;
    private Integer guestCount;
    private String eventType;
    private String status;
    private BigDecimal totalPrice;
    private String userNote;
    private String adminNote;
    private List<Long> optionIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

