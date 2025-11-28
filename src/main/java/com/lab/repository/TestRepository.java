package com.lab.repository;

import com.lab.entity.Test;
import com.lab.entity.enums.TestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findByOrderId(Long orderId);
    List<Test> findByTestTypeId(Long testTypeId);
    List<Test> findByStatus(TestStatus status);
    List<Test> findByOrderIdAndStatus(Long orderId, TestStatus status);

    // Новый метод для обновления результата теста
    default Test updateResult(Long id, String result, String referenceValues, TestStatus status) {
        Test test = findById(id).orElseThrow(() -> new RuntimeException("Test not found"));
        test.setResult(result);
        test.setReferenceValues(referenceValues);
        test.setStatus(status);
        if (status == TestStatus.COMPLETED) {
            test.setCompletedDate(java.time.LocalDateTime.now());
        }
        return save(test);
    }
}