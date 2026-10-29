package com.university.erp.student.service;

import com.university.erp.course.entity.Course;
import com.university.erp.course.repository.CourseRepository;
import com.university.erp.student.dto.EnrollmentRequest;
import com.university.erp.student.dto.EnrollmentResponse;
import com.university.erp.student.entity.Enrollment;
import com.university.erp.student.entity.Student;
import com.university.erp.student.mapper.EnrollmentMapper;
import com.university.erp.student.repository.EnrollmentRepository;
import com.university.erp.student.repository.StudentRepository;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {
    private static final Logger logger = LoggerFactory.getLogger(EnrollmentServiceImpl.class);

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository, StudentRepository studentRepository,
            CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> findAll() {
        List<EnrollmentResponse> enrollments = enrollmentRepository.findAll().stream()
                .map(EnrollmentMapper::toResponse).toList();
        logger.info("Listed {} enrollments", enrollments.size());
        return enrollments;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> findByStudentId(UUID studentId) {
        getStudent(studentId);
        List<EnrollmentResponse> enrollments = enrollmentRepository.findByStudentStudentId(studentId).stream()
                .map(EnrollmentMapper::toResponse).toList();
        logger.info("Listed {} enrollments for student {}", enrollments.size(), studentId);
        return enrollments;
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentResponse findById(UUID enrollmentId) {
        EnrollmentResponse enrollment = EnrollmentMapper.toResponse(getEnrollment(enrollmentId));
        logger.info("Retrieved enrollment {}", enrollmentId);
        return enrollment;
    }

    @Override
    public EnrollmentResponse create(EnrollmentRequest request) {
        Course course = getCourse(request.courseId());
        Enrollment enrollment = enrollmentRepository.save(
                EnrollmentMapper.toEntity(request, getStudent(request.studentId()), course));
        logger.info("Created enrollment {} for student {}", enrollment.getEnrollmentId(), request.studentId());
        return EnrollmentMapper.toResponse(enrollment);
    }

    @Override
    public EnrollmentResponse update(UUID enrollmentId, EnrollmentRequest request) {
        Enrollment enrollment = getEnrollment(enrollmentId);
        Student student = getStudent(request.studentId());
        Course course = getCourse(request.courseId());
        EnrollmentMapper.updateEntity(enrollment, request, student, course);
        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        logger.info("Updated enrollment {}", enrollmentId);
        return EnrollmentMapper.toResponse(updatedEnrollment);
    }

    @Override
    public void delete(UUID enrollmentId) {
        enrollmentRepository.delete(getEnrollment(enrollmentId));
        logger.info("Deleted enrollment {}", enrollmentId);
    }

    private Enrollment getEnrollment(UUID enrollmentId) {
        return enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> {
                    logger.warn("Enrollment {} was not found", enrollmentId);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Enrollment not found: " + enrollmentId);
                });
    }

    private Student getStudent(UUID studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    logger.warn("Student {} was not found", studentId);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Student not found: " + studentId);
                });
    }

    private Course getCourse(Long courseId) {
        if (courseId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "courseId is required");
        }
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Course not found: " + courseId));
    }
}
