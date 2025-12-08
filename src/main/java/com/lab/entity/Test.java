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

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Test() {}

    public Test(Order order, TestType testType) {
        this.order = order;
        this.testType = testType;
        this.status = TestStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @SuppressWarnings("unused")
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public TestType getTestType() { return testType; }
    @SuppressWarnings("unused")
    public void setTestType(TestType testType) { this.testType = testType; }

    public LocalDateTime getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDateTime completedDate) {
        this.completedDate = completedDate;
    }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getReferenceValues() { return referenceValues; }
    public void setReferenceValues(String referenceValues) { this.referenceValues = referenceValues; }

    public TestStatus getStatus() { return status; }
    public void setStatus(TestStatus status) {
        this.status = status != null ? status : TestStatus.PENDING;
        if (this.status == TestStatus.COMPLETED && this.completedDate == null) {
            this.completedDate = LocalDateTime.now();
        }
    }

    @SuppressWarnings("unused")
    public LocalDateTime getCreatedAt() { return createdAt; }
    @SuppressWarnings("unused")
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    @SuppressWarnings("unused")
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public String toString() {
        return "Test{" +
                "id=" + id +
                ", order=" + (order != null ? order.getId() : "null") +
                ", testType=" + (testType != null ? testType.getId() : "null") +
                ", completedDate=" + completedDate +
                ", result='" + result + '\'' +
                ", referenceValues='" + referenceValues + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}