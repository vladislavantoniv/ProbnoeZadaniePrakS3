package com.lab.controller;

import com.lab.entity.Order;
import com.lab.entity.enums.OrderStatus;
import com.lab.repository.OrderRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заявка с номером: " + id + "не найдена"));
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderRepository.save(order);
    }

    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Long id, @RequestBody Order order) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Заявка с номером: " + id + "не найдена");
        }
        order.setId(id);
        return orderRepository.save(order);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Заявка с номером: " + id + "не найдена");
        }
        orderRepository.deleteById(id);
    }

    @GetMapping("/patient/{patientId}")
    public List<Order> getOrdersByPatientId(@PathVariable Long patientId) {
        return orderRepository.findByPatientId(patientId);
    }

    @PutMapping("/{id}/status")
    public Order updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String statusStr = request.get("status");

        OrderStatus status;
        try {
            status = OrderStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Неправильный статус заявки: " + statusStr +
                    "Нужно одно из этих: REGISTERED, IN_PROGRESS, COMPLETED, CANCELED");
        }

        return orderRepository.updateStatus(id, status);
    }

    @GetMapping("/status/{status}")
    public List<Order> getOrdersByStatus(@PathVariable String status) {
        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Неправильный статус заявки: " + status +
                    "Нужно одно из этих: REGISTERED, IN_PROGRESS, COMPLETED, CANCELED");
        }
        return orderRepository.findByStatus(orderStatus);
    }
}