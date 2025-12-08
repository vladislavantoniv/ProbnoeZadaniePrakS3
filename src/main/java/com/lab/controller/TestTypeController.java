package com.lab.controller;

import com.lab.dto.TestTypeDTO;
import com.lab.dto.PageResponse;
import com.lab.service.TestTypeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@SuppressWarnings("unused")
@RestController
@RequestMapping("/api/test-types")
public class TestTypeController {

    private final TestTypeService testTypeService;

    public TestTypeController(TestTypeService testTypeService) {
        this.testTypeService = testTypeService;
    }

    @GetMapping
    public PageResponse<TestTypeDTO> getAllTestTypes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return testTypeService.getAllTestTypes(page, size, sortBy, direction);
    }

    @GetMapping("/{id}")
    public TestTypeDTO getTestTypeById(@PathVariable Long id) {
        return testTypeService.getTestTypeById(id);
    }

    @PostMapping
    public ResponseEntity<TestTypeDTO> createTestType(@Valid @RequestBody TestTypeDTO testTypeDTO) {
        TestTypeDTO createdTestType = testTypeService.createTestType(testTypeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTestType);
    }

    @PutMapping("/{id}")
    public TestTypeDTO updateTestType(@PathVariable Long id, @Valid @RequestBody TestTypeDTO testTypeDTO) {
        return testTypeService.updateTestType(id, testTypeDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestType(@PathVariable Long id) {
        testTypeService.deleteTestType(id);
        return ResponseEntity.ok().build();
    }
}