package com.lab.repository;

import com.lab.entity.TestType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@SuppressWarnings("unused")
@Repository
public interface TestTypeRepository extends JpaRepository<TestType, Long> {
    @SuppressWarnings("unused")
    List<TestType> findByCode(String code);
    List<TestType> findByName(String name);
    boolean existsByCode(String code);
}