package com.university.erp.student;

import static org.assertj.core.api.Assertions.assertThat;

import com.university.erp.student.dto.EnrollmentRequest;
import com.university.erp.student.dto.StudentRequest;
import com.university.erp.student.service.EnrollmentService;
import com.university.erp.student.service.StudentService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StudentModuleIntegrationTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Test
    void shouldCreateStudentAndEnrollmentSuccessfully() {
        StudentRequest studentRequest = new StudentRequest();
        studentRequest.setRollNumber("2024-CS-101");
        studentRequest.setName("Aisha Khan");
        studentRequest.setEmail("aisha.khan@example.com");
        studentRequest.setPhone("9876543210");
        studentRequest.setAddress("Dhaka");
        studentRequest.setDateOfBirth(LocalDate.of(2003, 6, 15));
        studentRequest.setDepartment("CSE");
        studentRequest.setProgram("BSc in Computer Science");

        var savedStudent = studentService.createStudent(studentRequest);
        assertThat(savedStudent.getId()).isNotNull();
        assertThat(studentService.getStudentsByDepartment("CSE")).isNotEmpty();

        EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
        enrollmentRequest.setStudentId(savedStudent.getId());
        enrollmentRequest.setAcademicYear("2024-2025");
        enrollmentRequest.setSemester("Spring");
        enrollmentRequest.setCourseName("Data Structures");
        enrollmentRequest.setStatus("ACTIVE");

        var savedEnrollment = enrollmentService.createEnrollment(enrollmentRequest);
        assertThat(savedEnrollment.getId()).isNotNull();
        assertThat(enrollmentService.getEnrollmentsByStudentId(savedStudent.getId())).isNotEmpty();
        assertThat(enrollmentService.getActiveEnrollments()).isNotEmpty();
    }

    @Test
    void shouldReturnStudentsFilteredByProgram() {
        StudentRequest first = new StudentRequest();
        first.setRollNumber("2024-EE-201");
        first.setName("Rahim Ali");
        first.setEmail("rahim.ali@example.com");
        first.setPhone("9123456780");
        first.setAddress("Chittagong");
        first.setDateOfBirth(LocalDate.of(2002, 2, 10));
        first.setDepartment("EEE");
        first.setProgram("BSc in Electrical Engineering");

        StudentRequest second = new StudentRequest();
        second.setRollNumber("2024-EE-202");
        second.setName("Nadia Islam");
        second.setEmail("nadia.islam@example.com");
        second.setPhone("9234567890");
        second.setAddress("Sylhet");
        second.setDateOfBirth(LocalDate.of(2003, 5, 26));
        second.setDepartment("EEE");
        second.setProgram("BSc in Electrical Engineering");

        studentService.createStudent(first);
        studentService.createStudent(second);

        List<?> students = studentService.getStudentsByProgram("BSc in Electrical Engineering");
        assertThat(students).hasSizeGreaterThanOrEqualTo(2);
    }
}
