package com.university.erp.student.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.university.erp.student.entity.Student;
import com.university.erp.student.repository.StudentRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class StudentServiceImplTest {

    private StudentRepository studentRepository;
    private StudentServiceImpl studentService;

    @BeforeEach
    void setUp() {
        studentRepository = mock(StudentRepository.class);
        studentService = new StudentServiceImpl(studentRepository);
    }

    @Test
    void findByIdReturnsNotFoundWhenStudentDoesNotExist() {
        UUID studentId = UUID.randomUUID();
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> studentService.findById(studentId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Student not found: " + studentId, exception.getReason());
    }

    @Test
    void createGeneratesIdAndDefaultsStatusWhenRequestIncludesNoIdOrStatus() {
        Student request = validStudent();
        request.setStudentId(UUID.randomUUID());
        request.setCurrentStatus(null);
        when(studentRepository.save(request)).thenAnswer(invocation -> {
            Student saved = invocation.getArgument(0);
            assertNull(saved.getStudentId());
            saved.setStudentId(UUID.randomUUID());
            return saved;
        });

        Student created = studentService.create(request);

        assertEquals("Active", created.getCurrentStatus());
    }

    @Test
    void updateReturnsNotFoundWhenStudentDoesNotExist() {
        UUID studentId = UUID.randomUUID();
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> studentService.update(studentId, validStudent()));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Student not found: " + studentId, exception.getReason());
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void updateCopiesEntityFieldsButPreservesStatusWhenRequestOmitsIt() {
        UUID studentId = UUID.randomUUID();
        Student existing = validStudent();
        existing.setStudentId(studentId);
        existing.setCurrentStatus("Inactive");
        Student request = validStudent();
        request.setFirstName("Updated");
        request.setCurrentStatus(null);
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(existing));
        when(studentRepository.save(existing)).thenReturn(existing);

        Student updated = studentService.update(studentId, request);

        assertEquals("Updated", updated.getFirstName());
        assertEquals("Inactive", updated.getCurrentStatus());
        verify(studentRepository).save(existing);
    }

    @Test
    void deleteReturnsNotFoundWhenStudentDoesNotExist() {
        UUID studentId = UUID.randomUUID();
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> studentService.delete(studentId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(studentRepository, never()).delete(any(Student.class));
    }

    @Test
    void deleteMarksStudentInactiveAndSavesWithoutRemovingIt() {
        UUID studentId = UUID.randomUUID();
        Student existing = validStudent();
        existing.setStudentId(studentId);
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(existing));
        when(studentRepository.save(existing)).thenReturn(existing);

        studentService.delete(studentId);

        assertEquals("Inactive", existing.getCurrentStatus());
        verify(studentRepository).save(existing);
        verify(studentRepository, never()).delete(any(Student.class));
    }

    @Test
    void hardDeletePhysicallyRemovesExistingStudent() {
        UUID studentId = UUID.randomUUID();
        Student existing = validStudent();
        existing.setStudentId(studentId);
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(existing));

        studentService.hardDelete(studentId);

        verify(studentRepository).delete(existing);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void hardDeleteReturnsNotFoundWhenStudentDoesNotExist() {
        UUID studentId = UUID.randomUUID();
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> studentService.hardDelete(studentId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Student not found: " + studentId, exception.getReason());
        verify(studentRepository, never()).delete(any(Student.class));
    }

    private Student validStudent() {
        Student student = new Student();
        student.setEnrollmentNo("ENR-2026-001");
        student.setFirstName("Asha");
        student.setLastName("Patel");
        student.setEmail("asha.patel@example.edu");
        student.setAdmissionDate(LocalDate.of(2026, 8, 1));
        return student;
    }
}
