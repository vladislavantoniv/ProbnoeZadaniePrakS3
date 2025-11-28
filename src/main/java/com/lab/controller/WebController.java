package com.lab.controller;

import com.lab.entity.Patient;
import com.lab.entity.Order;
import com.lab.entity.TestType;
import com.lab.entity.Test;
import com.lab.entity.enums.Gender;
import com.lab.entity.enums.OrderStatus;
import com.lab.entity.enums.TestStatus;
import com.lab.repository.PatientRepository;
import com.lab.repository.OrderRepository;
import com.lab.repository.TestTypeRepository;
import com.lab.repository.TestRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/web")
public class WebController {

    private final PatientRepository patientRepository;
    private final OrderRepository orderRepository;
    private final TestTypeRepository testTypeRepository;
    private final TestRepository testRepository;

    public WebController(PatientRepository patientRepository,
                         OrderRepository orderRepository,
                         TestTypeRepository testTypeRepository,
                         TestRepository testRepository) {
        this.patientRepository = patientRepository;
        this.orderRepository = orderRepository;
        this.testTypeRepository = testTypeRepository;
        this.testRepository = testRepository;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("patientsCount", patientRepository.count());
        model.addAttribute("ordersCount", orderRepository.count());
        model.addAttribute("testTypesCount", testTypeRepository.count());
        model.addAttribute("testsCount", testRepository.count());
        return "index";
    }

    @GetMapping("/patients")
    public String patientsPage(Model model) {
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("genders", Gender.values());
        return "patients";
    }

    @PostMapping("/patients")
    public String createPatient(@ModelAttribute Patient patient) {
        patientRepository.save(patient);
        return "redirect:/web/patients";
    }

    @GetMapping("/orders")
    public String ordersPage(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("statuses", OrderStatus.values());
        return "orders";
    }

    @PostMapping("/orders")
    public String createOrder(@RequestParam Long patientId,
                              @RequestParam String comment) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + patientId));
        Order order = new Order(patient, comment);
        orderRepository.save(order);
        return "redirect:/web/orders";
    }

    @GetMapping("/test-types")
    public String testTypesPage(Model model) {
        model.addAttribute("testTypes", testTypeRepository.findAll());
        return "test-types";
    }

    @PostMapping("/test-types")
    public String createTestType(@ModelAttribute TestType testType) {
        testTypeRepository.save(testType);
        return "redirect:/web/test-types";
    }

    @GetMapping("/tests")
    public String testsPage(Model model) {
        try {
            List<Test> tests = testRepository.findAll();
            List<Order> orders = orderRepository.findAll();
            List<TestType> testTypes = testTypeRepository.findAll();

            model.addAttribute("tests", tests);
            model.addAttribute("orders", orders);
            model.addAttribute("testTypes", testTypes);
            model.addAttribute("statuses", TestStatus.values());

            return "tests";
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping("/tests")
    public String createTest(@RequestParam Long orderId,
                             @RequestParam Long testTypeId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заявка с номером: " + orderId + "не найдена"));
        TestType testType = testTypeRepository.findById(testTypeId)
                .orElseThrow(() -> new RuntimeException("Тип анализа с номером " + testTypeId + "не найден"));
        Test test = new Test(order, testType);
        testRepository.save(test);
        return "redirect:/web/tests";
    }

    @PostMapping("/tests/{id}/result")
    public String updateTestResult(@PathVariable Long id,
                                   @RequestParam String result,
                                   @RequestParam String referenceValues) {
        Test test = testRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Анализ с номером " + id + "не найден"));
        test.setResult(result);
        test.setReferenceValues(referenceValues);
        test.setStatus(TestStatus.COMPLETED);
        test.setCompletedDate(java.time.LocalDateTime.now());
        testRepository.save(test);
        return "redirect:/web/tests";
    }
}