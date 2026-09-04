package com.hospital.appointment.service.impl;

import java.time.LocalTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hospital.appointment.dto.AppointmentRequestDto;
import com.hospital.appointment.exception.AppointmentNotFoundException;
import com.hospital.appointment.exception.DoctorNotFoundException;
import com.hospital.appointment.exception.InvalidAppointmentSlotException;
import com.hospital.appointment.exception.PatientNotFoundException;
import com.hospital.appointment.exception.SlotAlreadyBookedException;
import com.hospital.appointment.repository.AppointmentRepository;
import com.hospital.appointment.repository.DoctorRepository;
import com.hospital.appointment.repository.PatientRepository;
import com.hospital.appointment.service.AppointmentService;
import com.hospital.appointment.dto.AppointmentUpdateRequestDto;
import com.hospital.appointment.entity.Appointment;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    AppointmentServiceImpl.class);

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Override
    public Appointment createAppointment(
            AppointmentRequestDto dto) {

        Appointment appointment =
                new Appointment();

        appointment.setPatientId(
                dto.getPatientId());

        appointment.setDoctorId(
                dto.getDoctorId());

        appointment.setAppointmentDate(
                dto.getAppointmentDate());

        appointment.setAppointmentTime(
                dto.getAppointmentTime());

        logger.info(
                "Creating appointment. PatientId: {}, DoctorId: {}",
                appointment.getPatientId(),
                appointment.getDoctorId());

        if (!patientRepository.existsById(
                appointment.getPatientId())) {

            logger.error(
                    "Patient not found with id: {}",
                    appointment.getPatientId());

            throw new PatientNotFoundException(
                    "Patient not found");
        }

        if (!doctorRepository.existsById(
                appointment.getDoctorId())) {

            logger.error(
                    "Doctor not found with id: {}",
                    appointment.getDoctorId());

            throw new DoctorNotFoundException(
                    "Doctor not found");
        }

        LocalTime time =
                appointment.getAppointmentTime();

        if (time.isBefore(LocalTime.of(9, 0))
                || time.isAfter(LocalTime.of(18, 0))) {

            logger.warn(
                    "Invalid appointment slot requested: {}",
                    time);

            throw new InvalidAppointmentSlotException(
                    "Appointments can only be booked between 09:00 and 18:00");
        }

        if (time.getMinute() != 0
                && time.getMinute() != 30) {

            logger.warn(
                    "Invalid appointment interval requested: {}",
                    time);

            throw new InvalidAppointmentSlotException(
                    "Appointments are allowed only in 30-minute intervals");
        }

        boolean patientAlreadyBooked =
                appointmentRepository
                        .existsByPatientIdAndAppointmentDateAndAppointmentTime(
                                appointment.getPatientId(),
                                appointment.getAppointmentDate(),
                                appointment.getAppointmentTime());

        if (patientAlreadyBooked) {

            logger.warn(
                    "Patient double booking attempt. PatientId: {}",
                    appointment.getPatientId());

            throw new SlotAlreadyBookedException(
                    "Patient already has an appointment for this slot");
        }

        boolean doctorAlreadyBooked =
                appointmentRepository
                        .existsByDoctorIdAndAppointmentDateAndAppointmentTime(
                                appointment.getDoctorId(),
                                appointment.getAppointmentDate(),
                                appointment.getAppointmentTime());

        if (doctorAlreadyBooked) {

            logger.warn(
                    "Doctor double booking attempt. DoctorId: {}",
                    appointment.getDoctorId());

            throw new SlotAlreadyBookedException(
                    "Doctor already booked for this slot");
        }

        Appointment savedAppointment =
                appointmentRepository.save(
                        appointment);

        logger.info(
                "Appointment booked successfully with id: {}",
                savedAppointment.getAppointmentId());

        return savedAppointment;
    }

    @Override
    public List<Appointment> getAllAppointments() {

        logger.info(
                "Fetching all appointments");

        return appointmentRepository.findAll();
    }

    @Override
    public Appointment getAppointmentById(
            Long appointmentId) {

        logger.info(
                "Fetching appointment with id: {}",
                appointmentId);

        return appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() -> {

                    logger.error(
                            "Appointment not found with id: {}",
                            appointmentId);

                    return new AppointmentNotFoundException(
                            "Appointment not found with id : "
                                    + appointmentId);
                });
    }

    @Override
    public Appointment updateAppointment(
            Long appointmentId,
            AppointmentUpdateRequestDto dto) {

        logger.info(
                "Updating appointment with id: {}",
                appointmentId);

        Appointment existingAppointment =
                appointmentRepository
                        .findById(appointmentId)
                        .orElseThrow(() -> {

                            logger.error(
                                    "Appointment not found with id: {}",
                                    appointmentId);

                            return new AppointmentNotFoundException(
                                    "Appointment not found with id : "
                                            + appointmentId);
                        });

        existingAppointment.setPatientId(
                dto.getPatientId());

        existingAppointment.setDoctorId(
                dto.getDoctorId());

        existingAppointment.setAppointmentDate(
                dto.getAppointmentDate());

        existingAppointment.setAppointmentTime(
                dto.getAppointmentTime());

        Appointment updatedAppointment =
                appointmentRepository.save(
                        existingAppointment);

        logger.info(
                "Appointment updated successfully with id: {}",
                appointmentId);

        return updatedAppointment;
    }

    @Override
    public void cancelAppointment(
            Long appointmentId) {

        logger.info(
                "Cancelling appointment with id: {}",
                appointmentId);

        if (!appointmentRepository.existsById(
                appointmentId)) {

            logger.error(
                    "Appointment not found with id: {}",
                    appointmentId);

            throw new AppointmentNotFoundException(
                    "Appointment not found with id : "
                            + appointmentId);
        }

        appointmentRepository.deleteById(
                appointmentId);

        logger.info(
                "Appointment cancelled successfully with id: {}",
                appointmentId);
    }

    @Override
    public List<Appointment> getAppointmentsByDoctorId(
            Long doctorId) {

        logger.info(
                "Fetching appointments for doctorId: {}",
                doctorId);

        if (!doctorRepository.existsById(
                doctorId)) {

            logger.error(
                    "Doctor not found with id: {}",
                    doctorId);

            throw new DoctorNotFoundException(
                    "Doctor not found with id : "
                            + doctorId);
        }

        return appointmentRepository.findByDoctorId(
                doctorId);
    }

    @Override
    public List<Appointment> getAppointmentsByPatientId(
            Long patientId) {

        logger.info(
                "Fetching appointments for patientId: {}",
                patientId);

        if (!patientRepository.existsById(
                patientId)) {

            logger.error(
                    "Patient not found with id: {}",
                    patientId);

            throw new PatientNotFoundException(
                    "Patient not found with id : "
                            + patientId);
        }

        return appointmentRepository.findByPatientId(
                patientId);
    }
}