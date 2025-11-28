package com.lab.entity;

import com.lab.entity.enums.TestStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tests")
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_type_id", nullable = false)
    private TestType testType;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Column(columnDefinition = "TEXT")
    private String result;

    @Column(name = "reference_values", columnDefinition = "TEXT")
    private String referenceValues;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestStatus status = TestStatus.PENDING;

    // Конструкторы
    public Test() {}

    public Test(Order order, TestType testType) {
        this.order = order;
        this.testType = testType;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public TestType getTestType() { return testType; }
    public void setTestType(TestType testType) { this.testType = testType; }

    public LocalDateTime getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDateTime completedDate) { this.completedDate = completedDate; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getReferenceValues() { return referenceValues; }
    public void setReferenceValues(String referenceValues) { this.referenceValues = referenceValues; }

    public TestStatus getStatus() { return status; }
    public void setStatus(TestStatus status) { this.status = status; }
}