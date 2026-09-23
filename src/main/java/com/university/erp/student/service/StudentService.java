package com.university.erp.student.service;

import com.university.erp.student.dto.StudentRequest;
import com.university.erp.student.dto.StudentResponse;
import java.util.List;
import java.util.UUID;

public interface StudentService {
	List<StudentResponse> findAll();
	StudentResponse findById(UUID studentId);
	StudentResponse create(StudentRequest request);
	StudentResponse update(UUID studentId, StudentRequest request);
	void delete(UUID studentId);
}
