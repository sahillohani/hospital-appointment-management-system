package com.hospital.appointment.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
   
    private static final Logger logger =
    LoggerFactory.getLogger(
            PatientServiceImpl.class);
    
    public Patient createPatient(PatientRequestDto dto) {
    	logger.info(
    	        "Creating patient with username: {}",
    	        dto.getUsername());
    	if (userRepository.existsByUsername(
    	        dto.getUsername())) {
    		
    		logger.warn(
    		        "Duplicate username registration attempt: {}",
    		        dto.getUsername());

    	    throw new UsernameAlreadyExistsException(
    	            "Username already exists");}
        if (patientRepository.existsByPhoneNumber(
                dto.getPhoneNumber())) {
        	
        	logger.warn(
        	        "Duplicate patient registration attempt for phone number: {}",
        	        dto.getPhoneNumber());
        	
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

//    @Override
//    public Patient getPatientById(Long patientId) {
//
//    	logger.info(
//    	        "Fetching patient details for patientId: {}",
//    	        patientId);
//    	
//        validatePatientOwnership(patientId);
//
//        return patientRepository
//                .findById(patientId)
//                .orElseThrow(() ->
//                
//                        new PatientNotFoundException(
//                                "Patient not found with id : "
//                                        + patientId));
//    }
    
    
  @Override
  public Patient getPatientById(Long patientId) {
    Optional<Patient> patient =
            patientRepository.findById(patientId);

    if (patient.isEmpty()) {

        logger.error(
                "Patient not found with id: {}",
                patientId);

        throw new PatientNotFoundException(
                "Patient not found with id : "
                        + patientId);
    }

    return patient.get();
  }
    @Override
    public Patient updatePatient(
            Long patientId,
            PatientUpdateRequestDto dto) {

        logger.info(
                "Updating patient with id: {}",
                patientId);

        validatePatientOwnership(patientId);

        Patient existingPatient =
                patientRepository
                        .findById(patientId)
                        .orElseThrow(() -> {

                            logger.error(
                                    "Patient not found with id: {}",
                                    patientId);

                            return new PatientNotFoundException(
                                    "Patient not found with id : "
                                            + patientId);
                        });

        existingPatient.setPatientName(
                dto.getPatientName());

        existingPatient.setAge(
                dto.getAge());

        existingPatient.setPhoneNumber(
                dto.getPhoneNumber());

        existingPatient.getUser()
                .setUsername(dto.getUsername());

        existingPatient.getUser()
                .setPassword(dto.getPassword());

        Patient updatedPatient =
                patientRepository.save(existingPatient);

        logger.info(
                "Patient updated successfully with id: {}",
                patientId);

        return updatedPatient;
    }

    @Override
    public void deletePatient(Long patientId) {

        logger.info(
                "Deleting patient with id: {}",
                patientId);

        validatePatientOwnership(patientId);

        if (!patientRepository.existsById(patientId)) {

            logger.error(
                    "Patient not found with id: {}",
                    patientId);

            throw new PatientNotFoundException(
                    "Patient not found with id : "
                            + patientId);
        }

        patientRepository.deleteById(patientId);

        logger.info(
                "Patient deleted successfully with id: {}",
                patientId);
    }

    private void validatePatientOwnership(
            Long patientId) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String username =
                authentication.getName();

        logger.debug(
                "Validating ownership for patientId: {} by username: {}",
                patientId,
                username);

        // Admin bypass
        if ("admin".equals(username)) {

            logger.info(
                    "Admin access granted for patientId: {}",
                    patientId);

            return;
        }

        Patient loggedInPatient =
                patientRepository
                        .findByUserUsername(username)
                        .orElseThrow(() -> {

                            logger.error(
                                    "Patient profile not found for username: {}",
                                    username);

                            return new PatientNotFoundException(
                                    "Patient not found");
                        });

        if (!loggedInPatient.getPatientId()
                .equals(patientId)) {

            logger.warn(
                    "Unauthorized patient access attempt. LoggedInPatientId: {}, RequestedPatientId: {}",
                    loggedInPatient.getPatientId(),
                    patientId);

            throw new UnauthorizedPatientAccessException(
                    "You can access only your own profile");
        }

        logger.info(
                "Patient ownership validation successful for patientId: {}",
                patientId);
    }
}