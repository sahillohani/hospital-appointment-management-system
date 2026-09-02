package com.hospital.appointment.contoller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.appointment.dto.DoctorRequestDto;
import com.hospital.appointment.dto.DoctorUpdateRequestDto;
import com.hospital.appointment.entity.Doctor;
import com.hospital.appointment.service.DoctorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @PostMapping
    public Doctor createDoctor(
            @RequestBody DoctorRequestDto dto) {

        return doctorService.createDoctor(dto);
    }
    @GetMapping
    public List<Doctor> getAllDoctors() {
        return doctorService.getAllDoctors();
    }

    @GetMapping("/{doctorId}")
    public Doctor getDoctorById(@PathVariable Long doctorId) {
        return doctorService.getDoctorById(doctorId);
    }

    @PutMapping("/{doctorId}")
   
    public Doctor updateDoctor(
    
    @PathVariable Long doctorId,
   
    @Valid @RequestBody DoctorUpdateRequestDto dto) {

        return doctorService.updateDoctor(doctorId, dto);
    }

    @DeleteMapping("/{doctorId}")
    public String deleteDoctor(@PathVariable Long doctorId) {

        doctorService.deleteDoctor(doctorId);

        return "Doctor deleted successfully";
    }

}
