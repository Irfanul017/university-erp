package com.university.erp.student.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EnrollmentRequest(
	@NotNull
	UUID studentId,
	@NotNull
	Long courseId,
	Integer semester,
	@NotBlank
	String academicYear,
	@NotNull
	Integer rollNo,
	@NotBlank
	String division,
	String admissionType,
	String enrollmentStatus,
	@NotNull
	LocalDate startDate,
	LocalDate endDate,
	String exitReason,
	BigDecimal finalCgpa) {
}
