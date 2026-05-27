package org.droneshow.booking_service.controller;

import jakarta.validation.Valid;
import org.droneshow.booking_service.dto.*;
import org.droneshow.booking_service.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Create new booking
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreateBookingRequest request) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        BookingResponse response = bookingService.createBooking(token, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Booking created", response));
    }

    /**
     * Get booking by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBooking(@PathVariable Long id) {
        BookingResponse response = bookingService.getBookingById(id);
        return ResponseEntity.ok(ApiResponse.success("Booking retrieved", response));
    }

    /**
     * Update booking status (ADMIN only)
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<BookingResponse>> updateStatus(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookingStatusRequest request) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        BookingResponse response = bookingService.updateBookingStatus(token, id, request);
        return ResponseEntity.ok(ApiResponse.success("Status updated", response));
    }

    /**
     * Cancel booking
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelBooking(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        bookingService.cancelBooking(token, id);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled"));
    }

    /**
     * Check availability for date
     */
    @GetMapping("/availability")
    public ResponseEntity<ApiResponse<AvailabilityResponse>> checkAvailability(
            @RequestParam LocalDate date) {
        AvailabilityResponse response = bookingService.checkAvailability(date);
        return ResponseEntity.ok(ApiResponse.success("Availability checked", response));
    }
}

