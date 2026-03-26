package com.meditrack.appointment.repository;

import com.meditrack.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByDoctorIdAndStatus(Long doctorId, Appointment.AppointmentStatus status);
    List<Appointment> findByPatientIdAndStatus(Long patientId, Appointment.AppointmentStatus status);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctorId = :doctorId AND a.appointmentDateTime BETWEEN :start AND :end")
    List<Appointment> findDoctorAppointmentsBetween(@Param("doctorId") Long doctorId, 
                                                     @Param("start") LocalDateTime start, 
                                                     @Param("end") LocalDateTime end);
}