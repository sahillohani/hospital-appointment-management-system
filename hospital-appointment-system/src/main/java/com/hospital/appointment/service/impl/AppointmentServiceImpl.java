package com.hospital.appointment.service.impl;

import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hospital.appointment.entity.Appointment;
import com.hospital.appointment.exception.AppointmentNotFoundException;
import com.hospital.appointment.exception.DoctorNotFoundException;
import com.hospital.appointment.exception.InvalidAppointmentSlotException;
import com.hospital.appointment.exception.PatientNotFoundException;
import com.hospital.appointment.exception.SlotAlreadyBookedException;
import com.hospital.appointment.repository.AppointmentRepository;
import com.hospital.appointment.repository.DoctorRepository;
import com.hospital.appointment.repository.PatientRepository;
import com.hospital.appointment.service.AppointmentService;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Override
    public Appointment createAppointment(Appointment appointment) {

        // Validate Patient
        if (!patientRepository.existsById(
                appointment.getPatientId())) {

            throw new PatientNotFoundException(
                    "Patient not found");
        }

        // Validate Doctor
        if (!doctorRepository.existsById(
                appointment.getDoctorId())) {

            throw new DoctorNotFoundException(
                    "Doctor not found");
        }

        // Validate Working Hours
        LocalTime time = appointment.getAppointmentTime();

        if (time.isBefore(LocalTime.of(9, 0))
                || time.isAfter(LocalTime.of(18, 0))) {

            throw new InvalidAppointmentSlotException(
                    "Appointments can only be booked between 09:00 and 18:00");
        }

        // Validate 30 Minute Slots
        if (time.getMinute() != 0
                && time.getMinute() != 30) {

            throw new InvalidAppointmentSlotException(
                    "Appointments are allowed only in 30-minute intervals");
        }

        // Prevent Patient Double Booking
        boolean patientAlreadyBooked =
                appointmentRepository
                        .existsByPatientIdAndAppointmentDateAndAppointmentTime(
                                appointment.getPatientId(),
                                appointment.getAppointmentDate(),
                                appointment.getAppointmentTime());

        if (patientAlreadyBooked) {

            throw new SlotAlreadyBookedException(
                    "Patient already has an appointment for this slot");
        }

        // Prevent Doctor Double Booking
        boolean doctorAlreadyBooked =
                appointmentRepository
                        .existsByDoctorIdAndAppointmentDateAndAppointmentTime(
                                appointment.getDoctorId(),
                                appointment.getAppointmentDate(),
                                appointment.getAppointmentTime());

        if (doctorAlreadyBooked) {

            throw new SlotAlreadyBookedException(
                    "Doctor already booked for this slot");
        }

        return appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Override
    public Appointment getAppointmentById(Long appointmentId) {

        return appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() ->
                        new AppointmentNotFoundException(
                                "Appointment not found with id : "
                                        + appointmentId));
    }

    @Override
    public Appointment updateAppointment(
            Long appointmentId,
            Appointment appointment) {

        Appointment existingAppointment =
                appointmentRepository
                        .findById(appointmentId)
                        .orElseThrow(() ->
                                new AppointmentNotFoundException(
                                        "Appointment not found with id : "
                                                + appointmentId));

        existingAppointment.setPatientId(
                appointment.getPatientId());

        existingAppointment.setDoctorId(
                appointment.getDoctorId());

        existingAppointment.setAppointmentDate(
                appointment.getAppointmentDate());

        existingAppointment.setAppointmentTime(
                appointment.getAppointmentTime());

        return appointmentRepository.save(
                existingAppointment);
    }

    @Override
    public void cancelAppointment(Long appointmentId) {

        if (!appointmentRepository.existsById(
                appointmentId)) {

            throw new AppointmentNotFoundException(
                    "Appointment not found with id : "
                            + appointmentId);
        }

        appointmentRepository.deleteById(
                appointmentId);
    }
    
    @Override
    public List<Appointment> getAppointmentsByDoctorId(
            Long doctorId) {

        if (!doctorRepository.existsById(doctorId)) {

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

        if (!patientRepository.existsById(patientId)) {

            throw new PatientNotFoundException(
                    "Patient not found with id : "
                            + patientId);
        }

        return appointmentRepository
                .findByPatientId(patientId);
    }
    
}