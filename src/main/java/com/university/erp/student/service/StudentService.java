package com.university.erp.student.service;

import com.university.erp.student.dto.StudentRequest;
import com.university.erp.student.dto.StudentResponse;
import java.util.List;

public interface StudentService {

    List<StudentResponse> getAllStudents();

    StudentResponse getStudentById(Long id);

    List<StudentResponse> getStudentsByDepartment(String department);

    List<StudentResponse> getStudentsByProgram(String program);

    StudentResponse createStudent(StudentRequest request);

    StudentResponse updateStudent(Long id, StudentRequest request);

    void deleteStudent(Long id);
}
