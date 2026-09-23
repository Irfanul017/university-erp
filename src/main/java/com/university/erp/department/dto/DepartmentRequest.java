package com.university.erp.department.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepartmentRequest(
        @NotBlank(message = "Department name is required")
        @Size(max = 100, message = "Department name must be at most 100 characters")
        String departmentName,

        @Size(max = 100, message = "Location must be at most 100 characters")
        String location
) {}
