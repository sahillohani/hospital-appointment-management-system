package com.hospital.appointment.service;

import java.util.List;

import com.hospital.appointment.dto.AppointmentRequestDto;
import com.hospital.appointment.dto.AppointmentUpdateRequestDto;
import com.hospital.appointment.entity.Appointment;

public interface AppointmentService {


    List<Appointment> getAllAppointments();

    Appointment getAppointmentById(Long appointmentId);

    Appointment createAppointment(
            AppointmentRequestDto dto);

    Appointment updateAppointment(
            Long appointmentId,
            AppointmentUpdateRequestDto dto);

    void cancelAppointment(Long appointmentId);

	List<Appointment> getAppointmentsByDoctorId(Long doctorId);

	List<Appointment> getAppointmentsByPatientId(Long patientId);
}