package com.hospital.appointment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.appointment.entity.Patient;
import com.hospital.appointment.entity.User;

public interface PatientRepository extends JpaRepository<Patient, Long> {


	    boolean existsByPhoneNumber(String phoneNumber);

		 Optional<User> findByUserUsername(String username);
	}
