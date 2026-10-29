package com.university.erp.student.entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.university.erp.course.entity.Course;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class StudentEnrollmentJsonTest {

    private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

    @Test
    void serializesEntityRelationshipsWithoutCircularStudentEnrollmentJson() throws Exception {
        Student student = new Student();
        student.setStudentId(UUID.randomUUID());
        student.setFirstName("Asha");
        student.setLastName("Patel");

        Course course = new Course();
        course.setId(7L);
        course.setCourseCode("CS-BSC");
        course.setCourseName("Computer Science");

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setAcademicYear("2026-2027");
        enrollment.setRollNo(1);
        enrollment.setDivision("A");
        student.setEnrollments(List.of(enrollment));

        String studentJson = objectMapper.writeValueAsString(student);
        String enrollmentJson = objectMapper.writeValueAsString(enrollment);

        assertFalse(studentJson.contains("\"enrollments\""));
        assertTrue(enrollmentJson.contains("\"student\""));
        assertTrue(enrollmentJson.contains("\"course\""));
        assertTrue(enrollmentJson.contains("\"studentName\":\"Asha Patel\""));
    }
}
