package com.university.erp.student.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.university.erp.course.entity.Course;
import com.university.erp.course.repository.CourseRepository;
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
    void updateReturnsNotFoundWhenEnrollmentDoesNotExist() {
        UUID enrollmentId = UUID.randomUUID();
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> enrollmentService.update(enrollmentId, validRequest(new Student(), new Course())));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Enrollment not found: " + enrollmentId, exception.getReason());
        verify(studentRepository, never()).findById(any(UUID.class));
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    void updateResolvesAssociationsAndCopiesEntityFields() {
        UUID enrollmentId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        long courseId = 5L;
        Enrollment existing = new Enrollment();
        existing.setEnrollmentId(enrollmentId);
        existing.setEnrollmentStatus("Pending");
        Student student = new Student();
        student.setStudentId(studentId);
        student.setFirstName("Asha");
        student.setMiddleName("Ravi");
        student.setLastName("Patel");
        Course course = new Course();
        course.setId(courseId);
        course.setCourseCode("CS-BSC");
        course.setCourseName("Computer Science");
        Enrollment request = validRequest(student, course);
        request.setSemester(2);
        request.setAcademicYear("2026-2027");
        request.setRollNo(17);
        request.setDivision("B");
        request.setAdmissionType("Regular");
        request.setEnrollmentStatus(null);
        request.setStartDate(LocalDate.of(2026, 8, 1));
        request.setFinalCgpa(new BigDecimal("3.75"));
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(existing));
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.save(existing)).thenReturn(existing);

        Enrollment updated = enrollmentService.update(enrollmentId, request);

        assertEquals(enrollmentId, updated.getEnrollmentId());
        assertEquals(student, updated.getStudent());
        assertEquals(course, updated.getCourse());
        assertEquals(2, updated.getSemester());
        assertEquals("2026-2027", updated.getAcademicYear());
        assertEquals(Integer.valueOf(17), updated.getRollNo());
        assertEquals("B", updated.getDivision());
        assertEquals("Pending", updated.getEnrollmentStatus());
        assertEquals(new BigDecimal("3.75"), updated.getFinalCgpa());
        verify(enrollmentRepository).save(existing);
    }

    @Test
    void createRequiresExistingStudentAndCourseAndDefaultsStatus() {
        UUID studentId = UUID.randomUUID();
        long courseId = 7L;
        Student student = new Student();
        student.setStudentId(studentId);
        Course course = new Course();
        course.setId(courseId);
        Enrollment request = validRequest(student, course);
        request.setEnrollmentId(UUID.randomUUID());
        request.setEnrollmentStatus(null);
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.save(request)).thenAnswer(invocation -> {
            Enrollment saved = invocation.getArgument(0);
            saved.setEnrollmentId(UUID.randomUUID());
            return saved;
        });

        Enrollment created = enrollmentService.create(request);

        assertEquals(student, created.getStudent());
        assertEquals(course, created.getCourse());
        assertEquals("Active", created.getEnrollmentStatus());
    }

    @Test
    void createReturnsBadRequestWhenStudentReferenceHasNoId() {
        Enrollment request = validRequest(new Student(), new Course());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> enrollmentService.create(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void createReturnsBadRequestWhenCourseReferenceIsMissing() {
        UUID studentId = UUID.randomUUID();
        Student student = new Student();
        student.setStudentId(studentId);
        Enrollment request = validRequest(student, null);
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> enrollmentService.create(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("course is required", exception.getReason());
    }

    @Test
    void deleteReturnsNotFoundWhenEnrollmentDoesNotExist() {
        UUID enrollmentId = UUID.randomUUID();
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> enrollmentService.delete(enrollmentId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(enrollmentRepository, never()).delete(any(Enrollment.class));
    }

    private Enrollment validRequest(Student student, Course course) {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setAcademicYear("2026-2027");
        enrollment.setRollNo(1);
        enrollment.setDivision("A");
        enrollment.setStartDate(LocalDate.of(2026, 8, 1));
        return enrollment;
    }
}
