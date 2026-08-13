package com.university.erp.student.service;

import com.university.erp.student.dto.EnrollmentRequest;
import com.university.erp.student.dto.EnrollmentResponse;
import java.util.List;

public interface EnrollmentService {

    List<EnrollmentResponse> getAllEnrollments();

    EnrollmentResponse getEnrollmentById(Long id);

    List<EnrollmentResponse> getEnrollmentsByStudentId(Long studentId);

    List<EnrollmentResponse> getActiveEnrollments();

    EnrollmentResponse createEnrollment(EnrollmentRequest request);

    EnrollmentResponse updateEnrollment(Long id, EnrollmentRequest request);

    void deleteEnrollment(Long id);
}
