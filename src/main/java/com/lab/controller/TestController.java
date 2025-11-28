package com.lab.controller;

import com.lab.entity.Test;
import com.lab.repository.TestRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return testRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Test createTest(@RequestBody Test test) {
        return testRepository.save(test);
    }

    @PutMapping("/{id}")
    public Test updateTest(@PathVariable Long id, @RequestBody Test test) {
        test.setId(id);
        return testRepository.save(test);
    }

    @DeleteMapping("/{id}")
    public void deleteTest(@PathVariable Long id) {
        testRepository.deleteById(id);
    }

    @GetMapping("/order/{orderId}")
    public List<Test> getTestsByOrderId(@PathVariable Long orderId) {
        return testRepository.findByOrderId(orderId);
    }
}