package com.lab.service.impl;

import com.lab.dto.TestTypeDTO;
import com.lab.dto.PageResponse;
import com.lab.entity.TestType;
import com.lab.exception.ResourceAlreadyExistsException;
import com.lab.exception.ResourceNotFoundException;
import com.lab.repository.TestTypeRepository;
import com.lab.service.TestTypeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@SuppressWarnings("unused")
@Service
@Transactional
public class TestTypeServiceImpl implements TestTypeService {

    private final TestTypeRepository testTypeRepository;

    public TestTypeServiceImpl(TestTypeRepository testTypeRepository) {
        this.testTypeRepository = testTypeRepository;
    }

    @Override
    public PageResponse<TestTypeDTO> getAllTestTypes(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TestType> testTypePage = testTypeRepository.findAll(pageable);

        List<TestTypeDTO> content = testTypePage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                testTypePage.getNumber(),
                testTypePage.getSize(),
                testTypePage.getTotalElements(),
                testTypePage.getTotalPages(),
                testTypePage.isLast()
        );
    }

    @Override
    public TestTypeDTO getTestTypeById(Long id) {
        TestType testType = testTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Тип анализа", "ID", id));
        return convertToDTO(testType);
    }

    @Override
    public TestTypeDTO createTestType(TestTypeDTO testTypeDTO) {
        if (testTypeRepository.existsByCode(testTypeDTO.getCode())) {
            throw new ResourceAlreadyExistsException("Тип анализа", "код", testTypeDTO.getCode());
        }

        TestType testType = convertToEntity(testTypeDTO);
        TestType savedTestType = testTypeRepository.save(testType);
        return convertToDTO(savedTestType);
    }

    @Override
    public TestTypeDTO updateTestType(Long id, TestTypeDTO testTypeDTO) {
        if (!testTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Тип анализа", "ID", id);
        }
        List<TestType> testTypesWithCode = testTypeRepository.findByCode(testTypeDTO.getCode());
        if (testTypesWithCode != null && !testTypesWithCode.isEmpty()) {
            boolean codeBelongsToOtherTestType = testTypesWithCode.stream()
                    .anyMatch(tt -> !tt.getId().equals(id));
            if (codeBelongsToOtherTestType) {
                throw new ResourceAlreadyExistsException("Тип анализа", "код", testTypeDTO.getCode());
            }
        }

        TestType testType = convertToEntity(testTypeDTO);
        testType.setId(id);
        TestType updatedTestType = testTypeRepository.save(testType);
        return convertToDTO(updatedTestType);
    }

    @Override
    public void deleteTestType(Long id) {
        if (!testTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Тип анализа", "ID", id);
        }
        TestType testType = testTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Тип анализа", "ID", id));

        if (!testType.getTests().isEmpty()) {
            throw new com.lab.exception.BusinessLogicException(
                    "Нельзя удалить тип анализа."
            );
        }

        testTypeRepository.deleteById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return testTypeRepository.existsByCode(code);
    }

    private TestTypeDTO convertToDTO(TestType testType) {
        TestTypeDTO dto = new TestTypeDTO();
        dto.setId(testType.getId());
        dto.setName(testType.getName());
        dto.setCode(testType.getCode());
        dto.setDescription(testType.getDescription());
        dto.setPrice(testType.getPrice());
        return dto;
    }

    private TestType convertToEntity(TestTypeDTO dto) {
        return new TestType(
                dto.getName(),
                dto.getCode(),
                dto.getDescription(),
                dto.getPrice()
        );
    }
}