package com.university.erp.student.service;

import com.university.erp.student.dto.EnrollmentRequest;
import com.university.erp.student.dto.EnrollmentResponse;
import com.university.erp.student.entity.Enrollment;
import com.university.erp.student.entity.Student;
import com.university.erp.student.mapper.EnrollmentMapper;
import com.university.erp.student.repository.EnrollmentRepository;
import com.university.erp.student.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository, StudentRepository studentRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public List<EnrollmentResponse> getAllEnrollments() {
        return enrollmentRepository.findAll().stream()
                .map(EnrollmentMapper::toResponse)
                .toList();
    }

    @Override
    public EnrollmentResponse getEnrollmentById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found with id: " + id));
        return EnrollmentMapper.toResponse(enrollment);
    }

    @Override
    public List<EnrollmentResponse> getEnrollmentsByStudentId(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(EnrollmentMapper::toResponse)
                .toList();
    }

    @Override
    public List<EnrollmentResponse> getActiveEnrollments() {
        return enrollmentRepository.findAll().stream()
                .filter(enrollment -> "ACTIVE".equalsIgnoreCase(enrollment.getStatus()))
                .map(EnrollmentMapper::toResponse)
                .toList();
    }

    @Override
    public EnrollmentResponse createEnrollment(EnrollmentRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + request.getStudentId()));
        Enrollment enrollment = EnrollmentMapper.toEntity(request, student);
        return EnrollmentMapper.toResponse(enrollmentRepository.save(enrollment));
    }

    @Override
    public EnrollmentResponse updateEnrollment(Long id, EnrollmentRequest request) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found with id: " + id));
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + request.getStudentId()));
        EnrollmentMapper.updateEntity(enrollment, request, student);
        return EnrollmentMapper.toResponse(enrollmentRepository.save(enrollment));
    }

    @Override
    public void deleteEnrollment(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found with id: " + id));
        enrollmentRepository.delete(enrollment);
    }
}
