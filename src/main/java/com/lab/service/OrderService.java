package com.lab.service;

import com.lab.dto.OrderDTO;
import com.lab.dto.PageResponse;
import com.lab.entity.enums.OrderStatus;

public interface OrderService {
    PageResponse<OrderDTO> getAllOrders(int page, int size, String sortBy, String direction);
    OrderDTO getOrderById(Long id);
    OrderDTO createOrder(OrderDTO orderDTO);
    OrderDTO updateOrder(Long id, OrderDTO orderDTO);
    void deleteOrder(Long id);
    PageResponse<OrderDTO> getOrdersByPatientId(Long patientId, int page, int size, String sortBy, String direction);
    OrderDTO updateOrderStatus(Long id, OrderStatus status);
}