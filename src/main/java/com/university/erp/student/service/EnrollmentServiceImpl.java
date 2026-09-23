package com.university.erp.student.service;

import com.university.erp.student.dto.EnrollmentRequest;
import com.university.erp.student.dto.EnrollmentResponse;
import com.university.erp.student.entity.Enrollment;
import com.university.erp.student.entity.Student;
import com.university.erp.student.mapper.EnrollmentMapper;
import com.university.erp.student.repository.EnrollmentRepository;
import com.university.erp.student.repository.StudentRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository, StudentRepository studentRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public List<EnrollmentResponse> findAll() {
        return enrollmentRepository.findAll().stream().map(EnrollmentMapper::toResponse).toList();
    }

    @Override
    public List<EnrollmentResponse> findByStudentId(UUID studentId) {
        getStudent(studentId);
        return enrollmentRepository.findByStudentStudentId(studentId).stream()
                .map(EnrollmentMapper::toResponse).toList();
    }

    @Override
    public EnrollmentResponse findById(UUID enrollmentId) {
        return EnrollmentMapper.toResponse(getEnrollment(enrollmentId));
    }

    @Override
    public EnrollmentResponse create(EnrollmentRequest request) {
        return EnrollmentMapper.toResponse(
                enrollmentRepository.save(EnrollmentMapper.toEntity(request, getStudent(request.studentId()))));
    }

    @Override
    public EnrollmentResponse update(UUID enrollmentId, EnrollmentRequest request) {
        Enrollment enrollment = getEnrollment(enrollmentId);
        Student student = getStudent(request.studentId());
        Enrollment replacement = EnrollmentMapper.toEntity(request, student);
        replacement.setEnrollmentId(enrollment.getEnrollmentId());
        return EnrollmentMapper.toResponse(enrollmentRepository.save(replacement));
    }

    @Override
    public void delete(UUID enrollmentId) {
        enrollmentRepository.delete(getEnrollment(enrollmentId));
    }

    private Enrollment getEnrollment(UUID enrollmentId) {
        return enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));
    }

    private Student getStudent(UUID studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
    }
}