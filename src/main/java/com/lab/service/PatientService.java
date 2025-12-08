package com.lab.service;

import com.lab.dto.PatientDTO;
import com.lab.dto.PageResponse;

public interface PatientService {
    PageResponse<PatientDTO> getAllPatients(int page, int size, String sortBy, String direction);
    PatientDTO getPatientById(Long id);
    PatientDTO createPatient(PatientDTO patientDTO);
    PatientDTO updatePatient(Long id, PatientDTO patientDTO);
    void deletePatient(Long id);
    PageResponse<PatientDTO> searchPatients(String lastName, String firstName,
                                            String middleName, String birthDate,
                                            int page, int size, String sortBy, String direction);
}