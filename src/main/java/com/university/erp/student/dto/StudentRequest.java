package com.university.erp.student.dto;

import java.time.LocalDate;

public record StudentRequest(
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
