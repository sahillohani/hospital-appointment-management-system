package com.hospital.appointment.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.hospital.appointment.dto.DoctorRequestDto;
import com.hospital.appointment.dto.DoctorUpdateRequestDto;
import com.hospital.appointment.entity.Doctor;
import com.hospital.appointment.entity.Role;
import com.hospital.appointment.entity.User;
import com.hospital.appointment.exception.DoctorAlreadyExistsException;
import com.hospital.appointment.exception.DoctorNotFoundException;
import com.hospital.appointment.exception.UnauthorizedDoctorAccessException;
import com.hospital.appointment.exception.UsernameAlreadyExistsException;
import com.hospital.appointment.repository.DoctorRepository;
import com.hospital.appointment.repository.UserRepository;
import com.hospital.appointment.service.DoctorService;

@Service
public class DoctorServiceImpl implements DoctorService {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    DoctorServiceImpl.class);

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Doctor createDoctor(DoctorRequestDto dto) {

        logger.info(
                "Creating doctor with username: {}",
                dto.getUsername());

        if (userRepository.existsByUsername(
                dto.getUsername())) {

            logger.warn(
                    "Duplicate username registration attempt: {}",
                    dto.getUsername());

            throw new UsernameAlreadyExistsException(
                    "Username already exists");
        }

        if (doctorRepository.existsByPhoneNumber(
                dto.getPhoneNumber())) {

            logger.warn(
                    "Duplicate doctor registration attempt for phone number: {}",
                    dto.getPhoneNumber());

            throw new DoctorAlreadyExistsException(
                    "Doctor already exists with this phone number");
        }

        User user = new User();

        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setRole(Role.DOCTOR);

        Doctor doctor = new Doctor();

        doctor.setDoctorName(dto.getDoctorName());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setPhoneNumber(dto.getPhoneNumber());
        doctor.setUser(user);

        Doctor savedDoctor =
                doctorRepository.save(doctor);

        logger.info(
                "Doctor created successfully with id: {}",
                savedDoctor.getDoctorId());

        return savedDoctor;
    }

    @Override
    public List<Doctor> getAllDoctors() {

        logger.info("Fetching all doctors");

        return doctorRepository.findAll();
    }

    @Override
    public Doctor getDoctorById(Long doctorId) {

        logger.info(
                "Fetching doctor with id: {}",
                doctorId);

        validateDoctorOwnership(doctorId);

        return doctorRepository
                .findById(doctorId)
                .orElseThrow(() -> {

                    logger.error(
                            "Doctor not found with id: {}",
                            doctorId);

                    return new DoctorNotFoundException(
                            "Doctor not found with id : "
                                    + doctorId);
                });
    }

    @Override
    public Doctor updateDoctor(
            Long doctorId,
            DoctorUpdateRequestDto dto) {

        logger.info(
                "Updating doctor with id: {}",
                doctorId);

        validateDoctorOwnership(doctorId);

        Doctor existingDoctor =
                doctorRepository
                        .findById(doctorId)
                        .orElseThrow(() -> {

                            logger.error(
                                    "Doctor not found with id: {}",
                                    doctorId);

                            return new DoctorNotFoundException(
                                    "Doctor not found with id : "
                                            + doctorId);
                        });

        existingDoctor.setDoctorName(
                dto.getDoctorName());

        existingDoctor.setSpecialization(
                dto.getSpecialization());

        existingDoctor.setPhoneNumber(
                dto.getPhoneNumber());

        existingDoctor.getUser()
                .setUsername(dto.getUsername());

        existingDoctor.getUser()
                .setPassword(dto.getPassword());

        Doctor updatedDoctor =
                doctorRepository.save(existingDoctor);

        logger.info(
                "Doctor updated successfully with id: {}",
                doctorId);

        return updatedDoctor;
    }

    @Override
    public void deleteDoctor(Long doctorId) {

        logger.info(
                "Deleting doctor with id: {}",
                doctorId);

        validateDoctorOwnership(doctorId);

        if (!doctorRepository.existsById(doctorId)) {

            logger.error(
                    "Doctor not found with id: {}",
                    doctorId);

            throw new DoctorNotFoundException(
                    "Doctor not found with id : "
                            + doctorId);
        }

        doctorRepository.deleteById(doctorId);

        logger.info(
                "Doctor deleted successfully with id: {}",
                doctorId);
    }

    private void validateDoctorOwnership(
            Long doctorId) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String username =
                authentication.getName();

        logger.debug(
                "Validating ownership for doctorId: {} by username: {}",
                doctorId,
                username);

        // Admin bypass
        if ("admin".equals(username)) {

            logger.info(
                    "Admin access granted for doctorId: {}",
                    doctorId);

            return;
        }

        Doctor loggedInDoctor =
                doctorRepository
                        .findByUserUsername(username)
                        .orElseThrow(() -> {

                            logger.error(
                                    "Doctor profile not found for username: {}",
                                    username);

                            return new DoctorNotFoundException(
                                    "Doctor not found");
                        });

        if (!loggedInDoctor.getDoctorId()
                .equals(doctorId)) {

            logger.warn(
                    "Unauthorized doctor access attempt. LoggedInDoctorId: {}, RequestedDoctorId: {}",
                    loggedInDoctor.getDoctorId(),
                    doctorId);

            throw new UnauthorizedDoctorAccessException(
                    "You can access only your own profile");
        }

        logger.info(
                "Doctor ownership validation successful for doctorId: {}",
                doctorId);
    }
}