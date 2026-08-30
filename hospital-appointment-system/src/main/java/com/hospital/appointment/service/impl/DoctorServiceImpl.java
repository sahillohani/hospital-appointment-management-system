package com.hospital.appointment.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hospital.appointment.entity.Doctor;
import com.hospital.appointment.exception.DoctorAlreadyExistsException;
import com.hospital.appointment.exception.DoctorNotFoundException;
import com.hospital.appointment.repository.DoctorRepository;
import com.hospital.appointment.service.DoctorService;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Override
    public Doctor createDoctor(Doctor doctor) {

        if (doctorRepository.existsByPhoneNumber(
                doctor.getPhoneNumber())) {

            throw new DoctorAlreadyExistsException(
                    "Doctor already exists with this phone number");
        }

        return doctorRepository.save(doctor);
    }

    @Override
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @Override
    public Doctor getDoctorById(Long doctorId) {

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
            Doctor doctor) {

        Doctor existingDoctor =
                doctorRepository
                        .findById(doctorId)
                        .orElseThrow(() ->
                                new DoctorNotFoundException(
                                        "Doctor not found with id : "
                                                + doctorId));

        existingDoctor.setDoctorName(
                doctor.getDoctorName());

        existingDoctor.setSpecialization(
                doctor.getSpecialization());

        existingDoctor.setPhoneNumber(
                doctor.getPhoneNumber());

        return doctorRepository.save(existingDoctor);
    }

    @Override
    public void deleteDoctor(Long doctorId) {

        if (!doctorRepository.existsById(doctorId)) {

            throw new DoctorNotFoundException(
                    "Doctor not found with id : "
                            + doctorId);
        }

        doctorRepository.deleteById(doctorId);
    }
}