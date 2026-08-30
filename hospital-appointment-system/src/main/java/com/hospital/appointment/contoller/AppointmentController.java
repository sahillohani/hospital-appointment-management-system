package com.hospital.appointment.contoller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hospital.appointment.entity.Appointment;
import com.hospital.appointment.service.AppointmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping
    public Appointment createAppointment(
    		@Valid  @RequestBody Appointment appointment) {

        return appointmentService.createAppointment(appointment);
    }

    @GetMapping
    public List<Appointment> getAllAppointments() {

        return appointmentService.getAllAppointments();
    }

    @GetMapping("/{appointmentId}")
    public Appointment getAppointmentById(
            @PathVariable Long appointmentId) {

        return appointmentService.getAppointmentById(
                appointmentId);
    }

    @PutMapping("/{appointmentId}")
    public Appointment updateAppointment(
            @PathVariable Long appointmentId,
            @Valid  @RequestBody Appointment appointment) {

        return appointmentService.updateAppointment(
                appointmentId,
                appointment);
    }

    @DeleteMapping("/{appointmentId}")
    public String cancelAppointment(
            @PathVariable Long appointmentId) {

        appointmentService.cancelAppointment(
                appointmentId);

        return "Appointment cancelled successfully";
    }
}