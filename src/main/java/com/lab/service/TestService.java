package com.lab.service;

import com.lab.dto.TestDTO;
import com.lab.dto.PageResponse;

public interface TestService {
    PageResponse<TestDTO> getAllTests(int page, int size, String sortBy, String direction);
    TestDTO getTestById(Long id);
    TestDTO createTest(TestDTO testDTO);
    TestDTO updateTest(Long id, TestDTO testDTO);
    void deleteTest(Long id);
    PageResponse<TestDTO> getTestsByOrderId(Long orderId, int page, int size, String sortBy, String direction);
    TestDTO updateTestResult(Long id, String result, String referenceValues);
}