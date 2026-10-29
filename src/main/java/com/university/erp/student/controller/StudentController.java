package com.university.erp.student.controller;

import com.university.erp.student.entity.Enrollment;
import com.university.erp.student.entity.Student;
import com.university.erp.student.service.EnrollmentService;
import com.university.erp.student.service.StudentService;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Students")
public class StudentController {

	private final StudentService studentService;
	private final EnrollmentService enrollmentService;

	public StudentController(StudentService studentService, EnrollmentService enrollmentService) {
		this.studentService = studentService;
		this.enrollmentService = enrollmentService;
	}

	@GetMapping
	@Operation(summary = "List students")
	public List<Student> findAll() { return studentService.findAll(); }

	@GetMapping("/{studentId}")
	@Operation(summary = "Get a student")
	public Student findById(@PathVariable UUID studentId) { return studentService.findById(studentId); }

	@GetMapping("/{studentId}/enrollments")
	@Operation(summary = "List enrollments for a student")
	public List<Enrollment> findEnrollments(@PathVariable UUID studentId) {
		return enrollmentService.findByStudentId(studentId);
	}

	@PostMapping
	@Operation(summary = "Create a student")
	public ResponseEntity<Student> create(@Valid @RequestBody Student request) {
		Student response = studentService.create(request);
		return ResponseEntity.created(URI.create("/api/students/" + response.getStudentId())).body(response);
	}

	@PutMapping("/{studentId}")
	@Operation(summary = "Update a student")
	public Student update(@PathVariable UUID studentId, @Valid @RequestBody Student request) {
		return studentService.update(studentId, request);
	}

	@DeleteMapping("/{studentId}")
	@Operation(summary = "Delete a student")
	public ResponseEntity<Void> delete(@PathVariable UUID studentId) {
		studentService.delete(studentId);
		return ResponseEntity.noContent().build();
	}
}
