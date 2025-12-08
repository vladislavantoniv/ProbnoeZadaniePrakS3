// file name: TestServiceImpl.java
package com.lab.service.impl;

import com.lab.dto.TestDTO;
import com.lab.dto.PageResponse;
import com.lab.entity.Test;
import com.lab.entity.Order;
import com.lab.entity.TestType;
import com.lab.entity.enums.TestStatus;
import com.lab.exception.ResourceNotFoundException;
import com.lab.exception.BusinessLogicException;
import com.lab.repository.TestRepository;
import com.lab.repository.OrderRepository;
import com.lab.repository.TestTypeRepository;
import com.lab.service.TestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TestServiceImpl implements TestService {

    private final TestRepository testRepository;
    private final OrderRepository orderRepository;
    private final TestTypeRepository testTypeRepository;

    public TestServiceImpl(TestRepository testRepository,
                           OrderRepository orderRepository,
                           TestTypeRepository testTypeRepository) {
        this.testRepository = testRepository;
        this.orderRepository = orderRepository;
        this.testTypeRepository = testTypeRepository;
    }

    @Override
    public PageResponse<TestDTO> getAllTests(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Test> testPage = testRepository.findAll(pageable);

        List<TestDTO> content = testPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                testPage.getNumber(),
                testPage.getSize(),
                testPage.getTotalElements(),
                testPage.getTotalPages(),
                testPage.isLast()
        );
    }

    @Override
    public TestDTO getTestById(Long id) {
        Test test = testRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Анализ", "ID", id));
        return convertToDTO(test);
    }

    @Override
    public TestDTO createTest(TestDTO testDTO) {
        try {
            Order order = orderRepository.findById(testDTO.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Заявка", "ID", testDTO.getOrderId()));

            TestType testType = testTypeRepository.findById(testDTO.getTestTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Тип анализа", "ID", testDTO.getTestTypeId()));

            // Проверка, не существует ли уже такой тест в заявке
            List<Test> existingTests = testRepository.findByOrderId(testDTO.getOrderId());
            boolean duplicateTest = existingTests.stream()
                    .anyMatch(t -> t.getTestType().getId().equals(testDTO.getTestTypeId()));

            if (duplicateTest) {
                throw new BusinessLogicException(
                        String.format("Анализ типа '%s' уже существует в заявке №%d",
                                testType.getName(), testDTO.getOrderId())
                );
            }

            Test test = new Test(order, testType);
            Test savedTest = testRepository.save(test);
            return convertToDTO(savedTest);
        } catch (ResourceNotFoundException | BusinessLogicException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessLogicException("Ошибка при создании анализа: " + e.getMessage(), e);
        }
    }

    @Override
    public TestDTO updateTest(Long id, TestDTO testDTO) {
        try {
            if (!testRepository.existsById(id)) {
                throw new ResourceNotFoundException("Анализ", "ID", id);
            }

            Order order = orderRepository.findById(testDTO.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Заявка", "ID", testDTO.getOrderId()));

            TestType testType = testTypeRepository.findById(testDTO.getTestTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Тип анализа", "ID", testDTO.getTestTypeId()));

            Test test = new Test(order, testType);
            test.setId(id);
            test.setResult(testDTO.getResult());
            test.setReferenceValues(testDTO.getReferenceValues());
            test.setStatus(testDTO.getStatus());

            Test updatedTest = testRepository.save(test);
            return convertToDTO(updatedTest);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessLogicException("Ошибка при обновлении анализа: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteTest(Long id) {
        if (!testRepository.existsById(id)) {
            throw new ResourceNotFoundException("Анализ", "ID", id);
        }
        testRepository.deleteById(id);
    }

    @Override
    public PageResponse<TestDTO> getTestsByOrderId(Long orderId, int page, int size, String sortBy, String direction) {
        try {
            // Проверка существования заявки
            if (!orderRepository.existsById(orderId)) {
                throw new ResourceNotFoundException("Заявка", "ID", orderId);
            }

            Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);

            List<Test> tests = testRepository.findByOrderId(orderId);

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), tests.size());

            if (start > tests.size()) {
                start = tests.size();
                end = tests.size();
            }

            List<TestDTO> content = tests.subList(start, end).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return new PageResponse<>(
                    content,
                    page,
                    size,
                    tests.size(),
                    (int) Math.ceil((double) tests.size() / size),
                    end >= tests.size()
            );
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessLogicException("Ошибка при получении анализов заявки: " + e.getMessage(), e);
        }
    }

    @Override
    public TestDTO updateTestResult(Long id, String result, String referenceValues) {
        try {
            Test test = testRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Анализ", "ID", id));

            if (result == null || result.trim().isEmpty()) {
                throw new com.lab.exception.ValidationException("Результат анализа не может быть пустым");
            }

            test.setResult(result.trim());

            if (referenceValues != null) {
                test.setReferenceValues(referenceValues.trim());
            }

            test.setStatus(TestStatus.COMPLETED);

            Test updatedTest = testRepository.save(test);
            return convertToDTO(updatedTest);
        } catch (ResourceNotFoundException | com.lab.exception.ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessLogicException("Ошибка при обновлении результата анализа: " + e.getMessage(), e);
        }
    }

    private TestDTO convertToDTO(Test test) {
        TestDTO dto = new TestDTO();
        dto.setId(test.getId());
        dto.setOrderId(test.getOrder().getId());
        dto.setTestTypeId(test.getTestType().getId());
        dto.setTestTypeName(test.getTestType().getName());
        dto.setCompletedDate(test.getCompletedDate());
        dto.setResult(test.getResult());
        dto.setReferenceValues(test.getReferenceValues());
        dto.setStatus(test.getStatus());
        return dto;
    }
}