package com.lab.service;

import com.lab.dto.TestTypeDTO;
import com.lab.dto.PageResponse;
@SuppressWarnings("unused")
public interface TestTypeService {
    PageResponse<TestTypeDTO> getAllTestTypes(int page, int size, String sortBy, String direction);
    TestTypeDTO getTestTypeById(Long id);
    TestTypeDTO createTestType(TestTypeDTO testTypeDTO);
    TestTypeDTO updateTestType(Long id, TestTypeDTO testTypeDTO);
    void deleteTestType(Long id);
    boolean existsByCode(String code);
}