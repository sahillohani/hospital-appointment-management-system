package com.hospital.appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.appointment.entity.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long>{

	boolean existsByPhoneNumber(String phoneNumber);
	
}
