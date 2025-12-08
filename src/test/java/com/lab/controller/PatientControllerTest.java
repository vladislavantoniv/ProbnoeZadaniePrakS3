package com.lab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab.dto.PatientDTO;
import com.lab.dto.PageResponse;
import com.lab.entity.enums.Gender;
import com.lab.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @Autowired
    private ObjectMapper objectMapper;

    private PatientDTO patientDTO;

    @BeforeEach
    void setUp() {
        patientDTO = new PatientDTO();
        patientDTO.setId(1L);
        patientDTO.setLastName("Есенин");
        patientDTO.setFirstName("Сергей");
        patientDTO.setMiddleName("Александрович");
        patientDTO.setBirthDate(LocalDate.of(1895, 10, 3));
        patientDTO.setGender(Gender.MALE);
        patientDTO.setPhoneNumber("+79111234567");
        patientDTO.setEmail("ecenin@gmail.com");
    }

    @Test
    void getAllPatients_ShouldReturnPageResponse() throws Exception {
        PageResponse<PatientDTO> pageResponse = new PageResponse<>(
                List.of(patientDTO),
                0,
                10,
                1,
                1,
                true
        );

        when(patientService.getAllPatients(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/patients")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].lastName").value("Есенин"))
                .andExpect(jsonPath("$.content[0].firstName").value("Сергей"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void getPatientById_WhenPatientExists_ShouldReturnPatient() throws Exception {
        when(patientService.getPatientById(1L)).thenReturn(patientDTO);

        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.lastName").value("Есенин"))
                .andExpect(jsonPath("$.firstName").value("Сергей"))
                .andExpect(jsonPath("$.email").value("ecenin@gmail.com"));
    }

    @Test
    void createPatient_WithValidData_ShouldCreatePatient() throws Exception {
        PatientDTO newPatientDTO = new PatientDTO();
        newPatientDTO.setLastName("Бунин");
        newPatientDTO.setFirstName("Иван");
        newPatientDTO.setMiddleName("Алексеевич");
        newPatientDTO.setBirthDate(LocalDate.of(1995, 5, 15));
        newPatientDTO.setGender(Gender.MALE);
        newPatientDTO.setEmail("bunin@gmail.com");

        PatientDTO createdPatientDTO = new PatientDTO();
        createdPatientDTO.setId(2L);
        createdPatientDTO.setLastName("Бунин");
        createdPatientDTO.setFirstName("Иван");
        createdPatientDTO.setMiddleName("Алексеевич");
        createdPatientDTO.setBirthDate(LocalDate.of(1995, 5, 15));
        createdPatientDTO.setGender(Gender.MALE);
        createdPatientDTO.setEmail("bunin@gmail.com");

        when(patientService.createPatient(any(PatientDTO.class))).thenReturn(createdPatientDTO);

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPatientDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.lastName").value("Бунин"))
                .andExpect(jsonPath("$.email").value("bunin@gmail.com"));
    }

    @Test
    void createPatient_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        PatientDTO invalidPatientDTO = new PatientDTO();
        invalidPatientDTO.setLastName("");
        invalidPatientDTO.setFirstName("");
        invalidPatientDTO.setEmail("invalid-email");
        invalidPatientDTO.setBirthDate(null);
        invalidPatientDTO.setGender(null);

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPatientDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.lastName").exists()) // Проверяем, что ошибка есть
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.birthDate").exists())
                .andExpect(jsonPath("$.gender").exists());
    }

    @Test
    void createPatient_WithDuplicateEmail_ShouldReturnBadRequest() throws Exception {
        when(patientService.createPatient(any(PatientDTO.class)))
                .thenThrow(new RuntimeException("Пациент с email ivanov@example.com уже существует"));

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Пациент с email ivanov@example.com уже существует"));
    }

    @Test
    void updatePatient_WithValidData_ShouldUpdatePatient() throws Exception {
        PatientDTO updatedPatientDTO = new PatientDTO();
        updatedPatientDTO.setLastName("Иванов");
        updatedPatientDTO.setFirstName("Иван");
        updatedPatientDTO.setMiddleName("Сергеевич"); // Измененное отчество
        updatedPatientDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        updatedPatientDTO.setGender(Gender.MALE);
        updatedPatientDTO.setEmail("ivanov@example.com");

        when(patientService.updatePatient(eq(1L), any(PatientDTO.class))).thenReturn(updatedPatientDTO);

        mockMvc.perform(put("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPatientDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.middleName").value("Сергеевич"));
    }



    @Test
    void deletePatient_WhenPatientExists_ShouldDeletePatient() throws Exception {
        doNothing().when(patientService).deletePatient(1L);

        mockMvc.perform(delete("/api/patients/1"))
                .andExpect(status().isOk());

        verify(patientService, times(1)).deletePatient(1L);
    }

    @Test
    void searchPatients_WithoutParameters_ShouldReturnAll() throws Exception {
        PageResponse<PatientDTO> pageResponse = new PageResponse<>(
                List.of(patientDTO),
                0,
                10,
                1,
                1,
                true
        );

        when(patientService.searchPatients(isNull(), isNull(), isNull(), isNull(),
                anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/patients/search")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].lastName").value("Есенин"));
    }
}