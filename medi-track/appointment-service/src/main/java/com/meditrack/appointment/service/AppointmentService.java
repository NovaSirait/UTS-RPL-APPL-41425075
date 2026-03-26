package com.meditrack.appointment.service;

import com.meditrack.appointment.dto.AppointmentDTO;
import com.meditrack.appointment.entity.Appointment;
import com.meditrack.appointment.entity.Availability;
import com.meditrack.appointment.repository.AppointmentRepository;
import com.meditrack.appointment.repository.AvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private AvailabilityRepository availabilityRepository;
    
    @Transactional
    public Appointment createAppointment(AppointmentDTO dto) {
        // Check if the time slot is available
        List<Availability> availabilities = availabilityRepository
            .findByDoctorIdAndStartTimeBetween(dto.getDoctorId(), 
                dto.getAppointmentDateTime(), 
                dto.getAppointmentDateTime().plusHours(1));
        
        if (availabilities.isEmpty()) {
            throw new RuntimeException("Selected time slot is not available");
        }
        
        // Check for conflicting appointments
        List<Appointment> conflicts = appointmentRepository
            .findDoctorAppointmentsBetween(dto.getDoctorId(), 
                dto.getAppointmentDateTime(), 
                dto.getAppointmentDateTime().plusHours(1));
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Doctor already has an appointment at this time");
        }
        
        Appointment appointment = new Appointment();
        appointment.setPatientId(dto.getPatientId());
        appointment.setDoctorId(dto.getDoctorId());
        appointment.setAppointmentDateTime(dto.getAppointmentDateTime());
        appointment.setReason(dto.getReason());
        appointment.setNotes(dto.getNotes());
        
        // Mark the availability slot as booked
        Availability availability = availabilities.get(0);
        availability.setBooked(true);
        availabilityRepository.save(availability);
        
        return appointmentRepository.save(appointment);
    }
    
    @Transactional
    public Appointment updateAppointment(Long id, AppointmentDTO dto) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        if (dto.getAppointmentDateTime() != null) {
            // Handle rescheduling
            appointment.setAppointmentDateTime(dto.getAppointmentDateTime());
            appointment.setStatus(Appointment.AppointmentStatus.RESCHEDULED);
        }
        
        if (dto.getReason() != null) {
            appointment.setReason(dto.getReason());
        }
        
        if (dto.getNotes() != null) {
            appointment.setNotes(dto.getNotes());
        }
        
        return appointmentRepository.save(appointment);
    }
    
    @Transactional
    public void cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }
    
    public List<Appointment> getPatientAppointments(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }
    
    public List<Appointment> getDoctorAppointments(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }
    
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }
    
    public List<Availability> getDoctorAvailability(Long doctorId) {
        return availabilityRepository.findByDoctorIdAndIsBookedFalse(doctorId);
    }
    
    @Transactional
    public Availability addAvailability(Availability availability) {
        return availabilityRepository.save(availability);
    }
}