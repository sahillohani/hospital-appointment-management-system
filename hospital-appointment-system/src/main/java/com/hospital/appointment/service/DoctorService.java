package com.hospital.appointment.service;

import java.util.List;

import com.hospital.appointment.entity.Doctor;

public interface DoctorService {

	Doctor createDoctor(Doctor doctor);

	List<Doctor> getAllDoctors();

	Doctor getDoctorById(Long doctorId);

	Doctor updateDoctor(Long doctorId, Doctor doctor);

	void deleteDoctor(Long doctorId);
}
