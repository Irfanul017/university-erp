package com.university.erp.student.service;

import com.university.erp.course.entity.Course;
import com.university.erp.course.repository.CourseRepository;
import com.university.erp.student.entity.Enrollment;
import com.university.erp.student.entity.Student;
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
    public List<Enrollment> findAll() {
        List<Enrollment> enrollments = enrollmentRepository.findAll();
        logger.info("Listed {} enrollments", enrollments.size());
        return enrollments;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Enrollment> findByStudentId(UUID studentId) {
        getStudent(studentId);
        List<Enrollment> enrollments = enrollmentRepository.findByStudentStudentId(studentId);
        logger.info("Listed {} enrollments for student {}", enrollments.size(), studentId);
        return enrollments;
    }

    @Override
    @Transactional(readOnly = true)
    public Enrollment findById(UUID enrollmentId) {
        Enrollment enrollment = getEnrollment(enrollmentId);
        logger.info("Retrieved enrollment {}", enrollmentId);
        return enrollment;
    }

    @Override
    @Transactional
    public Enrollment create(Enrollment request) {
        Student student = getStudentId(request);
        Course course = getCourseId(request);
        request.setEnrollmentId(null);
        request.setStudent(student);
        request.setCourse(course);
        if (request.getEnrollmentStatus() == null) {
            request.setEnrollmentStatus("Active");
        }
        Enrollment enrollment = enrollmentRepository.save(request);
        logger.info("Created enrollment {} for student {}", enrollment.getEnrollmentId(), student.getStudentId());
        return enrollment;
    }

    @Override
    @Transactional
    public Enrollment update(UUID enrollmentId, Enrollment request) {
        Enrollment enrollment = getEnrollment(enrollmentId);
        Student student = getStudentId(request);
        Course course = getCourseId(request);
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setSemester(request.getSemester());
        enrollment.setAcademicYear(request.getAcademicYear());
        enrollment.setRollNo(request.getRollNo());
        enrollment.setDivision(request.getDivision());
        enrollment.setAdmissionType(request.getAdmissionType());
        if (request.getEnrollmentStatus() != null) {
            enrollment.setEnrollmentStatus(request.getEnrollmentStatus());
        }
        enrollment.setStartDate(request.getStartDate());
        enrollment.setEndDate(request.getEndDate());
        enrollment.setExitReason(request.getExitReason());
        enrollment.setFinalCgpa(request.getFinalCgpa());
        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        logger.info("Updated enrollment {}", enrollmentId);
        return updatedEnrollment;
    }

    @Override
    @Transactional
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
        if (studentId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "studentId is required");
        }
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

    private Student getStudentId(Enrollment enrollment) {
        if (enrollment.getStudent() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "student is required");
        }
        return getStudent(enrollment.getStudent().getStudentId());
    }

    private Course getCourseId(Enrollment enrollment) {
        if (enrollment.getCourse() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "course is required");
        }
        return getCourse(enrollment.getCourse().getId());
    }
}
