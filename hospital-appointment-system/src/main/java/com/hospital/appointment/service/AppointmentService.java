package com.hospital.appointment.service;

import java.util.List;

import com.hospital.appointment.entity.Appointment;

public interface AppointmentService {

    Appointment createAppointment(Appointment appointment);

    List<Appointment> getAllAppointments();

    Appointment getAppointmentById(Long appointmentId);

    Appointment updateAppointment(Long appointmentId,
                                  Appointment appointment);

    void cancelAppointment(Long appointmentId);

	List<Appointment> getAppointmentsByDoctorId(Long doctorId);

	List<Appointment> getAppointmentsByPatientId(Long patientId);
}