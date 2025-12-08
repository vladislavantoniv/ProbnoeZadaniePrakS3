package com.lab.repository;

import com.lab.entity.Order;
import com.lab.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByPatientId(Long patientId);

    @SuppressWarnings("unused")
    Page<Order> findByPatientId(Long patientId, Pageable pageable);


    @SuppressWarnings("unused")
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    @SuppressWarnings("unused")
    default Order updateStatus(Long id, OrderStatus status) {
        Order order = findById(id).orElseThrow(() -> new RuntimeException("Заявка не найдена"));
        order.setStatus(status);
        return save(order);
    }
}