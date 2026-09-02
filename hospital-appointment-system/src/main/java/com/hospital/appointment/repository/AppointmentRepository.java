package com.hospital.appointment.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.appointment.entity.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

	boolean existsByDoctorIdAndAppointmentDateAndAppointmentTime(Long doctorId, LocalDate appointmentDate,
			LocalTime appointmentTime);
	
	boolean existsByPatientIdAndAppointmentDateAndAppointmentTime(
	        Long patientId,
	        LocalDate appointmentDate,
	        LocalTime appointmentTime);

	List<Appointment> findByDoctorId(Long doctorId);

	List<Appointment> findByPatientId(Long patientId);

}
