package com.hospital.appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.appointment.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {


	    boolean existsByPhoneNumber(String phoneNumber);
	}
