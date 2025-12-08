package com.lab.controller;

import com.lab.dto.TestDTO;
import com.lab.dto.PageResponse;
import com.lab.service.TestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@SuppressWarnings("unused")
@RestController
@RequestMapping("/api/tests")
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping
    public PageResponse<TestDTO> getAllTests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return testService.getAllTests(page, size, sortBy, direction);
    }

    @GetMapping("/{id}")
    public TestDTO getTestById(@PathVariable Long id) {
        return testService.getTestById(id);
    }

    @PostMapping
    public ResponseEntity<TestDTO> createTest(@Valid @RequestBody TestDTO testDTO) {
        TestDTO createdTest = testService.createTest(testDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTest);
    }

    @PutMapping("/{id}")
    public TestDTO updateTest(@PathVariable Long id, @Valid @RequestBody TestDTO testDTO) {
        return testService.updateTest(id, testDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTest(@PathVariable Long id) {
        testService.deleteTest(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/order/{orderId}")
    public PageResponse<TestDTO> getTestsByOrderId(
            @PathVariable Long orderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return testService.getTestsByOrderId(orderId, page, size, sortBy, direction);
    }

    @PutMapping("/{id}/result")
    public TestDTO updateTestResult(@PathVariable Long id, @RequestBody UpdateResultRequest request) {
        return testService.updateTestResult(id, request.getResult(), request.getReferenceValues());
    }
    static class UpdateResultRequest {
        private String result;
        private String referenceValues;

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getReferenceValues() {
            return referenceValues;
        }

        public void setReferenceValues(String referenceValues) {
            this.referenceValues = referenceValues;
        }
    }
}