package com.university.erp.department.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.university.erp.department.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    boolean existsByDepartmentNameIgnoreCase(String departmentName);

}