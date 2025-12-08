package com.lab.repository;

import com.lab.entity.Test;
import com.lab.entity.enums.TestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findByOrderId(Long orderId);
    @SuppressWarnings("unused")
    Page<Test> findByOrderId(Long orderId, Pageable pageable);
    @SuppressWarnings("unused")
    List<Test> findByTestTypeId(Long testTypeId);
    @SuppressWarnings("unused")
    Page<Test> findByStatus(TestStatus status, Pageable pageable);
    @SuppressWarnings("unused")
    List<Test> findByOrderIdAndStatus(Long orderId, TestStatus status);

    @SuppressWarnings("unused")
    default Test updateResult(Long id, String result, String referenceValues, TestStatus status) {
        Test test = findById(id).orElseThrow(() -> new RuntimeException("Тест не найден"));
        test.setResult(result);
        test.setReferenceValues(referenceValues);
        test.setStatus(status);
        if (status == TestStatus.COMPLETED) {
            test.setCompletedDate(java.time.LocalDateTime.now());
        }
        return save(test);
    }
}