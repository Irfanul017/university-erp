package com.university.erp.student.entity;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.university.erp.course.entity.Course;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class StudentEnrollmentEntityValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void requiresStudentFieldsAndValidEmail() {
        Student student = new Student();
        student.setEnrollmentNo("");
        student.setFirstName("");
        student.setLastName("");
        student.setEmail("not-an-email");

        Set<String> invalidFields = validator.validate(student).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertTrue(invalidFields.contains("enrollmentNo"));
        assertTrue(invalidFields.contains("firstName"));
        assertTrue(invalidFields.contains("lastName"));
        assertTrue(invalidFields.contains("email"));
        assertTrue(invalidFields.contains("admissionDate"));
    }

    @Test
    void requiresEnrollmentFieldsAndAssociations() {
        Enrollment enrollment = new Enrollment();

        Set<String> invalidFields = validator.validate(enrollment).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertTrue(invalidFields.contains("student"));
        assertTrue(invalidFields.contains("academicYear"));
        assertTrue(invalidFields.contains("rollNo"));
        assertTrue(invalidFields.contains("division"));
        assertTrue(invalidFields.contains("startDate"));
    }

    @Test
    void enrollmentReferenceObjectsAreValidatedByIdInTheServiceNotAsFullEntities() {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(new Student());
        enrollment.setCourse(new Course());
        enrollment.setAcademicYear("2026-2027");
        enrollment.setRollNo(1);
        enrollment.setDivision("A");
        enrollment.setStartDate(java.time.LocalDate.of(2026, 8, 1));

        assertTrue(validator.validate(enrollment).isEmpty());
    }
}
