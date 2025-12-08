package com.lab.entity;

import com.lab.entity.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.REGISTERED;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Test> tests = new ArrayList<>();

    @SuppressWarnings("unused")
    public Order() {
    }

    public Order(Patient patient, String comment) {
        this.patient = patient;
        this.comment = comment;
        this.createdDate = LocalDateTime.now();
        this.status = OrderStatus.REGISTERED;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    @SuppressWarnings("unused")
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate != null ? createdDate : LocalDateTime.now();
    }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) {
        this.status = status != null ? status : OrderStatus.REGISTERED;
    }

    public String getComment() { return comment; }
    @SuppressWarnings("unused")
    public void setComment(String comment) { this.comment = comment; }

    public List<Test> getTests() { return tests; }
    public void setTests(List<Test> tests) { this.tests = tests; }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", patient=" + (patient != null ? patient.getId() : "null") +
                ", createdDate=" + createdDate +
                ", status=" + status +
                ", comment='" + comment + '\'' +
                '}';
    }
}