package com.university.erp.student.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StudentRequest(
	@NotBlank
	String enrollmentNo,
	@NotBlank
	String firstName,
	String middleName,
	@NotBlank
	String lastName,
	String gender,
	LocalDate dob,
	@NotBlank
	@Email
	String email,
	String phone,
	String address,
	@NotNull
	LocalDate admissionDate,
	String currentStatus) {
}
