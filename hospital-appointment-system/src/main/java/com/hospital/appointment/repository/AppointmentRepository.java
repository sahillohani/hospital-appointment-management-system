package com.hospital.appointment.repository;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.appointment.entity.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long>{

	
	    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTime(
	            Long doctorId,
	            LocalDate appointmentDate,
	            LocalTime appointmentTime);
	}
	
	

