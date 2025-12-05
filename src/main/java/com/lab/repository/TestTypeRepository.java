package com.lab.repository;

import com.lab.entity.TestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TestTypeRepository extends JpaRepository<TestType, Long> {
    List<TestType> findByCode(String code);
    List<TestType> findByName(String name);
    boolean existsByCode(String code);
}