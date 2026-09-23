package com.university.erp.student.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record EnrollmentResponse(
	UUID enrollmentId,
	UUID studentId,
	Integer semester,
	String academicYear,
	Integer rollNo,
	String division,
	String admissionType,
	String enrollmentStatus,
	LocalDate startDate,
	LocalDate endDate,
	String exitReason,
	BigDecimal finalCgpa) {
}
