package org.droneshow.booking_service.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.droneshow.booking_service.client.PackageServiceClient;
import org.droneshow.booking_service.dto.*;
import org.droneshow.booking_service.exception.BookingException;
import org.droneshow.booking_service.exception.ResourceNotFoundException;
import org.droneshow.booking_service.model.Booking;
import org.droneshow.booking_service.model.BookingOption;
import org.droneshow.booking_service.model.BookingStatus;
import org.droneshow.booking_service.repository.BookingOptionRepository;
import org.droneshow.booking_service.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final BookingRepository bookingRepository;
    private final BookingOptionRepository bookingOptionRepository;
    private final PackageServiceClient packageServiceClient;


    public BookingServiceImpl(BookingRepository bookingRepository,
                            BookingOptionRepository bookingOptionRepository,
                            PackageServiceClient packageServiceClient) {
        this.bookingRepository = bookingRepository;
        this.bookingOptionRepository = bookingOptionRepository;
        this.packageServiceClient = packageServiceClient;
    }
    @Override
    public BookingResponse createBooking(
            String token,
            CreateBookingRequest request) {

        // 1. Get authenticated user
        Long userId = getUserIdFromToken(token);

        // 2. Resolve package name -> package ID
        PackageResponse pkg =
                packageServiceClient.getPackageByName(
                        request.getPackageId()
                );

        if (pkg == null) {
            throw new BookingException("Package not found");
        }

        if (!Boolean.TRUE.equals(pkg.getIsActive())) {
            throw new BookingException("Package is not active");
        }

        // 3. Check daily booking limit
        long bookingsOnDay =
                bookingRepository.countByEventDate(
                        request.getEventDate()
                );

        if (bookingsOnDay >= 2) {
            throw new BookingException(
                    "Maximum bookings for this date exceeded"
            );
        }

        // 4. Check duplicate time slot
        List<Booking> existing =
                bookingRepository.findByEventDate(
                        request.getEventDate()
                );

        if (existing.stream()
                .anyMatch(b ->
                        b.getEventTime()
                                .equals(request.getEventTime()))) {

            throw new BookingException(
                    "Time slot already booked"
            );
        }

        // 5. Calculate base price from Package Service
        BigDecimal totalPrice = pkg.getBasePrice();

        // 6. Create booking
        Booking booking = Booking.builder()
                .userId(userId)
                .packageId(pkg.getId())
                .eventDate(request.getEventDate())
                .eventTime(request.getEventTime())
                .location(request.getLocation())
                .city(request.getCity())
                .guestCount(request.getGuestCount())
                .eventType(request.getEventType())
                .status(BookingStatus.PENDING)
                .totalPrice(totalPrice)
                .userNote(request.getUserNote())
                .build();

        // 7. Save booking
        booking = bookingRepository.save(booking);

        // 8. Save options
        if (request.getOptionIds() != null
                && !request.getOptionIds().isEmpty()) {

            for (Long optionId : request.getOptionIds()) {

                BookingOption option = BookingOption.builder()
                        .bookingId(booking.getId())
                        .optionId(optionId)
                        .build();

                bookingOptionRepository.save(option);
            }
        }

        return mapToResponse(booking);
    }


    @Override
    public BookingResponse getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        return mapToResponse(booking);
    }

    @Override
    public List<BookingResponse> getMyBookings(String token) {
        Long userId = getUserIdFromToken(token);
        return bookingRepository.findByUserId(userId, Pageable.unpaged())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponse> getAllBookings(String token) {
        verifyAdmin(token);
        return bookingRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponse updateBookingStatus(String token, Long bookingId, UpdateBookingStatusRequest request) {
        // Verify admin from token
        verifyAdmin(token);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        try {
            booking.setStatus(BookingStatus.valueOf(request.getStatus().toUpperCase()));
            if (request.getAdminNote() != null) {
                booking.setAdminNote(request.getAdminNote());
            }
        } catch (IllegalArgumentException e) {
            throw new BookingException("Invalid status value");
        }

        booking = bookingRepository.save(booking);
        return mapToResponse(booking);
    }

    @Override
    public void cancelBooking(String token, Long bookingId) {
        Long userId = getUserIdFromToken(token);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUserId().equals(userId)) {
            throw new BookingException("You can only cancel your own bookings");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BookingException("Can only cancel pending bookings");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    public AvailabilityResponse checkAvailability(LocalDate date) {
        long count = bookingRepository.countByEventDate(date);
        Integer maxShowsPerDay = 2;
        Boolean isAvailable = count < maxShowsPerDay;

        String reason = null;
        if (!isAvailable) {
            reason = "Maximum bookings for this date exceeded";
        }

        return AvailabilityResponse.builder()
                .date(date)
                .isAvailable(isAvailable)
                .showsBooked((int) count)
                .maxShowsPerDay(maxShowsPerDay)
                .reason(reason)
                .build();
    }

    private BigDecimal calculatePrice(CreateBookingRequest request) {
        // In a real system, this would call the Package Service
        // For now, return a default price
        return BigDecimal.valueOf(1500);
    }


    private Long getUserIdFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            throw new BookingException("Invalid token");
        }
    }

    private void verifyAdmin(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String role = claims.get("role", String.class);
            if (!"ADMIN".equals(role)) {
                throw new BookingException("Only admins can perform this action");
            }
        } catch (Exception e) {
            throw new BookingException("Invalid or expired token");
        }
    }

    private BookingResponse mapToResponse(Booking booking) {
        List<Long> optionIds = bookingOptionRepository.findByBookingId(booking.getId())
                .stream()
                .map(BookingOption::getOptionId)
                .collect(Collectors.toList());

        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .packageId(booking.getPackageId())
                .eventDate(booking.getEventDate())
                .eventTime(booking.getEventTime())
                .location(booking.getLocation())
                .city(booking.getCity())
                .guestCount(booking.getGuestCount())
                .eventType(booking.getEventType())
                .status(booking.getStatus().toString())
                .totalPrice(booking.getTotalPrice())
                .userNote(booking.getUserNote())
                .adminNote(booking.getAdminNote())
                .optionIds(optionIds)
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}

