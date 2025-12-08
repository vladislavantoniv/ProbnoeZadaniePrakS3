package com.lab.dto;

import com.lab.entity.enums.TestStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class TestDTO {
    private Long id;

    @NotNull(message = "ID заявки обязательно")
    private Long orderId;

    @NotNull(message = "ID типа анализа обязательно")
    private Long testTypeId;

    private String testTypeName;
    private LocalDateTime completedDate;

    @Size(max = 2000, message = "Результат не должен превышать 2000 символов")
    private String result;

    @Size(max = 1000, message = "Референтные значения не должны превышать 1000 символов")
    private String referenceValues;

    private TestStatus status;

    public TestDTO() {}

    public TestDTO(Long orderId, Long testTypeId) {
        this.orderId = orderId;
        this.testTypeId = testTypeId;
        this.status = TestStatus.PENDING;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getTestTypeId() { return testTypeId; }
    public void setTestTypeId(Long testTypeId) { this.testTypeId = testTypeId; }

    @SuppressWarnings("unused")
    public String getTestTypeName() { return testTypeName; }
    public void setTestTypeName(String testTypeName) { this.testTypeName = testTypeName; }

    @SuppressWarnings("unused")
    public LocalDateTime getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDateTime completedDate) { this.completedDate = completedDate; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getReferenceValues() { return referenceValues; }
    public void setReferenceValues(String referenceValues) { this.referenceValues = referenceValues; }

    public TestStatus getStatus() { return status; }
    public void setStatus(TestStatus status) { this.status = status; }
}