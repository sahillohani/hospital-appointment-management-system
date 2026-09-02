package com.hospital.appointment.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.hospital.appointment.exception.UnauthorizedDoctorAccessException;
@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public Doctor createDoctor(DoctorRequestDto dto) {

    	if (userRepository.existsByUsername(
    	        dto.getUsername())) {

    	    throw new UsernameAlreadyExistsException(
    	            "Username already exists");}
    	
    	
        if (doctorRepository.existsByPhoneNumber(
                dto.getPhoneNumber())) {

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

        return doctorRepository.save(doctor);
    }

    @Override
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @Override
    public Doctor getDoctorById(Long doctorId) {
    	validateDoctorOwnership(doctorId);
    	
        return doctorRepository
                .findById(doctorId)
                .orElseThrow(() ->
                        new DoctorNotFoundException(
                                "Doctor not found with id : "
                                        + doctorId));
    }

    @Override
    public Doctor updateDoctor(
            Long doctorId,
            DoctorUpdateRequestDto dto) {

        validateDoctorOwnership(doctorId);

        Doctor existingDoctor =
                doctorRepository
                        .findById(doctorId)
                        .orElseThrow(() ->
                                new DoctorNotFoundException(
                                        "Doctor not found with id : "
                                                + doctorId));

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

        return doctorRepository.save(existingDoctor);
    }
    @Override
    public void deleteDoctor(Long doctorId) {
    	validateDoctorOwnership(doctorId);
    	
        if (!doctorRepository.existsById(doctorId)) {

            throw new DoctorNotFoundException(
                    "Doctor not found with id : "
                            + doctorId);
        }

        doctorRepository.deleteById(doctorId);
    }
    
    
    
    
    private void validateDoctorOwnership(
            Long doctorId) {

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

        Doctor loggedInDoctor =
                doctorRepository
                        .findByUserUsername(username)
                        .orElseThrow(() ->
                                new DoctorNotFoundException(
                                        "Doctor not found"));

        if (!loggedInDoctor.getDoctorId()
                .equals(doctorId)) {

            throw new UnauthorizedDoctorAccessException(
                    "You can access only your own profile");
        }
    }
    
}