package com.hospital.appointment.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hospital.appointment.entity.Patient;
import com.hospital.appointment.exception.PatientNotFoundException;
import com.hospital.appointment.repository.PatientRepository;
import com.hospital.appointment.service.PatientService;
import com.hospital.appointment.exception.PatientAlreadyExistsException;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Override
    public Patient createPatient(Patient patient) {

        if (patientRepository.existsByPhoneNumber(
                patient.getPhoneNumber())) {

            throw new PatientAlreadyExistsException(
                    "Patient already exists with this phone number");
        }

        return patientRepository.save(patient);
    }
    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Override
    public Patient getPatientById(Long patientId) {

        return patientRepository
                .findById(patientId)
                .orElseThrow(() ->
                        new PatientNotFoundException(
                                "Patient not found with id : "
                                        + patientId));
    }

    @Override
    public Patient updatePatient(
            Long patientId,
            Patient patient) {

        Patient existingPatient =
                patientRepository
                        .findById(patientId)
                        .orElseThrow(() ->
                                new PatientNotFoundException(
                                        "Patient not found with id : "
                                                + patientId));

        existingPatient.setPatientName(
                patient.getPatientName());

        existingPatient.setAge(
                patient.getAge());

        existingPatient.setPhoneNumber(
                patient.getPhoneNumber());

        return patientRepository.save(existingPatient);
    }

    @Override
    public void deletePatient(Long patientId) {

        if (!patientRepository.existsById(patientId)) {

            throw new PatientNotFoundException(
                    "Patient not found with id : "
                            + patientId);
        }

        patientRepository.deleteById(patientId);
    }
}