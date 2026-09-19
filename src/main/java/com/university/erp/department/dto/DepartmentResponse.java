package com.university.erp.department.dto;

import java.time.LocalDateTime;

public record DepartmentResponse(
        Integer departmentId,
        String departmentName,
        String location,
        Integer hodFacultyId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
