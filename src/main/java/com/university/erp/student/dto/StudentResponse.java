package com.university.erp.student.dto;

import java.time.LocalDate;
import java.util.UUID;

public record StudentResponse(
	UUID studentId,
	String enrollmentNo,
	String firstName,
	String middleName,
	String lastName,
	String gender,
	LocalDate dob,
	String email,
	String phone,
	String address,
	LocalDate admissionDate,
	String currentStatus) {
}
