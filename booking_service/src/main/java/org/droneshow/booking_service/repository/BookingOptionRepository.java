package org.droneshow.booking_service.repository;

import org.droneshow.booking_service.model.BookingOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingOptionRepository extends JpaRepository<BookingOption, Long> {

    List<BookingOption> findByBookingId(Long bookingId);

    void deleteByBookingId(Long bookingId);
}

