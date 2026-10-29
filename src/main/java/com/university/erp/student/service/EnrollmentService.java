package com.university.erp.student.service;

import com.university.erp.student.entity.Enrollment;
import java.util.List;
import java.util.UUID;

public interface EnrollmentService {
	List<Enrollment> findAll();
	List<Enrollment> findByStudentId(UUID studentId);
	Enrollment findById(UUID enrollmentId);
	Enrollment create(Enrollment request);
	Enrollment update(UUID enrollmentId, Enrollment request);
	void delete(UUID enrollmentId);
}
