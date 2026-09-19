package com.university.erp.department.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FacultyRequest(
        @NotBlank @Size(max = 50)
        String firstName,

        @NotBlank @Size(max = 50)
        String lastName,

        @NotBlank @Email @Size(max = 100)
        String email,

        @Size(max = 15)
        String phone,

        LocalDate dob,

        @Size(max = 50)
        String designation,

        @Size(max = 100)
        String qualification,

        @Size(max = 100)
        String specialization,

        LocalDate joiningDate,

        @Size(max = 20)
        String status,

        @NotNull
        Integer departmentId
) {}

