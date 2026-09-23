package com.university.erp.student.service;

import com.university.erp.student.dto.EnrollmentRequest;
import com.university.erp.student.dto.EnrollmentResponse;
import java.util.List;
import java.util.UUID;

public interface EnrollmentService {
	List<EnrollmentResponse> findAll();
	List<EnrollmentResponse> findByStudentId(UUID studentId);
	EnrollmentResponse findById(UUID enrollmentId);
	EnrollmentResponse create(EnrollmentRequest request);
	EnrollmentResponse update(UUID enrollmentId, EnrollmentRequest request);
	void delete(UUID enrollmentId);
}
