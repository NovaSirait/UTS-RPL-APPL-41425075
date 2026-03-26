package com.meditrack.appointment.repository;

import com.meditrack.appointment.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByDoctorIdAndIsBookedFalse(Long doctorId);
    List<Availability> findByDoctorIdAndStartTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end);
}