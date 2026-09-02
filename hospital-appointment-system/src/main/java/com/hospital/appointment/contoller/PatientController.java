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

import com.hospital.appointment.dto.PatientRequestDto;
import com.hospital.appointment.dto.PatientUpdateRequestDto;
import com.hospital.appointment.entity.Patient;
import com.hospital.appointment.service.PatientService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping
    public Patient createPatient(
            @Valid @RequestBody PatientRequestDto dto) {

        return patientService.createPatient(dto);
    }

    @GetMapping
    public List<Patient> getAllPatients() {
        return patientService.getAllPatients();
    }

    @GetMapping("/{patientId}")
    public Patient getPatientById(@PathVariable Long patientId) {
        return patientService.getPatientById(patientId);
    }

    @PutMapping("/{patientId}")
    public Patient updatePatient(
            @PathVariable Long patientId,
            @Valid @RequestBody PatientUpdateRequestDto dto) {

        return patientService.updatePatient(
                patientId,
                dto);
    }

    @DeleteMapping("/{patientId}")
    public String deletePatient(@PathVariable Long patientId) {

        patientService.deletePatient(patientId);

        return "Patient deleted successfully";
    }
}
