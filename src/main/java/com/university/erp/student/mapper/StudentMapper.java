package com.university.erp.student.mapper;

import com.university.erp.student.dto.StudentRequest;
import com.university.erp.student.dto.StudentResponse;
import com.university.erp.student.entity.Student;

public final class StudentMapper {
	private StudentMapper() { }

	public static Student toEntity(StudentRequest request) {
		Student student = new Student();
		updateEntity(student, request);
		return student;
	}

	public static void updateEntity(Student student, StudentRequest request) {
		student.setEnrollmentNo(request.enrollmentNo());
		student.setFirstName(request.firstName());
		student.setMiddleName(request.middleName());
		student.setLastName(request.lastName());
		student.setGender(request.gender());
		student.setDob(request.dob());
		student.setEmail(request.email());
		student.setPhone(request.phone());
		student.setAddress(request.address());
		student.setAdmissionDate(request.admissionDate());
		if (request.currentStatus() != null) student.setCurrentStatus(request.currentStatus());
	}

	public static StudentResponse toResponse(Student student) {
		return new StudentResponse(student.getStudentId(), student.getEnrollmentNo(), student.getFirstName(),
				student.getMiddleName(), student.getLastName(), student.getGender(), student.getDob(),
				student.getEmail(), student.getPhone(), student.getAddress(), student.getAdmissionDate(),
				student.getCurrentStatus());
	}
}
