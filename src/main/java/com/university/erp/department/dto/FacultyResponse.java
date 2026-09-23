package com.university.erp.department.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record FacultyResponse(
        Integer facultyId,
        String firstName,
        String lastName,
        String email,
        String phone,
        LocalDate dob,
        String designation,
        String qualification,
        String specialization,
        LocalDate joiningDate,
        String status,
        Integer departmentId,
        String departmentName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

