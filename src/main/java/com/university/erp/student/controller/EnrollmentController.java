package com.university.erp.student.controller;

import com.university.erp.student.dto.EnrollmentRequest;
import com.university.erp.student.dto.EnrollmentResponse;
import com.university.erp.student.service.EnrollmentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping("/enrollments")
    public List<EnrollmentResponse> getAllEnrollments() {
        return enrollmentService.getAllEnrollments();
    }

    @GetMapping("/enrollments/active")
    public List<EnrollmentResponse> getActiveEnrollments() {
        return enrollmentService.getActiveEnrollments();
    }

    @GetMapping("/students/{studentId}/enrollments")
    public List<EnrollmentResponse> getEnrollmentsByStudentId(@PathVariable Long studentId) {
        return enrollmentService.getEnrollmentsByStudentId(studentId);
    }

    @GetMapping("/enrollments/{id}")
    public ResponseEntity<EnrollmentResponse> getEnrollmentById(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(id));
    }

    @PostMapping("/enrollments")
    public ResponseEntity<EnrollmentResponse> createEnrollment(@Valid @RequestBody EnrollmentRequest request) {
        EnrollmentResponse response = enrollmentService.createEnrollment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/enrollments/{id}")
    public ResponseEntity<EnrollmentResponse> updateEnrollment(@PathVariable Long id, @Valid @RequestBody EnrollmentRequest request) {
        return ResponseEntity.ok(enrollmentService.updateEnrollment(id, request));
    }

    @DeleteMapping("/enrollments/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
        return ResponseEntity.noContent().build();
    }
}
