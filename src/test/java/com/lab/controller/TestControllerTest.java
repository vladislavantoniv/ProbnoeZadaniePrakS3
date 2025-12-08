package com.lab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab.dto.TestDTO;
import com.lab.dto.PageResponse;
import com.lab.entity.enums.TestStatus;
import com.lab.service.TestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TestController.class)
class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TestService testService;

    @Autowired
    private ObjectMapper objectMapper;

    private TestDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new TestDTO();
        testDTO.setId(1L);
        testDTO.setOrderId(1L);
        testDTO.setTestTypeId(1L);
        testDTO.setTestTypeName("Общий анализ крови");
        testDTO.setStatus(TestStatus.PENDING);
        testDTO.setCompletedDate(null);
        testDTO.setResult(null);
        testDTO.setReferenceValues(null);
    }

    @Test
    void getAllTests_ShouldReturnPageResponse() throws Exception {
        PageResponse<TestDTO> pageResponse = new PageResponse<>(
                List.of(testDTO),
                0,
                10,
                1,
                1,
                true
        );

        when(testService.getAllTests(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/tests")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].orderId").value(1))
                .andExpect(jsonPath("$.content[0].testTypeId").value(1))
                .andExpect(jsonPath("$.content[0].status").value("PENDING"));
    }

    @Test
    void getTestById_WhenTestExists_ShouldReturnTest() throws Exception {
        when(testService.getTestById(1L)).thenReturn(testDTO);
        mockMvc.perform(get("/api/tests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.testTypeId").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }


    @Test
    void createTest_WithValidData_ShouldCreateTest() throws Exception {
        TestDTO newTestDTO = new TestDTO();
        newTestDTO.setOrderId(2L);
        newTestDTO.setTestTypeId(3L);

        TestDTO createdTestDTO = new TestDTO();
        createdTestDTO.setId(4L);
        createdTestDTO.setOrderId(2L);
        createdTestDTO.setTestTypeId(3L);
        createdTestDTO.setTestTypeName("Биохимический анализ");
        createdTestDTO.setStatus(TestStatus.PENDING);

        when(testService.createTest(any(TestDTO.class))).thenReturn(createdTestDTO);

        mockMvc.perform(post("/api/tests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.orderId").value(2))
                .andExpect(jsonPath("$.testTypeId").value(3))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void createTest_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        TestDTO invalidTestDTO = new TestDTO();

        mockMvc.perform(post("/api/tests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.orderId").value("ID заявки обязательно"))
                .andExpect(jsonPath("$.testTypeId").value("ID типа анализа обязательно"));
    }

    @Test
    void createTest_WhenOrderNotFound_ShouldReturnBadRequest() throws Exception {

        when(testService.createTest(any(TestDTO.class)))
                .thenThrow(new RuntimeException("Заявка с ID 999 не найдена"));

        TestDTO newTestDTO = new TestDTO();
        newTestDTO.setOrderId(999L);
        newTestDTO.setTestTypeId(1L);

        mockMvc.perform(post("/api/tests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Заявка с ID 999 не найдена"));
    }

    @Test
    void updateTestResult_WithValidData_ShouldUpdateTest() throws Exception {
        TestDTO updatedTestDTO = new TestDTO();
        updatedTestDTO.setId(1L);
        updatedTestDTO.setResult("Результат: все показатели в норме");
        updatedTestDTO.setReferenceValues("Референс: 0-10");
        updatedTestDTO.setStatus(TestStatus.COMPLETED);
        updatedTestDTO.setCompletedDate(LocalDateTime.now());

        when(testService.updateTestResult(eq(1L), anyString(), anyString()))
                .thenReturn(updatedTestDTO);

        mockMvc.perform(put("/api/tests/1/result")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "result", "Результат: все показатели в норме",
                                "referenceValues", "Референс: 0-10"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.result").value("Результат: все показатели в норме"));
    }


    @Test
    void getTestsByOrderId_ShouldReturnOrderTests() throws Exception {

        PageResponse<TestDTO> pageResponse = new PageResponse<>(
                List.of(testDTO),
                0,
                10,
                1,
                1,
                true
        );

        when(testService.getTestsByOrderId(anyLong(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/tests/order/1")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].orderId").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }



    @Test
    void deleteTest_WhenTestExists_ShouldDeleteTest() throws Exception {
        doNothing().when(testService).deleteTest(1L);

        mockMvc.perform(delete("/api/tests/1"))
                .andExpect(status().isOk());

        verify(testService, times(1)).deleteTest(1L);
    }
}