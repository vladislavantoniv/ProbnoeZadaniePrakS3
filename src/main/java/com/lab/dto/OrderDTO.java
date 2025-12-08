package com.lab.dto;

import com.lab.entity.enums.OrderStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class OrderDTO {
    private Long id;

    @NotNull(message = "ID пациента обязательно")
    private Long patientId;

    private String patientFullName;
    private LocalDateTime createdDate;
    private OrderStatus status;

    @Size(max = 500, message = "Комментарий не должен превышать 500 символов")
    private String comment;

    public OrderDTO() {}

    public OrderDTO(Long patientId, String comment) {
        this.patientId = patientId;
        this.comment = comment;
        this.status = OrderStatus.REGISTERED;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    @SuppressWarnings("unused")
    public String getPatientFullName() { return patientFullName; }
    public void setPatientFullName(String patientFullName) { this.patientFullName = patientFullName; }

    @SuppressWarnings("unused")
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}