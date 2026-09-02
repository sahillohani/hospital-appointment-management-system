package com.hospital.appointment.service;

import java.util.List;
import java.util.Optional;

import com.hospital.appointment.dto.PatientRequestDto;
import com.hospital.appointment.dto.PatientUpdateRequestDto;
import com.hospital.appointment.entity.Patient;

public interface PatientService {

	Patient createPatient(PatientRequestDto dto);
	List<Patient> getAllPatients();

	Patient getPatientById(Long patientId);


	Patient updatePatient(Long patientId,
	                      PatientUpdateRequestDto dto);
	void deletePatient(Long patientId);
//	Optional<Patient> findByUserUsername(String username);
}
