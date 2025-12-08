package com.lab.service.impl;

import com.lab.dto.OrderDTO;
import com.lab.dto.PageResponse;
import com.lab.entity.Order;
import com.lab.entity.Patient;
import com.lab.entity.enums.OrderStatus;
import com.lab.repository.OrderRepository;
import com.lab.repository.PatientRepository;
import com.lab.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@SuppressWarnings("unused")
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PatientRepository patientRepository;

    public OrderServiceImpl(OrderRepository orderRepository, PatientRepository patientRepository) {
        this.orderRepository = orderRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    public PageResponse<OrderDTO> getAllOrders(int page, int size, String sortBy, String direction) {
        try {
            Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Order> orderPage = orderRepository.findAll(pageable);

            List<OrderDTO> content = orderPage.getContent().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return new PageResponse<>(
                    content,
                    orderPage.getNumber(),
                    orderPage.getSize(),
                    orderPage.getTotalElements(),
                    orderPage.getTotalPages(),
                    orderPage.isLast()
            );
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении заявок: " + e.getMessage());
        }
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заявка с ID " + id + " не найдена"));
        return convertToDTO(order);
    }

    @Override
    public OrderDTO createOrder(OrderDTO orderDTO) {
        try {
            Patient patient = patientRepository.findById(orderDTO.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Пациент с ID " + orderDTO.getPatientId() + " не найден"));

            Order order = new Order(patient, orderDTO.getComment());
            Order savedOrder = orderRepository.save(order);
            return convertToDTO(savedOrder);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании заявки: " + e.getMessage());
        }
    }

    @Override
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        try {
            if (!orderRepository.existsById(id)) {
                throw new RuntimeException("Заявка с ID " + id + " не найдена");
            }

            Patient patient = patientRepository.findById(orderDTO.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Пациент с ID " + orderDTO.getPatientId() + " не найден"));

            Order order = new Order(patient, orderDTO.getComment());
            order.setId(id);
            order.setStatus(orderDTO.getStatus());
            Order updatedOrder = orderRepository.save(order);
            return convertToDTO(updatedOrder);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обновлении заявки: " + e.getMessage());
        }
    }

    @Override
    public void deleteOrder(Long id) {
        try {
            if (!orderRepository.existsById(id)) {
                throw new RuntimeException("Заявка с ID " + id + " не найдена");
            }
            orderRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении заявки: " + e.getMessage());
        }
    }

    @Override
    public PageResponse<OrderDTO> getOrdersByPatientId(Long patientId, int page, int size, String sortBy, String direction) {
        try {
            Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);

            List<Order> orders = orderRepository.findByPatientId(patientId);

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), orders.size());

            if (start > orders.size()) {
                start = orders.size();
                end = orders.size();
            }

            List<OrderDTO> content = orders.subList(start, end).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return new PageResponse<>(
                    content,
                    page,
                    size,
                    orders.size(),
                    (int) Math.ceil((double) orders.size() / size),
                    end >= orders.size()
            );
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении заявок пациента: " + e.getMessage());
        }
    }

    @Override
    public OrderDTO updateOrderStatus(Long id, OrderStatus status) {
        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Заявка с ID " + id + " не найдена"));
            order.setStatus(status);
            Order updatedOrder = orderRepository.save(order);
            return convertToDTO(updatedOrder);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обновлении статуса заявки: " + e.getMessage());
        }
    }

    private OrderDTO convertToDTO(Order order) {
        try {
            OrderDTO dto = new OrderDTO();
            dto.setId(order.getId());

            if (order.getPatient() != null) {
                dto.setPatientId(order.getPatient().getId());
                String fullName = order.getPatient().getLastName() + " " +
                        order.getPatient().getFirstName();
                if (order.getPatient().getMiddleName() != null && !order.getPatient().getMiddleName().isEmpty()) {
                    fullName += " " + order.getPatient().getMiddleName();
                }
                dto.setPatientFullName(fullName);
            } else {
                dto.setPatientId(null);
                dto.setPatientFullName("Не указан");
            }

            dto.setCreatedDate(order.getCreatedDate());
            dto.setStatus(order.getStatus());
            dto.setComment(order.getComment());
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при преобразовании заявки в DTO: " + e.getMessage());
        }
    }
}