package org.droneshow.booking_service.service;

import org.droneshow.booking_service.dto.*;

import java.time.LocalDate;

public interface BookingService {

    BookingResponse createBooking(String token, CreateBookingRequest request);

    BookingResponse getBookingById(Long bookingId);

    BookingResponse updateBookingStatus(String token, Long bookingId, UpdateBookingStatusRequest request);

    void cancelBooking(String token, Long bookingId);

    AvailabilityResponse checkAvailability(LocalDate date);
}

