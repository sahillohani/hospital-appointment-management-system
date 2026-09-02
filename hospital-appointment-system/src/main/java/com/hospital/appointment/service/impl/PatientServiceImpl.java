package com.hospital.appointment.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.hospital.appointment.dto.PatientRequestDto;
import com.hospital.appointment.dto.PatientUpdateRequestDto;
import com.hospital.appointment.entity.Patient;
import com.hospital.appointment.entity.Role;
import com.hospital.appointment.entity.User;
import com.hospital.appointment.exception.PatientAlreadyExistsException;
import com.hospital.appointment.exception.PatientNotFoundException;
import com.hospital.appointment.exception.UnauthorizedPatientAccessException;
import com.hospital.appointment.exception.UsernameAlreadyExistsException;
import com.hospital.appointment.repository.PatientRepository;
import com.hospital.appointment.repository.UserRepository;
import com.hospital.appointment.service.PatientService;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public Patient createPatient(PatientRequestDto dto) {
    	if (userRepository.existsByUsername(
    	        dto.getUsername())) {

    	    throw new UsernameAlreadyExistsException(
    	            "Username already exists");}
        if (patientRepository.existsByPhoneNumber(
                dto.getPhoneNumber())) {

            throw new PatientAlreadyExistsException(
                    "Patient already exists with this phone number");
        }

        User user = new User();

        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setRole(Role.PATIENT);

        Patient patient = new Patient();

        patient.setPatientName(dto.getPatientName());
        patient.setAge(dto.getAge());
        patient.setPhoneNumber(dto.getPhoneNumber());
        patient.setUser(user);

        return patientRepository.save(patient);
    }

    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Override
    public Patient getPatientById(Long patientId) {

        validatePatientOwnership(patientId);

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
            PatientUpdateRequestDto dto) {

        validatePatientOwnership(patientId);

        Patient existingPatient =
                patientRepository
                        .findById(patientId)
                        .orElseThrow(() ->
                                new PatientNotFoundException(
                                        "Patient not found with id : "
                                                + patientId));

        existingPatient.setPatientName(
                dto.getPatientName());

        existingPatient.setAge(
        		dto.getAge());

        existingPatient.setPhoneNumber(
        		dto.getPhoneNumber());

        return patientRepository.save(existingPatient);
    }

    @Override
    public void deletePatient(Long patientId) {

        validatePatientOwnership(patientId);

        if (!patientRepository.existsById(patientId)) {

            throw new PatientNotFoundException(
                    "Patient not found with id : "
                            + patientId);
        }

        patientRepository.deleteById(patientId);
    }

    private void validatePatientOwnership(
            Long patientId) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String username =
                authentication.getName();

        // Admin bypass
        if ("admin".equals(username)) {
            return;
        }

        Patient loggedInPatient =
                patientRepository
                        .findByUserUsername(username)
                        .orElseThrow(() ->
                                new PatientNotFoundException(
                                        "Patient not found"));

        if (!loggedInPatient.getPatientId()
                .equals(patientId)) {

            throw new UnauthorizedPatientAccessException(
                    "You can access only your own profile");
        }
    }
}