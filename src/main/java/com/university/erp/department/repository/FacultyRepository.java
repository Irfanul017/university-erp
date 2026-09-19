package com.university.erp.department.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.university.erp.department.entity.Faculty;

public interface FacultyRepository extends JpaRepository<Faculty, Integer> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByDepartmentDepartmentId(Integer departmentId);

    List<Faculty> findByDepartmentDepartmentId(Integer departmentId);

    List<Faculty> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName);

    List<Faculty> findByDesignationContainingIgnoreCase(String designation);

    List<Faculty> findBySpecializationContainingIgnoreCase(String specialization);

    @Query("""
        SELECT f
        FROM Faculty f
        JOIN FETCH f.department
        """)
    List<Faculty> findAllWithDepartment();

    @Query("""
        SELECT f
        FROM Faculty f
        JOIN FETCH f.department
        WHERE f.facultyId = :id
        """)
    Optional<Faculty> findByIdWithDepartment(@Param("id") Integer id);
}