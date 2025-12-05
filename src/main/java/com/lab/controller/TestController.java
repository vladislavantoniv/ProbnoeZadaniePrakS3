package com.lab.controller;

import com.lab.entity.Test;
import com.lab.entity.enums.TestStatus;
import com.lab.repository.TestRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tests")
public class TestController {

    private final TestRepository testRepository;

    public TestController(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    @GetMapping
    public List<Test> getAllTests() {
        return testRepository.findAll();
    }

    @GetMapping("/{id}")
    public Test getTestById(@PathVariable Long id) {
        return testRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Анализ с номером " + id + "не найден"));
    }

    @PostMapping
    public Test createTest(@RequestBody Test test) {
        return testRepository.save(test);
    }

    @PutMapping("/{id}")
    public Test updateTest(@PathVariable Long id, @RequestBody Test test) {
        if (!testRepository.existsById(id)) {
            throw new RuntimeException("Анализ с номером " + id + "не найден");
        }
        test.setId(id);
        return testRepository.save(test);
    }

    @DeleteMapping("/{id}")
    public void deleteTest(@PathVariable Long id) {
        if (!testRepository.existsById(id)) {
            throw new RuntimeException("Анализ с номером " + id + "не найден");
        }
        testRepository.deleteById(id);
    }

    @GetMapping("/order/{orderId}")
    public List<Test> getTestsByOrderId(@PathVariable Long orderId) {
        return testRepository.findByOrderId(orderId);
    }

    @PutMapping("/{id}/result")
    public Test updateTestResult(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String result = request.get("result");
        String referenceValues = request.get("referenceValues");

        return testRepository.updateResult(id, result, referenceValues, TestStatus.COMPLETED);
    }

    @GetMapping("/status/{status}")
    public List<Test> getTestsByStatus(@PathVariable String status) {
        TestStatus testStatus;

            testStatus = TestStatus.valueOf(status.toUpperCase());
        return testRepository.findByStatus(testStatus);
    }
}