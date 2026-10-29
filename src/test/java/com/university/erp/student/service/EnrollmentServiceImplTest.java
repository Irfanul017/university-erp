package com.university.erp.student.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.university.erp.course.entity.Course;
import com.university.erp.course.repository.CourseRepository;
import com.university.erp.student.dto.EnrollmentRequest;
import com.university.erp.student.dto.EnrollmentResponse;
import com.university.erp.student.entity.Enrollment;
import com.university.erp.student.entity.Student;
import com.university.erp.student.repository.EnrollmentRepository;
import com.university.erp.student.repository.StudentRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class EnrollmentServiceImplTest {

    private EnrollmentRepository enrollmentRepository;
    private StudentRepository studentRepository;
    private CourseRepository courseRepository;
    private EnrollmentServiceImpl enrollmentService;

    @BeforeEach
    void setUp() {
        enrollmentRepository = mock(EnrollmentRepository.class);
        studentRepository = mock(StudentRepository.class);
        courseRepository = mock(CourseRepository.class);
        enrollmentService = new EnrollmentServiceImpl(enrollmentRepository, studentRepository, courseRepository);
    }

    @Test
    void findByIdReturnsNotFoundWhenEnrollmentDoesNotExist() {
        UUID enrollmentId = UUID.randomUUID();
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> enrollmentService.findById(enrollmentId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Enrollment not found: " + enrollmentId, exception.getReason());
    }

    @Test
    void updateReturnsNullWhenEnrollmentDoesNotExist() {
        UUID enrollmentId = UUID.randomUUID();
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        EnrollmentResponse response = enrollmentService.update(enrollmentId, mock(EnrollmentRequest.class));

        assertNull(response);
        verify(studentRepository, never()).findById(org.mockito.ArgumentMatchers.any(UUID.class));
        verify(enrollmentRepository, never()).save(org.mockito.ArgumentMatchers.any(Enrollment.class));
    }

    @Test
    void updateMutatesAndSavesExistingEnrollment() {
        UUID enrollmentId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        long courseId = 5L;
        Enrollment existingEnrollment = new Enrollment();
        existingEnrollment.setEnrollmentId(enrollmentId);
        Student student = new Student();
        student.setStudentId(studentId);
        student.setFirstName("Asha");
        student.setMiddleName("Ravi");
        student.setLastName("Patel");
        Course course = new Course();
        course.setId(courseId);
        course.setCourseCode("CS-BSC");
        course.setCourseName("Computer Science");
        EnrollmentRequest request = new EnrollmentRequest(
                studentId, courseId, 2, "2026-2027", 17, "B", "Regular", "Active",
                LocalDate.of(2026, 8, 1), null, null, new BigDecimal("3.75"));
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(existingEnrollment));
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.save(existingEnrollment)).thenReturn(existingEnrollment);

        EnrollmentResponse response = enrollmentService.update(enrollmentId, request);

        assertEquals(enrollmentId, response.enrollmentId());
        assertEquals(studentId, response.studentId());
        assertEquals("Asha Ravi Patel", response.studentName());
        assertEquals(courseId, response.courseId());
        assertEquals("CS-BSC", response.courseCode());
        assertEquals(course, existingEnrollment.getCourse());
        assertEquals(2, existingEnrollment.getSemester());
        assertEquals("2026-2027", existingEnrollment.getAcademicYear());
        assertEquals(Integer.valueOf(17), existingEnrollment.getRollNo());
        assertEquals("B", existingEnrollment.getDivision());
        assertEquals(new BigDecimal("3.75"), existingEnrollment.getFinalCgpa());
        verify(enrollmentRepository).save(existingEnrollment);
    }

    @Test
    void deleteReturnsNotFoundWhenEnrollmentDoesNotExist() {
        UUID enrollmentId = UUID.randomUUID();
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> enrollmentService.delete(enrollmentId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(enrollmentRepository, never()).delete(org.mockito.ArgumentMatchers.any(Enrollment.class));
    }
}
