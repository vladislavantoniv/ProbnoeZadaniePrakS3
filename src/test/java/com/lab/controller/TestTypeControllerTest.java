package com.lab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab.dto.TestTypeDTO;
import com.lab.dto.PageResponse;
import com.lab.service.TestTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TestTypeController.class)
class TestTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TestTypeService testTypeService;

    @Autowired
    private ObjectMapper objectMapper;

    private TestTypeDTO testTypeDTO;

    @BeforeEach
    void setUp() {
        testTypeDTO = new TestTypeDTO();
        testTypeDTO.setId(1L);
        testTypeDTO.setName("Общий анализ крови");
        testTypeDTO.setCode("OAK-001");
        testTypeDTO.setDescription("Анализ крови с определением основных показателей");
        testTypeDTO.setPrice(new BigDecimal("500.00"));
    }

    @Test
    void getAllTestTypes_ShouldReturnPageResponse() throws Exception {
        PageResponse<TestTypeDTO> pageResponse = new PageResponse<>(
                List.of(testTypeDTO),
                0,
                10,
                1,
                1,
                true
        );

        when(testTypeService.getAllTestTypes(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/test-types")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Общий анализ крови"))
                .andExpect(jsonPath("$.content[0].code").value("OAK-001"))
                .andExpect(jsonPath("$.content[0].price").value(500.00));
    }

    @Test
    void getTestTypeById_WhenTestTypeExists_ShouldReturnTestType() throws Exception {

        when(testTypeService.getTestTypeById(1L)).thenReturn(testTypeDTO);

        mockMvc.perform(get("/api/test-types/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Общий анализ крови"))
                .andExpect(jsonPath("$.code").value("OAK-001"));
    }

    @Test
    void createTestType_WithValidData_ShouldCreateTestType() throws Exception {
        TestTypeDTO newTestTypeDTO = new TestTypeDTO();
        newTestTypeDTO.setName("Биохимический анализ");
        newTestTypeDTO.setCode("BIO-001");
        newTestTypeDTO.setDescription("Расширенный биохимический анализ");
        newTestTypeDTO.setPrice(new BigDecimal("2500.00"));

        TestTypeDTO createdTestTypeDTO = new TestTypeDTO();
        createdTestTypeDTO.setId(2L);
        createdTestTypeDTO.setName("Биохимический анализ");
        createdTestTypeDTO.setCode("BIO-001");
        createdTestTypeDTO.setDescription("Расширенный биохимический анализ");
        createdTestTypeDTO.setPrice(new BigDecimal("2500.00"));

        when(testTypeService.createTestType(any(TestTypeDTO.class))).thenReturn(createdTestTypeDTO);

        mockMvc.perform(post("/api/test-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTestTypeDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Биохимический анализ"))
                .andExpect(jsonPath("$.code").value("BIO-001"));
    }

    @Test
    void createTestType_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        TestTypeDTO invalidTestTypeDTO = new TestTypeDTO();
        invalidTestTypeDTO.setName(null);
        invalidTestTypeDTO.setCode("");
        invalidTestTypeDTO.setPrice(new BigDecimal("-100.00"));


        mockMvc.perform(post("/api/test-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTestTypeDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.price").exists());
    }

    @Test
    void createTestType_WithDuplicateCode_ShouldReturnBadRequest() throws Exception {

        when(testTypeService.createTestType(any(TestTypeDTO.class)))
                .thenThrow(new RuntimeException("Тип анализа с кодом OAK-001 уже существует"));

        mockMvc.perform(post("/api/test-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTypeDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Тип анализа с кодом OAK-001 уже существует"));
    }

    @Test
    void updateTestType_WithValidData_ShouldUpdateTestType() throws Exception {

        TestTypeDTO updatedTestTypeDTO = new TestTypeDTO();
        updatedTestTypeDTO.setName("Общий анализ крови (расширенный)");
        updatedTestTypeDTO.setCode("OAK-001");
        updatedTestTypeDTO.setDescription("Расширенный анализ крови");
        updatedTestTypeDTO.setPrice(new BigDecimal("600.00"));

        when(testTypeService.updateTestType(eq(1L), any(TestTypeDTO.class)))
                .thenReturn(updatedTestTypeDTO);

        mockMvc.perform(put("/api/test-types/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTestTypeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Общий анализ крови (расширенный)"))
                .andExpect(jsonPath("$.price").value(600.00));
    }

    @Test
    void deleteTestType_WhenTestTypeExists_ShouldDeleteTestType() throws Exception {
        doNothing().when(testTypeService).deleteTestType(1L);

        mockMvc.perform(delete("/api/test-types/1"))
                .andExpect(status().isOk());

        verify(testTypeService, times(1)).deleteTestType(1L);
    }

}