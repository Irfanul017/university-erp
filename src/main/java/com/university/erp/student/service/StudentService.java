package com.university.erp.student.service;

import com.university.erp.student.entity.Student;
import java.util.List;
import java.util.UUID;

public interface StudentService {
	List<Student> findAll();
	Student findById(UUID studentId);
	Student create(Student request);
	Student update(UUID studentId, Student request);
	void delete(UUID studentId);
	void hardDelete(UUID studentId);
}
