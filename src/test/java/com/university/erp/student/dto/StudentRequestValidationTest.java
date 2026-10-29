package com.university.erp.student.dto;

import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;

class StudentRequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void requiresStudentFieldsAndValidEmail() {
        StudentRequest request = new StudentRequest(
                "", "", null, "", null, null, "not-an-email", null, null, null, null);

        Set<String> invalidFields = validator.validate(request).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(java.util.stream.Collectors.toSet());

        assertTrue(invalidFields.contains("enrollmentNo"));
        assertTrue(invalidFields.contains("firstName"));
        assertTrue(invalidFields.contains("lastName"));
        assertTrue(invalidFields.contains("email"));
        assertTrue(invalidFields.contains("admissionDate"));
    }
}
