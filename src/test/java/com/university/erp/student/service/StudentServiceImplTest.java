package com.university.erp.student.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.university.erp.student.dto.StudentRequest;
import com.university.erp.student.dto.StudentResponse;
import com.university.erp.student.entity.Student;
import com.university.erp.student.repository.StudentRepository;
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
    void updateReturnsNotFoundWhenStudentDoesNotExist() {
        UUID studentId = UUID.randomUUID();
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> studentService.update(studentId, mock(StudentRequest.class)));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Student not found: " + studentId, exception.getReason());
        verify(studentRepository, never()).save(org.mockito.ArgumentMatchers.any(Student.class));
    }

    @Test
    void deleteReturnsNotFoundWhenStudentDoesNotExist() {
        UUID studentId = UUID.randomUUID();
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> studentService.delete(studentId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(studentRepository, never()).delete(org.mockito.ArgumentMatchers.any(Student.class));
    }
}
