package com.lab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab.dto.OrderDTO;
import com.lab.dto.PageResponse;
import com.lab.entity.enums.OrderStatus;
import com.lab.service.OrderService;
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

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setPatientId(1L);
        orderDTO.setPatientFullName("Медведев Владислав Владиславович");
        orderDTO.setCreatedDate(LocalDateTime.of(2002, 11, 14, 10, 0));
        orderDTO.setStatus(OrderStatus.REGISTERED);
        orderDTO.setComment("Плановое обследование");
    }

    @Test
    void getAllOrders_ShouldReturnPageResponse() throws Exception {
        PageResponse<OrderDTO> pageResponse = new PageResponse<>(
                List.of(orderDTO),
                0,
                10,
                1,
                1,
                true
        );

        when(orderService.getAllOrders(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/orders")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].patientFullName").value("Медведев Владислав Владиславович"))
                .andExpect(jsonPath("$.content[0].status").value("REGISTERED"));
    }


    @Test
    void getOrderById_WhenOrderExists_ShouldReturnOrder() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(orderDTO);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.patientId").value(1))
                .andExpect(jsonPath("$.status").value("REGISTERED"));
    }


    @Test
    void createOrder_WithValidData_ShouldCreateOrder() throws Exception {
        OrderDTO newOrderDTO = new OrderDTO();
        newOrderDTO.setPatientId(2L);
        newOrderDTO.setComment("Срочный анализ");

        OrderDTO createdOrderDTO = new OrderDTO();
        createdOrderDTO.setId(3L);
        createdOrderDTO.setPatientId(2L);
        createdOrderDTO.setPatientFullName("Бунин Иван Алексеевич");
        createdOrderDTO.setStatus(OrderStatus.REGISTERED);
        createdOrderDTO.setComment("Срочный анализ");

        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(createdOrderDTO);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOrderDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.patientId").value(2))
                .andExpect(jsonPath("$.status").value("REGISTERED"));
    }

    @Test
    void createOrder_WithInvalidData_ShouldReturnBadRequest() throws Exception {

        OrderDTO invalidOrderDTO = new OrderDTO();
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidOrderDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.patientId").value("ID пациента обязательно"));
    }

    @Test
    void createOrder_WhenPatientNotFound_ShouldReturnBadRequest() throws Exception {
        when(orderService.createOrder(any(OrderDTO.class)))
                .thenThrow(new RuntimeException("Пациент с ID 999 не найден"));

        OrderDTO newOrderDTO = new OrderDTO();
        newOrderDTO.setPatientId(999L);
        newOrderDTO.setComment("Тест");

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOrderDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Пациент с ID 999 не найден"));
    }

    @Test
    void updateOrderStatus_WithValidStatus_ShouldUpdateStatus() throws Exception {
        OrderDTO updatedOrderDTO = new OrderDTO();
        updatedOrderDTO.setId(1L);
        updatedOrderDTO.setStatus(OrderStatus.COMPLETED);

        when(orderService.updateOrderStatus(eq(1L), eq(OrderStatus.COMPLETED)))
                .thenReturn(updatedOrderDTO);

        mockMvc.perform(put("/api/orders/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "COMPLETED"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }



    @Test
    void getOrdersByPatientId_ShouldReturnPatientOrders() throws Exception {
        PageResponse<OrderDTO> pageResponse = new PageResponse<>(
                List.of(orderDTO),
                0,
                10,
                1,
                1,
                true
        );

        when(orderService.getOrdersByPatientId(anyLong(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/orders/patient/1")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdDate")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].patientId").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }


    @Test
    void deleteOrder_WhenOrderExists_ShouldDeleteOrder() throws Exception {
        doNothing().when(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isOk());

        verify(orderService, times(1)).deleteOrder(1L);
    }
}