package org.droneshow.booking_service.repository;

import org.droneshow.booking_service.model.Booking;
import org.droneshow.booking_service.model.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByUserId(Long userId, Pageable pageable);

    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.eventDate = :date AND b.status IN ('PENDING', 'CONFIRMED')")
    List<Booking> findByEventDate(LocalDate date);

    long countByEventDate(LocalDate date);
}

