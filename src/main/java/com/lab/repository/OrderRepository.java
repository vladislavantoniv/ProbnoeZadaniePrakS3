package com.lab.repository;

import com.lab.entity.Order;
import com.lab.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByPatientId(Long patientId);

    List<Order> findByStatus(OrderStatus status);

    default Order updateStatus(Long id, OrderStatus status) {
        Order order = findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return save(order);
    }
}