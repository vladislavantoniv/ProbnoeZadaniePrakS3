package com.lab.repository;

import com.lab.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    @SuppressWarnings("unused")
    List<Patient> findByLastName(String lastName);
    @SuppressWarnings("unused")
    List<Patient> findByFirstName(String firstName);
    @SuppressWarnings("unused")
    List<Patient> findByBirthDate(LocalDate birthDate);
    List<Patient> findByEmail(String email);

    @Query("SELECT p FROM Patient p WHERE " +
            "(:lastName IS NULL OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
            "(:firstName IS NULL OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
            "(:middleName IS NULL OR LOWER(p.middleName) LIKE LOWER(CONCAT('%', :middleName, '%'))) AND " +
            "(:birthDate IS NULL OR p.birthDate = :birthDate)")
    List<Patient> searchPatients(@Param("lastName") String lastName,
                                 @Param("firstName") String firstName,
                                 @Param("middleName") String middleName,
                                 @Param("birthDate") LocalDate birthDate);

    @SuppressWarnings("unused")
    @Query("SELECT p FROM Patient p WHERE " +
            "(:lastName IS NULL OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
            "(:firstName IS NULL OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
            "(:middleName IS NULL OR LOWER(p.middleName) LIKE LOWER(CONCAT('%', :middleName, '%'))) AND " +
            "(:birthDate IS NULL OR p.birthDate = :birthDate)")
    Page<Patient> searchPatients(@Param("lastName") String lastName,
                                 @Param("firstName") String firstName,
                                 @Param("middleName") String middleName,
                                 @Param("birthDate") LocalDate birthDate,
                                 Pageable pageable);
}