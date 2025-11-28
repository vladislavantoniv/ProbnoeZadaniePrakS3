package com.lab.controller;

import com.lab.entity.Patient;
import com.lab.repository.PatientRepository;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientRepository patientRepository;

    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @GetMapping
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @GetMapping("/{id}")
    public Patient getPatientById(@PathVariable Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пациент с номером: " + id + "не найден"));
    }

    @PostMapping
    public Patient createPatient(@RequestBody Patient patient) {
        return patientRepository.save(patient);
    }

    @PutMapping("/{id}")
    public Patient updatePatient(@PathVariable Long id, @RequestBody Patient patient) {
        if (!patientRepository.existsById(id)) {
            throw new RuntimeException("Пациент с номером: " + id + "не найден");
        }
        patient.setId(id);
        return patientRepository.save(patient);
    }

    @DeleteMapping("/{id}")
    public void deletePatient(@PathVariable Long id) {
        if (!patientRepository.existsById(id)) {
            throw new RuntimeException("Пациент с номером: " + id + "не найден");
        }
        patientRepository.deleteById(id);
    }

    @GetMapping("/search/lastname")
    public List<Patient> searchPatientsByLastName(@RequestParam String lastName) {
        return patientRepository.findByLastName(lastName);
    }

    @GetMapping("/search")
    public List<Patient> searchPatients(
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String middleName,
            @RequestParam(required = false) String birthDate) {

        LocalDate parsedBirthDate = null;
        if (birthDate != null && !birthDate.trim().isEmpty()) {
                parsedBirthDate = LocalDate.parse(birthDate);
        }

        List<Patient> patients = patientRepository.searchPatients(
                lastName, firstName, middleName, parsedBirthDate
        );

        return patients;
    }
}