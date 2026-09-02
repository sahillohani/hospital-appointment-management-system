package com.hospital.appointment.service;

import java.util.List;

import com.hospital.appointment.dto.PatientRequestDto;
import com.hospital.appointment.entity.Patient;

public interface PatientService {

	Patient createPatient(PatientRequestDto dto);
	List<Patient> getAllPatients();

	Patient getPatientById(Long patientId);

	Patient updatePatient(Long patientId, Patient patient);

	void deletePatient(Long patientId);
}
