package com.lab.service.impl;

import com.lab.dto.PatientDTO;
import com.lab.dto.PageResponse;
import com.lab.entity.Patient;
import com.lab.exception.ResourceAlreadyExistsException;
import com.lab.exception.ResourceNotFoundException;
import com.lab.repository.PatientRepository;
import com.lab.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
@SuppressWarnings("unused")
@Service
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public PageResponse<PatientDTO> getAllPatients(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Patient> patientPage = patientRepository.findAll(pageable);

        List<PatientDTO> content = patientPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                patientPage.getNumber(),
                patientPage.getSize(),
                patientPage.getTotalElements(),
                patientPage.getTotalPages(),
                patientPage.isLast()
        );
    }

    @Override
    public PatientDTO getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пациент", "ID", id));
        return convertToDTO(patient);
    }

    @Override
    public PatientDTO createPatient(PatientDTO patientDTO) {
        List<Patient> existingPatients = patientRepository.findByEmail(patientDTO.getEmail());
        if (existingPatients != null && !existingPatients.isEmpty()) {
            throw new ResourceAlreadyExistsException("Пациент", "email", patientDTO.getEmail());
        }

        Patient patient = convertToEntity(patientDTO);
        Patient savedPatient = patientRepository.save(patient);
        return convertToDTO(savedPatient);
    }

    @Override
    public PatientDTO updatePatient(Long id, PatientDTO patientDTO) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Пациент", "ID", id);
        }

        // Проверка уникальности email для других пациентов
        List<Patient> patientsWithEmail = patientRepository.findByEmail(patientDTO.getEmail());
        if (patientsWithEmail != null && !patientsWithEmail.isEmpty()) {
            boolean emailBelongsToOtherPatient = patientsWithEmail.stream()
                    .anyMatch(p -> !p.getId().equals(id));
            if (emailBelongsToOtherPatient) {
                throw new ResourceAlreadyExistsException("Пациент", "email", patientDTO.getEmail());
            }
        }

        Patient patient = convertToEntity(patientDTO);
        patient.setId(id);
        Patient updatedPatient = patientRepository.save(patient);
        return convertToDTO(updatedPatient);
    }

    @Override
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Пациент", "ID", id);
        }
        patientRepository.deleteById(id);
    }

    @Override
    public PageResponse<PatientDTO> searchPatients(String lastName, String firstName,
                                                   String middleName, String birthDate,
                                                   int page, int size, String sortBy, String direction) {
        LocalDate parsedBirthDate = null;
        if (birthDate != null && !birthDate.trim().isEmpty()) {
            try {
                parsedBirthDate = LocalDate.parse(birthDate);
            } catch (Exception e) {
                throw new com.lab.exception.ValidationException("Не так указана дата");
            }
        }

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        List<Patient> patients = patientRepository.searchPatients(lastName, firstName,
                middleName, parsedBirthDate);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), patients.size());

        if (start > patients.size()) {
            start = patients.size();
            end = patients.size();
        }

        List<PatientDTO> content = patients.subList(start, end).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                page,
                size,
                patients.size(),
                (int) Math.ceil((double) patients.size() / size),
                end >= patients.size()
        );
    }

    private PatientDTO convertToDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setLastName(patient.getLastName());
        dto.setFirstName(patient.getFirstName());
        dto.setMiddleName(patient.getMiddleName());
        dto.setBirthDate(patient.getBirthDate());
        dto.setGender(patient.getGender());
        dto.setPhoneNumber(patient.getPhoneNumber());
        dto.setEmail(patient.getEmail());
        return dto;
    }

    private Patient convertToEntity(PatientDTO dto) {
        return new Patient(
                dto.getLastName(),
                dto.getFirstName(),
                dto.getMiddleName(),
                dto.getBirthDate(),
                dto.getGender(),
                dto.getPhoneNumber(),
                dto.getEmail()
        );
    }
}