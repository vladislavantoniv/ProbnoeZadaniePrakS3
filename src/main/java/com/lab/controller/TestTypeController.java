package com.lab.controller;

import com.lab.entity.TestType;
import com.lab.repository.TestTypeRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/test-types")
public class TestTypeController {

    private final TestTypeRepository testTypeRepository;

    public TestTypeController(TestTypeRepository testTypeRepository) {
        this.testTypeRepository = testTypeRepository;
    }

    @GetMapping
    public List<TestType> getAllTestTypes() {
        return testTypeRepository.findAll();
    }

    @GetMapping("/{id}")
    public TestType getTestTypeById(@PathVariable Long id) {
        return testTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("тип анализа с номером: " + id + "не найден"));
    }

    @PostMapping
    public TestType createTestType(@RequestBody TestType testType) {
        return testTypeRepository.save(testType);
    }

    @PutMapping("/{id}")
    public TestType updateTestType(@PathVariable Long id, @RequestBody TestType testType) {
        if (!testTypeRepository.existsById(id)) {
            throw new RuntimeException("тип анализа с номером: " + id + "не найден");
        }
        testType.setId(id);
        return testTypeRepository.save(testType);
    }

    @DeleteMapping("/{id}")
    public void deleteTestType(@PathVariable Long id) {
        if (!testTypeRepository.existsById(id)) {
            throw new RuntimeException("тип анализа с номером: " + id + "не найден");
        }
        testTypeRepository.deleteById(id);
    }

    @GetMapping("/search")
    public List<TestType> searchTestTypes(@RequestParam String name) {
        return testTypeRepository.findByName(name);
    }
}